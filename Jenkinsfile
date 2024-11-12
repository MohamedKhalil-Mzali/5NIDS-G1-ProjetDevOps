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
       TWILIO_ACCOUNT_SID = credentials('TWILIO_ACCOUNT_SID')  
    TWILIO_AUTH_TOKEN = credentials('TWILIO_AUTH_TOKEN')    

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

        stage('Send SMS Notification') {
    steps {
        script {
            // Use withCredentials block to securely inject credentials
            withCredentials([string(credentialsId: 'TWILIO_ACCOUNT_SID', variable: 'TWILIO_ACCOUNT_SID'),
                 string(credentialsId: 'TWILIO_AUTH_TOKEN', variable: 'TWILIO_AUTH_TOKEN')]) {
    
    def message = """
    Build Status: ${currentBuild.currentResult}
    Build Number: ${currentBuild.number}
    Project: ${env.JOB_NAME}
    Duration: ${currentBuild.durationString}
    Result: ${currentBuild.currentResult == 'SUCCESS' ? '✅ Success' : '❌ Failure'}
    
    Commit Message: ${currentBuild.changeSets ? currentBuild.changeSets[0].items[0].msg : 'No changes'}
    Build URL: ${env.BUILD_URL}
    """

    // Send SMS using Twilio API with curl
    sh """
    curl 'https://api.twilio.com/2010-04-01/Accounts/${TWILIO_ACCOUNT_SID}/Messages.json' -X POST --data-urlencode 'To=+21628221389' --data-urlencode 'MessagingServiceSid=MG6f26b98c01c74e1ecef4eacb9ccd7b3e' --data-urlencode 'Body=${message}' -u ${TWILIO_ACCOUNT_SID}:${TWILIO_AUTH_TOKEN}
"""
}



        }
    }
        }

stage('Send Email Notification') {
    steps {
        script {
            // Determine the subject based on build result
            def subject = currentBuild.currentResult == 'SUCCESS' ? 
                "✅ Build Success: ${currentBuild.fullDisplayName}" : 
                "❌ Build Failure: ${currentBuild.fullDisplayName}"

            // Create the email body
            def body = """
                <html>
                <head>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            background-color: #f4f6f9;
                            color: #333333;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30px auto;
                            padding: 20px;
                            background-color: #ffffff;
                            border-radius: 8px;
                            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                        }
                        h2 {
                            text-align: center;
                            font-size: 28px;
                            margin-bottom: 20px;
                            color: ${currentBuild.currentResult == 'SUCCESS' ? '#28a745' : '#dc3545'};
                        }
                        .divider {
                            height: 2px;
                            background-color: #f1f1f1;
                            margin: 20px 0;
                        }
                        p {
                            font-size: 16px;
                            line-height: 1.6;
                            color: #555555;
                        }
                        .summary {
                            display: flex;
                            flex-wrap: wrap;
                            justify-content: space-between;
                            margin-top: 20px;
                        }
                        .summary-item {
                            background-color: #f8f9fa;
                            border-radius: 6px;
                            padding: 15px;
                            width: 48%;
                            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                            margin-bottom: 15px;
                        }
                        .summary-item strong {
                            color: #333333;
                        }
                        .report-links {
                            margin-top: 20px;
                        }
                        .report-link {
                            display: inline-block;
                            padding: 12px 18px;
                            border-radius: 6px;
                            background-color: #007bff;
                            color: #ffffff;
                            text-decoration: none;
                            font-weight: bold;
                            font-size: 14px;
                            margin-top: 10px;
                            transition: background-color 0.3s;
                        }
                        .report-link:hover {
                            background-color: #0056b3;
                        }
                        .footer {
                            font-size: 14px;
                            color: #888888;
                            text-align: center;
                            margin-top: 30px;
                        }
                        .footer span {
                            color: #007bff;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>${subject}</h2>
                        <hr class="divider" />
                        <p>Dear Team,</p>
                        <p>The Jenkins build for the project <strong>${env.JOB_NAME}</strong> has completed. Below is a quick summary:</p>
                        <div class="summary">
                            <div class="summary-item">🔹 <strong>Build Number:</strong> ${currentBuild.number}</div>
                            <div class="summary-item">🔹 <strong>Project:</strong> ${env.JOB_NAME}</div>
                            <div class="summary-item">🔹 <strong>Build Duration:</strong> ${currentBuild.durationString}</div>
                            <div class="summary-item">🔹 <strong>Result:</strong> <span style="color: ${currentBuild.currentResult == 'SUCCESS' ? '#28a745' : '#dc3545'};">${currentBuild.currentResult}</span></div>
                        </div>
                        <p>Detailed Reports:</p>
                        <div class="report-links">
                            <a href="${env.BUILD_URL}artifact/target/site/jacoco/index.html" class="report-link">📊 JaCoCo Coverage Report</a>
                            <a href="${env.BUILD_URL}artifact/dependency-check-report.html" class="report-link">⚠️ OWASP Dependency-Check Report</a>
                            <a href="${env.BUILD_URL}artifact/tmp/lynis_reports/lynis-report.html" class="report-link">🛡️ Lynis Security Report</a>
                        </div>
                        <div class="footer">
                            <p>Generated by <span>Jenkins CI/CD</span>, Team DevOps</p>
                            <p>Contact Admin: Rayen</p>
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
post {
        always {
            echo "Email notification sent."
        }
}

}



    }
}
