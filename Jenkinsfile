pipeline {
    agent any

    environment {
        JAVA_HOME      = "C:\\Program Files\\Java\\jdk-11.0.15.1"
        PATH           = "${env.JAVA_HOME}\\bin;${env.PATH}"
        BACKEND_REPO   = "https://github.com/rajatdhakad5/ors10-backend.git"
        BACKEND_BRANCH = "master"
        JAR_FILE       = "target\\orsp10-backend-0.0.1-SNAPSHOT.jar"
        BACKEND_PORT   = "8084"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "🔄 Checking out backend code..."
                deleteDir()
                git branch: "${env.BACKEND_BRANCH}", url: "${env.BACKEND_REPO}"
            }
        }

        stage('Build Backend') {
            steps {
                echo "⚙️ Building Spring Boot JAR with Maven..."
                bat "\"${tool 'Maven'}\\bin\\mvn.cmd\" clean package -DskipTests"
            }
        }

        stage('Restart Backend') {
            steps {
                echo "♻ Restarting backend on port ${env.BACKEND_PORT}..."
                script {
                    // Kill process running on given port
                    bat """
                        for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":${env.BACKEND_PORT}"') do (
                            echo Found Java process with PID %%a
                            taskkill /PID %%a /F
                        )
                        timeout /t 3 >nul
                    """

                    // Run JAR in foreground (logs will stream to Jenkins console)
                    bat """
                        java -jar target\\orsp10-backend-0.0.1-SNAPSHOT.jar --server.port=${env.BACKEND_PORT}
                    """
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
