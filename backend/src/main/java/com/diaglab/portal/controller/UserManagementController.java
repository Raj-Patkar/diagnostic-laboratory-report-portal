
package com.diaglab.portal.controller;

import com.diaglab.portal.entity.User;
import com.diaglab.portal.entity.UserRole;
import com.diaglab.portal.repository.UserRepository;
import com.diaglab.portal.service.ActivityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserManagementController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityService activityService;

    public UserManagementController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ActivityService activityService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityService = activityService;
    }

    public record CreateUserRequest(
            @NotBlank
            @Size(min = 3, max = 50)
            @Pattern(
                    regexp = "[A-Za-z0-9._-]+",
                    message = "Username may contain letters, numbers, dots, underscores, and hyphens only"
            )
            String username,

            @NotBlank
            @Size(min = 8, max = 100)
            String password,

            UserRole role
    ) {}

    public record UserResponse(
            Long id,
            String username,
            String role,
            boolean enabled,
            LocalDateTime createdAt
    ) {
        static UserResponse from(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name(),
                    user.isEnabled(),
                    user.getCreatedAt()
            );
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return userRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(
                        User::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(UserResponse::from)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication
    ) {
        String username = request.username().trim();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "That username is already in use."
            );
        }

        if (request.role() != UserRole.DOCTOR
                && request.role() != UserRole.TECHNICIAN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only DOCTOR or TECHNICIAN accounts can be created here."
            );
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setEnabled(true);

        User saved = userRepository.save(user);

        activityService.record(
                "USER_CREATED",
                authentication.getName(),
                "Created " + saved.getRole().name()
                        + " account: " + saved.getUsername()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.from(saved));
    }
}
