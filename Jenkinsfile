pipeline {
    agent any

    stages {
        stage('Build Backend') {
            steps {
                echo "🚀 Building backend..."
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Restart Backend') {
            steps {
                echo "♻ Restarting backend (java -jar on port 8084)..."
                script {
                    // Kill process running on 8084 (if any)
                    bat '''
                        for /F "tokens=5" %%a in ('netstat -ano ^| findstr ":8084"') do (
                            echo Killing process on port 8084 with PID %%a  
                            taskkill /PID %%a /F 
                        )
                    '''
                    
                    // Run new JAR
                    bat '''
                        cd /d target
                        start cmd /c "java -jar orsp10-backend-0.0.1-SNAPSHOT.jar --server.port=8084"
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "📦 Pipeline finished (success/failure)."
        }
        success {
            echo "✅ Backend deployed successfully on port 8084."
        }
        failure {
            echo "❌ Backend pipeline failed."
        }
    }
}
