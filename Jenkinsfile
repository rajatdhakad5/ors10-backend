pipeline {
    agent any

    environment {
        JAVA_HOME     = "C:\\Program Files\\Java\\jdk-11.0.15.1"
        PATH          = "${env.JAVA_HOME}\\bin;${env.PATH}"
        BACKEND_REPO  = "https://github.com/rajatdhakad5/ors10-backend.git"
        BACKEND_BRANCH = "master"
        JAR_FILE      = "target\\orsp10-backend-0.0.1-SNAPSHOT.jar"
        BACKEND_PORT  = "8084"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "🔄 Checking out backend code..."
                deleteDir()
                git branch: "${env.BACKEND_BRANCH}", url: "${env.BACKEND_REPO}"
            }
        }

        stage('Build with Maven') {
            steps {
                echo "⚙️ Building Spring Boot JAR with Maven..."
                bat "\"${tool 'Maven'}\\bin\\mvn.cmd\" clean package -DskipTests"
            }
        }

        stage('Restart Backend') {
            steps {
                echo "♻ Restarting backend (java -jar on port ${env.BACKEND_PORT})..."
                script {
                    // Kill old process safely (ignore if not found)
                    bat """
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":${env.BACKEND_PORT}"') do (
                        echo Killing process on port ${env.BACKEND_PORT} with PID %%a
                        taskkill /PID %%a /F
                    )
                    exit /b 0
                    """

                    // Start new backend process
                    bat """
                    cd /d target
                    start cmd /c "java -jar orsp10-backend-0.0.1-SNAPSHOT.jar --server.port=${env.BACKEND_PORT}"
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                echo "🩺 Checking backend health..."
                script {
                    bat '''
                    setlocal enabledelayedexpansion
                    set RETRIES=5
                    set COUNT=1
                    :RETRY
                    curl -s http://localhost:8084/actuator/health >nul
                    if !errorlevel! == 0 (
                        echo ✅ Backend is UP
                        exit /b 0
                    ) else (
                        if !COUNT! lss !RETRIES! (
                            echo ⏳ Waiting for backend... Attempt !COUNT! of !RETRIES!
                            set /a COUNT=!COUNT!+1
                            timeout /t 5 >nul
                            goto :RETRY
                        ) else (
                            echo ❌ Backend did not start properly
                            exit /b 1
                        )
                    )
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Backend pipeline completed successfully."
        }
        failure {
            echo "❌ Backend pipeline failed. Check above logs."
        }
        always {
            echo "📌 Pipeline finished (success/failure)."
        }
    }
}
