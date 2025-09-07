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
                    def retries = 10
                    def success = false

                    for (int i = 1; i <= retries; i++) {
                        echo "⏳ Waiting for backend... Attempt ${i} of ${retries}"

                        def status = bat(
                            script: "curl -s -o NUL -w \"%%{http_code}\" http://localhost:${env.BACKEND_PORT}/actuator/health",
                            returnStdout: true
                        ).trim()

                        echo "HTTP Status: ${status}"

                        if (status == "200") {
                            echo "✅ Backend is UP"
                            success = true
                            break
                        } else {
                            sleep(time: 10, unit: "SECONDS")
                        }
                    }

                    if (!success) {
                        error("❌ Backend did not start properly")
                    }
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
