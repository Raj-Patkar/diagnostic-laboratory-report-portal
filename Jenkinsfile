pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    parameters {
        string(
            name: 'DEPLOY_DIR',
            defaultValue: 'C:\\nginx\\html',
            description: 'Frontend deployment directory'
        )
    }

    environment {
        FRONTEND_DIR = 'frontend'
        BACKEND_DIR = 'backend'
        SELENIUM_DIR = 'selenium-tests'
        NGINX_URL = 'http://localhost'
        DOCKER_IMAGE = 'rajpatkar/diagnostic-lab-portal'
        CONTAINER_NAME = 'diagnostic-lab-portal-container'
        CONTAINER_PORT = '8085'
        HOST_PORT = '8086'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git log -1 --oneline'
            }
        }

        stage('Build Application') {
            steps {
                dir("${FRONTEND_DIR}") {
                    bat 'npm ci'
                    bat 'npm run build'
                }

                dir("${BACKEND_DIR}") {
                    bat 'mvnw.cmd -B clean package -DskipTests'
                }
            }
        }

        stage('Selenium Continuous Testing') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'portal-test-credentials',
                        usernameVariable: 'PORTAL_USERNAME',
                        passwordVariable: 'PORTAL_PASSWORD'
                    )
                ]) {
                    dir("${SELENIUM_DIR}") {
                        bat 'mvn -B clean test -Dheadless=true -DbaseUrl=http://localhost:5173'
                    }
                }
            }
            post {
                always {
                    junit(
                        testResults: 'selenium-tests/target/surefire-reports/TEST-*.xml',
                        allowEmptyResults: false
                    )
                    archiveArtifacts(
                        artifacts: 'selenium-tests/target/screenshots/**',
                        allowEmptyArchive: true,
                        fingerprint: true
                    )
                }
            }
        }

        stage('Package Artifacts') {
            steps {
                archiveArtifacts(
                    artifacts: 'backend/target/*.jar,frontend/dist/**',
                    fingerprint: true
                )
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    env.IMAGE_TAG = "build-${env.BUILD_NUMBER}"
                }
                bat 'docker build -t %DOCKER_IMAGE%:%IMAGE_TAG% .'
            }
        }

        stage('Publish Docker Image') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    bat '''
                        @echo off
                        echo %DOCKERHUB_TOKEN%| docker login -u %DOCKERHUB_USERNAME% --password-stdin
                        if errorlevel 1 exit /b 1
                        docker push %DOCKER_IMAGE%:%IMAGE_TAG%
                        if errorlevel 1 exit /b 1
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy Backend Container') {
            steps {
                withCredentials([
                    string(credentialsId: 'diaglab-db-url', variable: 'DB_URL'),
                    string(credentialsId: 'diaglab-db-username', variable: 'DB_USERNAME'),
                    string(credentialsId: 'diaglab-db-password', variable: 'DB_PASSWORD')
                ]) {
                    bat '''
                        @echo off
                        docker pull %DOCKER_IMAGE%:%IMAGE_TAG%
                        if errorlevel 1 exit /b 1

                        docker rm -f %CONTAINER_NAME% 2>nul

                        docker run -d --name %CONTAINER_NAME% ^
                          --restart unless-stopped ^
                          -p %HOST_PORT%:%CONTAINER_PORT% ^
                          -e DB_URL -e DB_USERNAME -e DB_PASSWORD ^
                          %DOCKER_IMAGE%:%IMAGE_TAG%

                        if errorlevel 1 exit /b 1
                    '''
                }
            }
        }

        stage('Verify Backend Container') {
            steps {
                bat '''
                    @echo off
                    powershell -NoProfile -Command "$ok=$false; for ($i=0; $i -lt 30; $i++) { $c=docker inspect -f '{{.State.Running}}' $env:CONTAINER_NAME 2>$null; if ($c -eq 'true') { $ok=$true; break }; Start-Sleep -Seconds 2 }; if (-not $ok) { docker logs $env:CONTAINER_NAME; exit 1 }; docker ps --filter name=$env:CONTAINER_NAME; docker logs --tail 40 $env:CONTAINER_NAME; exit 0"
                '''
            }
        }

        stage('Deploy Frontend') {
            steps {
                bat '''
                    if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
                    if exist "%DEPLOY_DIR%\\*" del /Q "%DEPLOY_DIR%\\*"
                    xcopy "frontend\\dist\\*" "%DEPLOY_DIR%\\" /E /I /Y
                '''
            }
        }

        stage('Verify Frontend') {
            steps {
                bat '''
                    powershell -NoProfile -Command "$r = Invoke-WebRequest -Uri $env:NGINX_URL -UseBasicParsing; if ($r.StatusCode -ne 200) { exit 1 }; Write-Host ('NGINX HTTP Status: ' + $r.StatusCode)"
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded: tests, versioned Docker image publication, backend container deployment and frontend verification.'
        }
        failure {
            echo 'Pipeline failed. Review the failed stage and logs before retrying.'
        }
    }
}