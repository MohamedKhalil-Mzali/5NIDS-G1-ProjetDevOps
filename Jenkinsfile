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
        stage('Pre-commit Security Hooks') {
            steps {
                script {
                    sh '''
                    if ! command -v pre-commit &> /dev/null
                    then
                        echo "pre-commit n'est pas installé, installation dans un environnement virtuel..."
                        python3 -m venv venv
                        . venv/bin/activate
                        pip install pre-commit
                    fi
                    git config --unset-all core.hooksPath
                    pre-commit install
                    pre-commit run --all-files
                    '''
                }
            }
        }

        stage('Compile Stage') {
            steps {
                // Clean and compile the project
                sh 'mvn clean compile'
            }
        }
        
        stage('Scan Sonarqube') {
            steps {
                // Run SonarQube analysis
                withSonarQubeEnv('sq1') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('System Security Check - Lynis') {
            steps {
                script {
                    // Exécution de l'audit de sécurité système avec Lynis
                    sh 'lynis audit system | tee lynis_audit_output.txt'
                    
                    // Archivage des résultats pour consultation ultérieure
                    archiveArtifacts artifacts: 'lynis_audit_output.txt', allowEmptyArchive: true
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
                withCredentials([string(credentialsId: 'dockerhub.jenkins.token', variable: 'dockerhub_token')]) {
                    sh "echo ${dockerhub_token} | docker login -u wajdibenromdhane --password-stdin"
                }
                sh 'docker build -t wajdibenromdhane/gestion-station-ski:1.0.0 .'
            }
        }

        stage('Security Scan with Trivy') {
            steps {
                script {
                    // Effectuer un scan avec Trivy pour vérifier la présence de vulnérabilités dans l'image Docker
                    echo "Lancement du scan de sécurité avec Trivy..."
                    sh 'trivy image --exit-code 1 --severity HIGH,CRITICAL wajdibenromdhane/gestion-station-ski:1.0.0'
                }
            }
        }

        stage('Deploy image') {
            steps {
                // Push Docker image to Docker Hub
                withCredentials([string(credentialsId: 'dockerhub.jenkins.token', variable: 'dockerhub_token')]) {
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

        stage('Start Monitoring Containers Grafana Prometheus ') {
            steps {
                // Start specific container by container ID
                sh 'docker start 40d02048d5f4'
            }
        }

        stage('Security Scan : Nmap') {
            steps {
                 script {
                  echo "Starting Nmap Security Scan..."
                  sh 'nmap -sT -p 1-65535 -v localhost'
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
                     to: 'wajdiben2019@gmail.com, wajdi.benromdhane@esprit.tn'
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
                    to: 'wajdiben2019@gmail.com, wajdi.benromdhane@esprit.tn'
                )
            }
        }
        failure {
            script {
                emailext (
                    subject: "Build Failure: ${currentBuild.fullDisplayName}",
                    body: "Le build a échoué ! Vérifiez les détails à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'wajdiben2019@gmail.com, wajdi.benromdhane@esprit.tn'
                )
            }
        }
        always {
            script {
                emailext (
                    subject: "Build Notification: ${currentBuild.fullDisplayName}",
                    body: "Consultez les détails du build à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'wajdiben2019@gmail.com, wajdi.benromdhane@esprit.tn'
                )
            }
        }
    }
}
