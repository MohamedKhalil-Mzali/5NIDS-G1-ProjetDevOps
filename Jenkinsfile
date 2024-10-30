pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    stages {
        stage('GIT') {
            steps {
                git branch: 'Nawelhammami-5NIDS1-G1', 
                url: 'https://github.com/MohamedKhalil-Mzali/5NIDS-G1-ProjetDevOps.git'
            }
        }

        stage('Compile Stage') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Scan') {
            steps {
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
                sh 'docker build -t nawel119/gestion-station-ski:1.0.0 .'
            }
        }

        stage('Deploy image') {
            steps {
                withCredentials([string(credentialsId: 'dockerhub-jenkins-token', variable: 'dockerhub_token')]) {
                    sh "docker login -u nawel119 -p ${dockerhub_token}"
                    sh 'docker push nawel119/gestion-station-ski:1.0.0'
                }
            }
        }

        stage('Docker compose') {
            steps {
                sh 'docker compose up -d'
            }
        }

        stage('Deploy Grafana') {
            steps {
                sh 'docker compose up -d grafana'
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
                    to: 'nawel.hammami@esprit.tn'
                )
            }
        }
        failure {
            script {
                emailext (
                    subject: "Build Failure: ${currentBuild.fullDisplayName}",
                    body: "Le build a échoué ! Vérifiez les détails à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'nawel.hammami@esprit.tn'
                )
            }
        }
    }
}

