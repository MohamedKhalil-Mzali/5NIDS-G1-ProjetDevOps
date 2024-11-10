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
                sh 'mvn clean compile'
            }
        }

        stage('JUnit/Mockito Tests') {
            steps {
                sh 'mvn test' 
            }
        }

        stage('JaCoCo Report') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('JaCoCo coverage report') {
            steps {
                step([$class: 'JacocoPublisher',
                      execPattern: '**/target/jacoco.exec',
                      classPattern: '**/classes',
                      sourcePattern: '**/src',
                      exclusionPattern: '*/target/**/,**/*Test*,**/*_javassist/**'
                ])
            }
        }

        stage('Scan : SonarQube') {
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

        stage('Start Monitoring Containers') {
            steps {
                sh 'docker start be79135ec1cc'
            }
        }

        stage('Security Scan: ZAP Baseline Scan') {
            steps {
                script {
                    def targetUrl = 'http://192.168.33.10:8089'
                    echo "Starting ZAP Baseline Scan on ${targetUrl}"
                    sh """
                        docker run --rm -v \$(pwd):/zap/wrk:rw owasp/zap2docker-stable zap-baseline.py \
                        -t ${targetUrl} \
                        -r ZAP_Report.html \
                        -J ZAP_Report.json \
                        -z "-config api.disablekey=true"
                    """
                    archiveArtifacts artifacts: 'ZAP_Report.html', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'ZAP_Report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Security Scan: Nmap') {
            steps {
                script {
                    echo "Starting Nmap Security Scan..."
                    sh 'sudo nmap -sS -p 1-65535 -v localhost'
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
                     to: 'hammaminawel22@gmail.com, nawel.hammami@esprit.tn'
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
                    to: 'hammaminawel22@gmail.com, nawel.hammami@esprit.tn'
                )
            }
        }
        failure {
            script {
                emailext (
                    subject: "Build Failure: ${currentBuild.fullDisplayName}",
                    body: "Le build a échoué ! Vérifiez les détails à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'hammaminawel22@gmail.com, nawel.hammami@esprit.tn'
                )
            }
        }
        always {
            script {
                emailext (
                    subject: "Build Notification: ${currentBuild.fullDisplayName}",
                    body: "Consultez les détails du build à ${env.BUILD_URL}",
                    recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']],
                    to: 'hammaminawel22@gmail.com, nawel.hammami@esprit.tn'
                )
            }
        }
    }
}
