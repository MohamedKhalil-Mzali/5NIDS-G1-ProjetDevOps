pipeline {
    agent any
    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }
    environment {
        DOCKER_COMPOSE_PATH = '/usr/local/bin/docker-compose'
        DEPENDENCY_CHECK_CACHE_DIR = '/var/jenkins_home/.m2/repository/org/owasp/dependency-check'  // Customize this for your setup
    }
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'MedRayenBalghouthi-5NIDS1-G1', url: 'https://github.com/MohamedKhalil-Mzali/5NIDS-G1-ProjetDevOps.git'
            }
        }

        stage('Compile Project') {
            steps {
                sh 'mvn clean compile'
            }
            post {
                failure {
                    echo 'Compilation failed!'
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test -U'
            }
            post {
                failure {
                    echo 'Tests failed!'
                }
            }
        }

        // Move JaCoCo Coverage Report here after Unit Tests
        stage('Generate JaCoCo Coverage Report') {
            steps {
                // Generate the JaCoCo coverage report
                sh 'mvn jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sq1') {
                    sh 'mvn sonar:sonar'
                }
            }
            post {
                failure {
                    echo 'SonarQube scan failed!'
                }
            }
        }

        stage('Security Vulnerability Scan') {
            steps {
                // Run OWASP Dependency-Check with caching of CVE data
                sh ''' 
                    mvn org.owasp:dependency-check-maven:8.2.1:check \
                    -Ddependency-check.cacheDirectory=${DEPENDENCY_CHECK_CACHE_DIR}
                '''
            }
            post {
                failure {
                    echo 'Dependency-Check failed! Found vulnerabilities in dependencies.'
                }
                success {
                    echo 'No vulnerabilities found in dependencies.'
                }
            }
        }

        stage('Deploy to Nexus Repository') {
            steps {
                sh ''' 
                    mvn deploy -DskipTests \
                    -DaltDeploymentRepository=deploymentRepo::default::http://192.168.56.10:8081/repository/maven-releases/
                '''
            }
            post {
                success {
                    echo 'Deployment to Nexus was successful!'
                }
                failure {
                    echo 'Deployment to Nexus failed!'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t rayenbal/5nids-g1:1.0.0 .'
            }
            post {
                failure {
                    echo 'Docker image build failed!'
                }
            }
        }

        stage('Push Docker Image to Hub') {
            steps {
                script {
                    echo "Attempting Docker login with user: rayenbal"
                    withCredentials([usernamePassword(credentialsId: 'Docker', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_TOKEN')]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_TOKEN}"
                        sh 'docker push rayenbal/5nids-g1:1.0.0'
                    }
                }
            }
            post {
                success {
                    echo 'Docker image pushed successfully!'
                }
                failure {
                    echo 'Docker image push failed!'
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker compose up -d'
            }
            post {
                failure {
                    echo 'Docker compose up failed!'
                }
            }
        }

        stage('Start Monitoring Containers') {
            steps {
                sh 'docker start jenkins-prometheus-1 jenkins-mysqldb-1 grafana'
            }
            post {
                failure {
                    echo 'Failed to start monitoring containers!'
                }
            }
        }

        stage('Send Email Notification') {
            steps {
                script {
                    def subject = currentBuild.currentResult == 'SUCCESS' ? "🎉 Build Success: ${currentBuild.fullDisplayName}" : "⚠️ Build Failure: ${currentBuild.fullDisplayName}"
                    def body = """ 
                        <html>
                        <body>
                            <h2>${currentBuild.currentResult == 'SUCCESS' ? 'Build Successful!' : 'Build Failed!'}</h2>
                            <p><strong>Build Number:</strong> ${currentBuild.number}</p>
                            <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">Click here</a></p>
                            <p><strong>Result:</strong> ${currentBuild.currentResult}</p>
                            ${currentBuild.currentResult == 'SUCCESS' ? '<p style="color:green;">Everything went great! 🎉</p>' : '<p style="color:red;">There were some issues during the build.</p>'}
                            <p>Regards,<br/>Your DevOps Jenkins</p>
                        </body>
                        </html>
                    """
                    emailext(
                        subject: subject,
                        body: body,
                        to: 'rayenbal55@gmail.com',
                        mimeType: 'text/html',
                        recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']]
                    )
                }
            }
        }
    }

    post {
        success {
            script {
                emailext(
                    subject: "🎉 Build Success: ${currentBuild.fullDisplayName}",
                    body: """ 
                        <html>
                        <body>
                            <h2>Build was successful!</h2>
                            <p><strong>Build Number:</strong> ${currentBuild.number}</p>
                            <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Details:</strong> <a href="${env.BUILD_URL}">Click here</a></p>
                            <p>Thank you for using Jenkins!</p>
                        </body>
                        </html>
                    """,
                    to: 'rayenbal87@gmail.com',
                    mimeType: 'text/html'
                )
            }
        }
        failure {
            script {
                emailext(
                    subject: "⚠️ Build Failure: ${currentBuild.fullDisplayName}",
                    body: """ 
                        <html>
                        <body>
                            <h2>Unfortunately, the build failed!</h2>
                            <p><strong>Build Number:</strong> ${currentBuild.number}</p>
                            <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Details:</strong> <a href="${env.BUILD_URL}">Click here</a></p>
                            <p style="color:red;">Please check the logs for more details.</p>
                            <p>Regards,<br/>Jenkins Team</p>
                        </body>
                        </html>
                    """,
                    to: 'rayenbal87@gmail.com',
                    mimeType: 'text/html'
                )
            }
        }
    }
}
