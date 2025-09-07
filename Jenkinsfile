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

        stage('Run Backend (Foreground)') {
            steps {
                echo "♻ Running backend JAR in foreground (logs will appear below)..."
                script {
                    // Kill old process if running
                    bat """
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":${env.BACKEND_PORT}"') do (
                        echo Killing process on port ${env.BACKEND_PORT} with PID %%a
                        taskkill /PID %%a /F
                    )
                    """

                    // Run JAR in foreground so logs come in Jenkins console
                    bat """
                    cd /d target
                    java -jar orsp10-backend-0.0.1-SNAPSHOT.jar --server.port=${env.BACKEND_PORT}
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Backend JAR is running in foreground. Logs are visible in console."
        }
        failure {
            echo "❌ Backend pipeline failed. Check above logs."
        }
    }
}
