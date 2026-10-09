
package com.diaglab.portal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diaglab.portal.entity.User;
import com.diaglab.portal.repository.UserRepository;
import com.diaglab.portal.service.ActivityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository contextRepository,
            UserRepository userRepository,
            ActivityService activityService
    ) {
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    public record LoginRequest(
            @NotBlank @Size(max = 50) String username,
            @NotBlank @Size(max = 100) String password
    ) {
    }

    public record LoginResponse(
            String message,
            String username,
            String role
    ) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.username(),
                                    request.password()
                            )
                    );

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Persist authentication in the HTTP session.
            contextRepository.saveContext(
                    context,
                    servletRequest,
                    servletResponse
            );

            User user = userRepository
                    .findByUsernameIgnoreCase(authentication.getName())
                    .orElseThrow();

            String role = user.getRole().name();

            activityService.record(
                    "USER_LOGIN",
                    user.getUsername(),
                    "Successful login"
            );

            return ResponseEntity.ok(
                    new LoginResponse(
                            "Login successful",
                            user.getUsername(),
                            role
                    )
            );

        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return userRepository
                .findByUsernameIgnoreCase(authentication.getName())
                .<ResponseEntity<?>>map(user ->
                        ResponseEntity.ok(
                                new LoginResponse(
                                        "Authenticated",
                                        user.getUsername(),
                                        user.getRole().name()
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .build()
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // Capture the username before clearing the security context.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = null;

        if (authentication != null
                && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            username = authentication.getName();
        }

        // Record the logout before invalidating the session.
        if (username != null) {
            activityService.record(
                    "USER_LOGOUT",
                    username,
                    "Successful logout"
            );
        }

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok("Logout successful");
    }
}
