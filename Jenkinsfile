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
                        sudo chmod -R 777 /tmp/lynis_reports/
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
            // Determine the subject based on build result
            def subject = currentBuild.currentResult == 'SUCCESS' ? 
                "✨ Build Success: ${currentBuild.fullDisplayName}" : 
                "❗ Build Failure: ${currentBuild.fullDisplayName}"

            // Create the email body
            def body = """
                <html>
                <head>
                    <style>
                        @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;600&display=swap');
                        body {
                            font-family: 'Poppins', sans-serif;
                            background-color: #101010;
                            color: #ffffff;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 800px;
                            margin: 20px auto;
                            padding: 30px;
                            background: linear-gradient(145deg, #1a1a2e, #0f3460);
                            border-radius: 15px;
                            box-shadow: 0 0 20px rgba(0, 255, 255, 0.6);
                        }
                        h2 {
                            color: ${currentBuild.currentResult == 'SUCCESS' ? '#00ff88' : '#ff0044'};
                            text-align: center;
                            font-size: 30px;
                            margin-bottom: 20px;
                            text-shadow: 0 0 10px #00ff88, 0 0 20px ${currentBuild.currentResult == 'SUCCESS' ? '#00ff88' : '#ff0044'};
                        }
                        p, th, td {
                            font-size: 16px;
                            line-height: 1.6;
                        }
                        .summary {
                            display: flex;
                            flex-direction: column;
                            margin-top: 25px;
                            padding: 15px;
                            background-color: rgba(255, 255, 255, 0.05);
                            border-radius: 10px;
                            border: 1px solid #3d84a8;
                            box-shadow: 0 0 10px rgba(61, 132, 168, 0.5);
                        }
                        .summary-item {
                            background-color: rgba(50, 50, 50, 0.8);
                            border-radius: 8px;
                            padding: 12px;
                            margin-top: 10px;
                            font-size: 15px;
                            opacity: 0;
                            transform: translateX(-20px);
                            animation: slideIn 0.6s forwards;
                        }
                        .report-link {
                            display: inline-block;
                            padding: 10px 15px;
                            border-radius: 6px;
                            background-color: #3d84a8;
                            color: #ffffff;
                            text-decoration: none;
                            font-weight: bold;
                            transition: background-color 0.3s;
                            margin-top: 10px;
                        }
                        .report-link:hover {
                            background-color: #00bfff;
                        }
                        .footer {
                            font-size: 13px;
                            color: #aaaaaa;
                            text-align: center;
                            margin-top: 30px;
                        }
                        @keyframes slideIn {
                            to {
                                opacity: 1;
                                transform: translateX(0);
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>${subject}</h2>
                        <p>Hello Team,</p>
                        <p>The Jenkins build for the project <strong>${env.JOB_NAME}</strong> has completed. Here is the overview:</p>
                        <div class="summary">
                            <div class="summary-item">🔹 <strong>Build Number:</strong> ${currentBuild.number}</div>
                            <div class="summary-item">🔹 <strong>Project:</strong> ${env.JOB_NAME}</div>
                            <div class="summary-item">🔹 <strong>Build Duration:</strong> ${currentBuild.durationString}</div>
                            <div class="summary-item">🔹 <strong>Result:</strong> <span style="color: ${currentBuild.currentResult == 'SUCCESS' ? '#00ff88' : '#ff0044'};">${currentBuild.currentResult}</span></div>
                        </div>

                        <p>Access Reports:</p>
                        <a href="${env.BUILD_URL}artifact/target/site/jacoco/index.html" class="report-link">📊 JaCoCo Coverage Report</a><br>
                        <a href="${env.BUILD_URL}artifact/dependency-check-report.html" class="report-link">⚠️ OWASP Dependency-Check Report</a><br>
                        <a href="${env.BUILD_URL}artifact/tmp/lynis_reports/lynis-report.html" class="report-link">🛡️ Lynis Security Report</a><br>

                        <div class="footer">
                            <p>Generated by Jenkins CI/CD Pipeline, Team DevOps</p>
                            <p>Admin: Rayen</p>
                        </div>
                    </div>
                </body>
                </html>
            """

            // Send the email
            emailext subject: subject,
                     body: body,
                     mimeType: 'text/html',
                     attachmentsPattern: 'target/site/jacoco/*.html, dependency-check-report.html, tmp/lynis_reports/lynis-report.html',
                     to: 'rayenbal55@gmail.com'
        }
    }
}


    }
}
