pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    stages {
        stage('GIT') {
            steps {
                // Clone the specified branch of the repository
                git branch: 'AchrefWerchfeni-5NIDS1-G1', 
                    url: 'https://github.com/MohamedKhalil-Mzali/5NIDS-G1-ProjetDevOps.git'
            }
        }

        stage('Compile Stage') {
            steps {
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }

        stage('Scan') {
            steps {
                // Run SonarQube analysis
                withSonarQubeEnv('sq1') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests -DaltDeploymentRepository=deploymentRepo::default::http://192.168.33.10:8081/repository/maven-releases/'
            }
        }

        stage('Building image') {
            steps {
                // Login to Docker Hub before building the image
                withCredentials([string(credentialsId: 'dockerhub', variable: 'dockerhub_token')]) {
                    sh "echo ${dockerhub_token} | docker login -u zarix12 --password-stdin"
                }
                // Build the Docker image
                sh 'docker build -t zarix12/gestion-station-ski:1.0.0 .'
            }
        }

        stage('Deploy image') {
            steps {
                withCredentials([string(credentialsId: 'dockerhub', variable: 'dockerhub_token')]) {
                    sh "docker login -u zarix12 -p ${dockerhub_token}"
                    sh 'docker push zarix12/gestion-station-ski:1.0.0'
                }
            }
        }

        stage('Docker compose') {
            steps {
                sh 'docker compose up -d'
            }
        }

        stage('Start Monitoring Containers') {
            steps {
                sh 'docker start 3d9d88426efa'
            }
        }
    }
stage('Email Notification') {
            steps {
                mail bcc: '', 
                     body: '''
Final Report: The pipeline has completed successfully. No action required.
''', 
                     cc: '', 
                     from: '', 
                     replyTo: '', 
                     subject: 'Succès de la pipeline DevOps Project', 
                     to: 'zarouxi@gmail.com, achref.werchfeni@esprit.tn'
            }
        }
    }

    post {
        success {
            script {
                emailext (
                    subject: "Build Success: ${currentBuild.fullDisplayName}",
                    body: "Le build a réussi ! Consultez les détails à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'zarouxi@gmail.com, achref.werchfeni@esprit.tn'
                )
            }
        }
        failure {
            script {
                emailext (
                    subject: "Build Failure: ${currentBuild.fullDisplayName}",
                    body: "Le build a échoué ! Vérifiez les détails à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'zarouxi@gmail.com, achref.werchfeni@esprit.tn'
                )
            }
        }
        always {
            script {
                emailext (
                    subject: "Build Notification: ${currentBuild.fullDisplayName}",
                    body: "Consultez les détails du build à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'zarouxi@gmail.com, achref.werchfeni@esprit.tn'
                )
            }
        }
    }
}

