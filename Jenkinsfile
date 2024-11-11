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
            // Get the build cause
            def causes = currentBuild.rawBuild.getCauses()
            def causeMessage = "No cause found."
            if (causes && !causes.isEmpty()) {
                causeMessage = causes[0].getShortDescription()  // Correctly access the cause's shortDescription
            }

            // Determine subject based on build result
            def subject = currentBuild.currentResult == 'SUCCESS' ? 
                "✨ Build Success: ${currentBuild.fullDisplayName}" : 
                "❗ Build Failure: ${currentBuild.fullDisplayName}"

            // Create the email body
            def body = """
                <html>
                <head>
                    <style>
                        body {
                            font-family: 'Roboto', sans-serif;
                            background-color: #1d1f21;
                            color: #e0e0e0;
                        }
                        .container {
                            max-width: 700px;
                            margin: auto;
                            padding: 20px;
                            background-color: #2a2e33;
                            border-radius: 12px;
                            box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.3);
                            font-family: 'Roboto', sans-serif;
                        }
                        h2 {
                            color: ${currentBuild.currentResult == 'SUCCESS' ? '#81C784' : '#FF7043'};
                            text-align: center;
                            font-size: 24px;
                        }
                        p, th, td {
                            font-size: 15px;
                        }
                        table {
                            width: 100%;
                            border-spacing: 0;
                            border-collapse: collapse;
                            margin-top: 15px;
                            background-color: #33363b;
                        }
                        th, td {
                            padding: 12px;
                            border: 1px solid #444;
                        }
                        th {
                            background-color: #42474d;
                            color: #e0e0e0;
                            font-weight: bold;
                        }
                        .status {
                            color: ${currentBuild.currentResult == 'SUCCESS' ? '#81C784' : '#FF7043'};
                        }
                        .footer {
                            font-size: 12px;
                            color: #aaa;
                            text-align: center;
                            margin-top: 20px;
                        }
                        .report-link {
                            color: #64b5f6;
                            text-decoration: none;
                        }
                        .report-link:hover {
                            text-decoration: underline;
                        }
                        .stage-status {
                            display: inline-block;
                            padding: 6px 12px;
                            border-radius: 4px;
                            color: #fff;
                            font-weight: bold;
                        }
                        .stage-success {
                            background-color: #66bb6a;
                        }
                        .stage-failure {
                            background-color: #e57373;
                        }
                        .summary {
                            display: flex;
                            flex-direction: column;
                            margin-top: 20px;
                        }
                        .summary div {
                            background-color: #42474d;
                            border-radius: 8px;
                            padding: 10px;
                            margin-top: 8px;
                            font-size: 14px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>${subject}</h2>
                        <p>Hello Team,</p>
                        <p>The Jenkins build for the project <strong>${env.JOB_NAME}</strong> has completed. Here is the summary:</p>
                        <div class="summary">
                            <div>🔹 <strong>Build Number:</strong> ${currentBuild.number}</div>
                            <div>🔹 <strong>Project:</strong> ${env.JOB_NAME}</div>
                            <div>🔹 <strong>Build Duration:</strong> ${currentBuild.durationString}</div>
                            <div>🔹 <strong>Result:</strong> <span class="status">${currentBuild.currentResult}</span></div>
                            <div>🔹 <strong>Build Cause:</strong> ${causeMessage}</div>
                        </div>

                        <table>
                            <thead>
                                <tr>
                                    <th>Stage</th>
                                    <th>Status</th>
                                    <th>Duration</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% currentBuild.buildCauses.each { cause -> %>
                                    <tr>
                                        <td>${cause.shortDescription}</td>
                                        <td class="stage-status <%= cause.result == 'SUCCESS' ? 'stage-success' : 'stage-failure' %>">${cause.result}</td>
                                        <td>${cause.durationString}</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>

                        <p>Access the reports:</p>
                        <ul>
                            <li><a href="${env.BUILD_URL}artifact/target/site/jacoco/index.html" class="report-link">📊 JaCoCo Coverage Report</a></li>
                            <li><a href="${env.BUILD_URL}artifact/dependency-check-report.html" class="report-link">⚠️ OWASP Dependency-Check Report</a></li>
                            <li><a href="${env.BUILD_URL}artifact/tmp/lynis_reports/lynis-report.html" class="report-link">🛡️ Lynis Security Report</a></li>
                        </ul>

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
