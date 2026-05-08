pipeline {
    agent any
    
    environment {
        GEMINI_API_KEY = credentials('gemini-api-key')
        OPENAI_API_KEY = credentials('openai-api-key')
        DOCKER_REGISTRY = 'your-registry.com'
        DOCKER_IMAGE = 'resume-analyzer'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean compile test'
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }
        
        stage('Build Docker Images') {
            parallel {
                stage('Backend Image') {
                    steps {
                        dir('backend') {
                            script {
                                def image = docker.build("${DOCKER_REGISTRY}/${DOCKER_IMAGE}-backend:${BUILD_NUMBER}")
                                image.push()
                                image.push('latest')
                            }
                        }
                    }
                }
                stage('Frontend Image') {
                    steps {
                        dir('frontend') {
                            script {
                                def image = docker.build("${DOCKER_REGISTRY}/${DOCKER_IMAGE}-frontend:${BUILD_NUMBER}")
                                image.push()
                                image.push('latest')
                            }
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            steps {
                script {
                    // Deploy to staging environment
                    sh 'kubectl apply -f kubernetes/ -n staging'
                }
            }
        }
        
        stage('Run Tests') {
            parallel {
                stage('Backend Tests') {
                    steps {
                        dir('backend') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Frontend Tests') {
                    steps {
                        dir('frontend') {
                            sh 'npm test -- --coverage --watchAll=false'
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    // Deploy to production environment
                    sh 'kubectl apply -f kubernetes/ -n production'
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
            mail to: 'team@example.com',
                subject: 'Pipeline Failed',
                body: "The pipeline for ${env.JOB_NAME} failed. Check the logs for details."
        }
    }
}
