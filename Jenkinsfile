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
                sh 'mvn test'
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
                withCredentials([string(credentialsId: 'dockerhub-jenkins-token', variable: 'dockerhub_token')]) {
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
                sh 'docker start 6191d4dac2a6'  
            }
            post {
                failure {
                    echo 'Failed to start monitoring containers!'
                }
            }
        }

        stage('Email Notification') {
            steps {
                mail bcc: '',
                     body: 'Final Report: The pipeline has completed successfully. No action required.',
                     cc: '',
                     from: '',
                     replyTo: '',
                     subject: 'Success of DevOps Pipeline',
                     to: 'medrayen.balghouthi@esprit.tn, medrayen.balghouthi@gmail.com'
            }
        }
    }

    post {
        success {
            script {
                emailext(
                    subject: "Build Success: ${currentBuild.fullDisplayName}",
                    body: "The build was successful! Check the details at ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'medrayen.balghouthi@esprit.tn, medrayen.balghouthi@gmail.com'
                )
            }
        }
        failure {
            script {
                emailext(
                    subject: "Build Failure: ${currentBuild.fullDisplayName}",
                    body: "The build failed! Check the details at ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'medrayen.balghouthi@esprit.tn, medrayen.balghouthi@gmail.com'
                )
            }
        }
    }
}
