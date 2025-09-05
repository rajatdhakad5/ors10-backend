pipeline {
    agent any

    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-11.0.15.1"
        PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
        
        BACKEND_REPO = 'https://github.com/rajatdhakad5/ors10-backend.git'
        BACKEND_BRANCH = 'master'
        BACKEND_DIR = 'ors10-backend'
        BACKEND_JAR = 'ors10-backend\\target\\backend.jar'  // update with actual jar name
    }

    stages {
        stage('Checkout Backend') {
            steps {
                dir("${BACKEND_DIR}") {
                    deleteDir()
                    git branch: "${BACKEND_BRANCH}", url: "${BACKEND_REPO}"
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir("${BACKEND_DIR}") {
                    echo "🛠 Building Spring Boot project..."
                    bat "mvn clean install"
                }
            }
        }

        stage('Run Backend') {
            steps {
                echo "🚀 Running Spring Boot backend..."
                bat "start cmd /c java -jar ${BACKEND_JAR}"
                // 'start cmd /c' ensures backend runs in new command window and Jenkins continues
            }
        }
    }

    post {
        always {
            echo "✅ Backend pipeline completed."
        }
    }
}
