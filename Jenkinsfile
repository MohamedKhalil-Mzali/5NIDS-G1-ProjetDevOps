pipeline { 
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        DOCKER_COMPOSE_PATH = '/usr/local/bin/docker-compose'
        DEPENDENCY_CHECK_CACHE_DIR = '/var/jenkins_home/.m2/repository/org/owasp/dependency-check'
    }

    stages {
        // Development Phase: Pre-commit security, static analysis, and container security
        stage('Git Checkout') {
            steps {
                git branch: 'MedRayenBalghouthi-5NIDS1-G1', url: 'https://github.com/MohamedKhalil-Mzali/5NIDS-G1-ProjetDevOps.git'
            }
        }

        stage('Static Code Analysis - SonarQube') {
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

        stage('Security Vulnerability Scan - Dependency Check') {
            steps {
                sh ''' 
                    mvn verify -Ddependency-check.skip=false \
                    -Ddependency-check.failBuildOnCVSS=7 \
                    -Ddependency-check.threads=1 \
                    -Ddependency-check.autoUpdate=false \
                    dependency-check:aggregate
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

        stage('Security Unit Tests') {
            steps {
                sh 'mvn test -DskipTests=false'
            }
            post {
                failure {
                    echo 'Security unit tests failed!'
                }
            }
        }

        // Acceptance Phase: Build and container security scanning
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

        stage('Container Security Scan') {
            steps {
                // Using Trivy to scan the Docker image for vulnerabilities
                sh 'trivy image rayenbal/5nids-g1:1.0.0'
            }
            post {
                failure {
                    echo 'Container security scan failed!'
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

        // Production Phase: Deploy and run security smoke tests
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

        stage('Security Smoke Tests') {
            steps {
                // Running a simple smoke test for security validation
                sh './run_security_smoke_tests.sh'
            }
            post {
                failure {
                    echo 'Security smoke tests failed!'
                }
            }
        }

        stage('Secrets Management Validation') {
            steps {
                // Placeholder for secrets management validation (implement as needed)
                echo 'Checking secrets management configuration...'
            }
            post {
                failure {
                    echo 'Secrets management validation failed!'
                }
            }
        }

        stage('Server Hardening Validation') {
            steps {
                // Running Lynis to check server hardening
                sh 'sudo lynis audit system --quick'
            }
            post {
                failure {
                    echo 'Server hardening validation failed!'
                }
            }
        }

        // Operations Phase: Continuous scanning and monitoring
        stage('Fault Injection') {
    steps {
        script {
            // Use the container that is running and related to your app
            def containerName = 'jenkins-app-spring-1' // Use this container name for fault injection

            // Using Pumba for fault injection testing
            sh "sudo pumba pause --duration 10s ${containerName}"
        }
    }
    post {
        failure {
            echo 'Fault injection test failed!'
        }
    }
}

        stage('Continuous Scanning and Monitoring') {
            steps {
                // Using Falco for continuous runtime security monitoring
                sh 'sudo falco --config /etc/falco/falco.yaml'
            }
            post {
                failure {
                    echo 'Falco monitoring encountered an error!'
                }
            }
        }

        // Notification
        stage('Send Email Notification') {
            steps {
                script {
                    def subject = currentBuild.currentResult == 'SUCCESS' ? 
                        "🎉 Build Success: ${currentBuild.fullDisplayName}" : 
                        "⚠️ Build Failure: ${currentBuild.fullDisplayName}"

                    def body = """
                        <html>
                        <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333;">
                            <div style="max-width: 600px; margin: auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                                <h2 style="color: #4CAF50; text-align: center;">Build Status Notification</h2>
                                <p style="font-size: 16px; line-height: 1.6;">Hello Team,</p>
                                <p style="font-size: 16px; line-height: 1.6;">The Jenkins build for the project <strong>${env.JOB_NAME}</strong> has completed.</p>

                                <table style="width: 100%; margin-top: 20px; border-collapse: collapse; border: 1px solid #ddd;">
                                    <tr>
                                        <th style="background-color: #f2f2f2; padding: 8px; text-align: left;">Build Number</th>
                                        <td style="padding: 8px;">${currentBuild.number}</td>
                                    </tr>
                                    <tr>
                                        <th style="background-color: #f2f2f2; padding: 8px; text-align: left;">Project</th>
                                        <td style="padding: 8px;">${env.JOB_NAME}</td>
                                    </tr>
                                    <tr>
                                        <th style="background-color: #f2f2f2; padding: 8px; text-align: left;">Build URL</th>
                                        <td style="padding: 8px;"><a href="${env.BUILD_URL}" style="color: #1a73e8;">Click here to view the build</a></td>
                                    </tr>
                                    <tr>
                                        <th style="background-color: #f2f2f2; padding: 8px; text-align: left;">Result</th>
                                        <td style="padding: 8px; font-weight: bold; color: ${currentBuild.currentResult == 'SUCCESS' ? '#4CAF50' : '#FF7043'};">${currentBuild.currentResult}</td>
                                    </tr>
                                </table>

                                <p style="font-size: 16px; line-height: 1.6; margin-top: 20px;">
                                    ${currentBuild.currentResult == 'SUCCESS' ? 
                                        '<span style="color: #4CAF50;">🎉 The build has successfully passed!</span>' : 
                                        '<span style="color: #FF7043;">❌ There were issues during the build. Please check the logs for details.</span>'}
                                </p>

                                <p style="font-size: 14px; line-height: 1.6; color: #888;">
                                    Regards,<br/>
                                    The Jenkins DevOps Team, ADMIN : RAYEN
                                </p>
                            </div>
                        </body>
                        </html>
                    """

                    emailext subject: subject,
                             body: body,
                             mimeType: 'text/html',
                             to: 'rayenbal55@gmail.com'
                }
            }
        }
    }
}
