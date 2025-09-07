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
                    // Kill old process on 8084 (ignore errors if none found)
                    bat(script: '''
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8084"') do (
                        taskkill /PID %%a /F || echo "No process running on 8084"
                    )
                    ''', returnStatus: true)

                    // Start new backend on port 8084
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
                    // Try to hit Spring Boot actuator health endpoint
                    bat '''
                    powershell -Command "
                    $maxRetries = 5;
                    for ($i=0; $i -lt $maxRetries; $i++) {
                        try {
                            $res = Invoke-WebRequest -Uri http://localhost:8084/actuator/health -UseBasicParsing;
                            if ($res.StatusCode -eq 200) {
                                Write-Output '✅ Backend is UP';
                                exit 0;
                            }
                        } catch {
                            Write-Output '⏳ Waiting for backend...';
                            Start-Sleep -Seconds 5;
                        }
                    }
                    exit 1
                    "
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
