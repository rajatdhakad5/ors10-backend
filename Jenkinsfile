pipeline {
    agent any

    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-11.0.15.1"
        PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
        BACKEND_REPO = "https://github.com/rajatdhakad5/ors10-backend.git"
        BACKEND_BRANCH = "master"
        JAR_FILE = "target\\orsp10-backend-0.0.1-SNAPSHOT.jar"
        BACKEND_PORT = "8084"
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
                    // Kill old process if running on port
                    bat '''
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8084"') do taskkill /PID %%a /F
                    '''

                    // Start new backend process
                    bat """
                    cd /d target
                    start java -jar orsp10-backend-0.0.1-SNAPSHOT.jar --server.port=${env.BACKEND_PORT}
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                echo "🩺 Checking backend health..."
                script {
                    bat '''
                    for /L %%i in (1,1,5) do (
                        curl -s http://localhost:8084/actuator/health >nul
                        if %errorlevel%==0 (
                            echo ✅ Backend is UP
                            exit /b 0
                        ) else (
                            echo ⏳ Waiting for backend...
                            timeout /t 5 >nul
                        )
                    )
                    echo ❌ Backend did not start properly
                    exit /b 1
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "✅ Backend pipeline completed. Check above logs."
        }
    }
}
