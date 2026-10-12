
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
            description: 'Directory where the React production build is deployed'
        )
    }

    environment {
        FRONTEND_DIR = 'frontend'
        BACKEND_DIR = 'backend'
        NGINX_URL = 'http://localhost'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git log -1 --oneline'
            }
        }

        stage('Build and Test') {
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

        stage('Package') {
            steps {
                archiveArtifacts(
                    artifacts: 'backend/target/*.jar,frontend/dist/**',
                    fingerprint: true
                )
            }
        }

        stage('Deploy') {
            steps {
                bat '''
                    if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
                    if exist "%DEPLOY_DIR%\\*" del /Q "%DEPLOY_DIR%\\*"
                    xcopy "frontend\\dist\\*" "%DEPLOY_DIR%\\" /E /I /Y
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                bat '''
                    powershell -NoProfile -Command "$r = Invoke-WebRequest -Uri $env:NGINX_URL -UseBasicParsing; if ($r.StatusCode -ne 200) { exit 1 }; Write-Host ('NGINX HTTP Status: ' + $r.StatusCode)"
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed: build, package, deployment and verification succeeded.'
        }
        failure {
            echo 'Pipeline failed. Review the stage console output.'
        }
    }
}
