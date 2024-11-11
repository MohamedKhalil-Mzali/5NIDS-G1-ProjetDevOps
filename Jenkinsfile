pipeline {
    agent any
    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }
    environment {
        DOCKER_COMPOSE_PATH = '/usr/local/bin/docker-compose'
        DEPENDENCY_CHECK_CACHE_DIR = '/var/jenkins_home/.m2/repository/org/owasp/dependency-check'
        MY_SECRET_KEY = 'dummy_value_for_testing'
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
        
        stage('Generate JaCoCo Coverage Report') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        
        stage('JaCoCo Coverage Report') {
            steps {
                step([$class: 'JacocoPublisher',
                      execPattern: '**/target/jacoco.exec',
                      classPattern: '**/classes',
                      sourcePattern: '**/src',
                      exclusionPattern: '*/target/**/,**/*Test*,**/*_javassist/**'
                ])
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
        
        stage('Publish OWASP Dependency-Check Report') {
            steps {
                step([$class: 'DependencyCheckPublisher',
                      pattern: '**/dependency-check-report.html',
                      healthy: '0',
                      unhealthy: '1',
                      failureThreshold: '1'
                ])
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
        
        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker compose -f docker-compose.yml up -d'
            }
            post {
                failure {
                    echo 'Docker compose up failed!'
                }
            }
        }
        
        stage('Start Monitoring Containers') {
            steps {
                sh 'docker start 5-nids-1-rayen-balghouthi-g1-prometheus-1 grafana'
            }
            post {
                failure {
                    echo 'Failed to start monitoring containers!'
                }
            }
        }
        
        stage('Make Script Executable') {
            steps {
                sh 'chmod +x ./run_security_smoke_tests.sh'
            }
        }
        
        stage('Security Smoke Tests') {
            steps {
                sh './run_security_smoke_tests.sh'
            }
            post {
                failure {
                    echo 'Security smoke tests failed!'
                }
            }
        }

        stage('Server Hardening Validation - Lynis') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh '''
                        sudo mkdir -p /tmp/lynis_reports
                        sudo lynis audit system --quick --report-file /tmp/lynis_reports/lynis-report.dat
                        sudo cp /tmp/lynis_reports/lynis-report.dat /tmp/lynis_reports/lynis-report.html
                        sudo sed -i '1s/^/<html><body><pre>/' /tmp/lynis_reports/lynis-report.html
                        echo "</pre></body></html>" >> /tmp/lynis_reports/lynis-report.html
                    '''
                }
            }
        }

        stage('Publish Lynis Report') {
            steps {
                script {
                    publishHTML([
                        reportName: 'Lynis Report',
                        reportDir: '/tmp/lynis_reports',
                        reportFiles: 'lynis-report.html',
                        alwaysLinkToLastBuild: true,
                        allowMissing: false
                    ])
                }
            }
        }
        
                stage('Send Email Notification') {
            steps {
                script {
                    def subject = currentBuild.currentResult == 'SUCCESS' ?
                        "🌟 Build Success: ${currentBuild.fullDisplayName}" :
                        "🚨 Build Failure: ${currentBuild.fullDisplayName}";

                    def body = """
                        <html>
                        <head>
                            <style>
                                @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
                                body {
                                    font-family: 'Roboto', sans-serif;
                                    background-color: #121212;
                                    color: #e0e0e0;
                                }
                                .container {
                                    width: 80%;
                                    max-width: 600px;
                                    margin: 20px auto;
                                    padding: 30px;
                                    background: linear-gradient(135deg, #3a3f47, #212121);
                                    border-radius: 12px;
                                    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
                                }
                                h2 {
                                    color: ${currentBuild.currentResult == 'SUCCESS' ? '#76ff03' : '#ff3d00'};
                                    text-align: center;
                                    text-transform: uppercase;
                                    font-size: 24px;
                                    letter-spacing: 1px;
                                }
                                p {
                                    font-size: 16px;
                                    line-height: 1.6;
                                    color: #bdbdbd;
                                }
                                table {
                                    width: 100%;
                                    margin-top: 20px;
                                    border-collapse: collapse;
                                    color: #bdbdbd;
                                }
                                th, td {
                                    padding: 12px;
                                    border-bottom: 1px solid #484848;
                                }
                                th {
                                    background-color: #333333;
                                    text-transform: uppercase;
                                }
                                td {
                                    font-weight: bold;
                                }
                                .status {
                                    font-weight: bold;
                                    color: ${currentBuild.currentResult == 'SUCCESS' ? '#76ff03' : '#ff3d00'};
                                }
                                .report-link {
                                    color: #03a9f4;
                                    text-decoration: none;
                                }
                                .report-link:hover {
                                    text-decoration: underline;
                                }
                                .footer {
                                    margin-top: 20px;
                                    text-align: center;
                                    font-size: 14px;
                                    color: #9e9e9e;
                                }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <h2>🚀 Build Status Notification</h2>
                                <p>Dear Team,</p>
                                <p>The Jenkins build for <strong>${env.JOB_NAME}</strong> has completed. Below are the details:</p>
                                <table>
                                  <tr>
                                      <th>Build Number</th>
                                      <td>${currentBuild.number}</td>
                                  </tr>
                                  <tr>
                                      <th>Project</th>
                                      <td>${env.JOB_NAME}</td>
                                  </tr>
                                  <tr>
                                      <th>Build URL</th>
                                      <td><a href="${env.BUILD_URL}" class="report-link">View Build Details</a></td>
                                  </tr>
                                  <tr>
                                      <th>Result</th>
                                      <td class="status">${currentBuild.currentResult}</td>
                                  </tr>
                                </table>
                                <p>Access the full reports:</p>
                                <ul>
                                    <li><a href="${env.BUILD_URL}artifact/target/site/jacoco/index.html" class="report-link">📊 JaCoCo Coverage Report</a></li>
                                    <li><a href="/tmp/lynis_reports/lynis-report.html" class="report-link">🛡️ Lynis Security Report</a></li>
                                    <li><a href="${env.BUILD_URL}artifact/dependency-check-report.html" class="report-link">🔒 OWASP Dependency-Check Report</a></li>
                                </ul>
                                <p>${currentBuild.currentResult == 'SUCCESS' ? '🎉 Congratulations! The build succeeded without any issues.' : '❌ The build encountered issues. Please check the reports for further details.'}</p>
                                <div class="footer">
                                    <p>Kind regards,<br/>The Jenkins DevOps Team</p>
                                </div>
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
