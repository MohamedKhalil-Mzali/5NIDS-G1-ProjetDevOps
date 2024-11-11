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

        // Extra security and monitoring stages - catch errors to pass
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
                    sed -i '1s/^/<html><body><pre>/' /tmp/lynis_reports/lynis-report.html
                    echo "</pre></body></html>" >> /tmp/lynis_reports/lynis-report.html
                    '''
                }
            }
            
        }
        stage('Publish Lynis Report') {
    steps {
        script {
            // Assuming the HTML report was generated earlier in the pipeline
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
                "🚨 Build Failure: ${currentBuild.fullDisplayName}"

            def body = """
                <html>
                <head>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            background-color: #f0f0f0;
                            margin: 0;
                            padding: 0;
                            color: #333;
                        }
                        .container {
                            width: 100%;
                            max-width: 700px;
                            margin: auto;
                            padding: 30px;
                            background: linear-gradient(135deg, #6e7f8f, #2b3e4b);
                            color: #fff;
                            border-radius: 10px;
                            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                        }
                        h2 {
                            color: #4CAF50;
                            font-size: 28px;
                            text-align: center;
                            margin-bottom: 15px;
                        }
                        p {
                            font-size: 16px;
                            line-height: 1.6;
                        }
                        .status {
                            font-weight: bold;
                            color: ${currentBuild.currentResult == 'SUCCESS' ? '#4CAF50' : '#FF7043'};
                        }
                        .table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 20px;
                            background: #fff;
                            border-radius: 8px;
                            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                        }
                        th, td {
                            padding: 15px;
                            text-align: left;
                            border: 1px solid #ddd;
                        }
                        th {
                            background-color: #2b3e4b;
                            color: #fff;
                        }
                        td {
                            background-color: #f9f9f9;
                        }
                        .link {
                            color: #1a73e8;
                            text-decoration: none;
                            font-weight: bold;
                        }
                        .link:hover {
                            text-decoration: underline;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            color: #ccc;
                            text-align: center;
                        }
                        .icon {
                            width: 20px;
                            height: 20px;
                            margin-right: 10px;
                            vertical-align: middle;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>🚀 Build Status Notification</h2>
                        <p>Hello Team,</p>
                        <p>The Jenkins build for the project <strong>${env.JOB_NAME}</strong> has completed.</p>
                        <table class="table">
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
                                <td><a href="${env.BUILD_URL}" class="link">Click here to view the build</a></td>
                            </tr>
                            <tr>
                                <th>Result</th>
                                <td class="status">${currentBuild.currentResult}</td>
                            </tr>
                        </table>

                        <p>Access the reports:</p>
                        <ul>
                            <li><a href="${env.BUILD_URL}artifact/target/site/jacoco/index.html" class="link">📊 JaCoCo Coverage Report</a></li>
                            <li><a href="/tmp/lynis_reports/lynis-report.html" class="link">🛡️ Lynis Security Report</a></li>
                            <li><a href="${env.BUILD_URL}artifact/target/dependency-check-report.html" class="link">🔒 OWASP Dependency-Check Report</a></li>
                        </ul>

                        <p>${currentBuild.currentResult == 'SUCCESS' ? '🎉 The build has successfully passed!' : '❌ There were issues during the build. Please check the logs for details.'}</p>

                        <div class="footer">
                            <p>Regards,<br/> The Jenkins DevOps Team, ADMIN: RAYEN</p>
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
}
