pipeline {
    agent any
    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }
    stages {
        stage('GIT') {
            steps {
                git branch: 'MedRayenBalghouthi-5NIDS1-G1',
                    url: 'https://github.com/MohamedKhalil-Mzali/5NIDS-G1-ProjetDevOps.git'
            }
        }

        stage('Compile Stage') {
            steps {
                sh 'mvn clean compile'
            }
            post {
                failure {
                    echo 'Compilation failed!'
                }
            }
        }

        stage('JUnit/Mockito Tests') {
            steps {
                sh 'mvn test -U'
            }
            post {
                failure {
                    echo 'Tests failed!'
                }
            }
        }

        stage('Scan: SonarQube') {
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

        stage('Security Check') {
            steps {
                // Run OWASP Dependency-Check
                sh 'mvn org.owasp:dependency-check-maven:8.2.1:check'
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

        stage('Deploy to Nexus') {
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

        stage('Deploy Docker Image') {
            steps {
                withCredentials([string(credentialsId: 'Docker', variable: 'dockerhub_token')]) {
                    sh "docker login -u rayenbal -p ${dockerhub_token}"
                    sh 'docker push rayenbal/5nids-g1:1.0.0'
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

        stage('Docker Compose') {
            steps {
                sh 'docker-compose up -d'
            }
            post {
                failure {
                    echo 'Docker compose up failed!'
                }
            }
        }

        stage('Start Monitoring Containers') {
            steps {
                sh 'docker start docker-prometheus-1 docker-mysqldb-1 grafana'
            }
            post {
                failure {
                    echo 'Failed to start monitoring containers!'
                }
            }
        }

        stage('Email Notification') {
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
