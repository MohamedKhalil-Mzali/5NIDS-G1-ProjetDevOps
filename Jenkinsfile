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
                git branch: 'WAJDIBENROMDHANE-5NIDS1-G1', 
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
                // Deploy to Nexus repository, skipping tests
                sh 'mvn deploy -DskipTests -DaltDeploymentRepository=deploymentRepo::default::http://192.168.56.10:8081/repository/maven-releases/'
            }
        }

        stage('Building image') {
            steps {
                // Login to Docker Hub and build Docker image
                withCredentials([string(credentialsId: 'dockerhub', variable: 'dockerhub_token')]) {
                    sh "echo ${dockerhub_token} | docker login -u wajdibenromdhane --password-stdin"
                }
                sh 'docker build -t wajdibenromdhane/gestion-station-ski:1.0.0 .'
            }
        }

        stage('Deploy image') {
            steps {
                // Push Docker image to Docker Hub
                withCredentials([string(credentialsId: 'dockerhub', variable: 'dockerhub_token')]) {
                    sh "echo ${dockerhub_token} | docker login -u wajdibenromdhane --password-stdin"
                    sh 'docker push wajdibenromdhane/gestion-station-ski:1.0.0'
                }
            }
        }

        stage('Docker compose') {
            steps {
                // Start services defined in docker-compose.yml
                sh 'docker compose up -d'
            }
        }

        stage('Start Monitoring Containers') {
            steps {
                // Start specific container by container ID
                sh 'docker start 40d02048d5f4'
            }
        }
    }
}
