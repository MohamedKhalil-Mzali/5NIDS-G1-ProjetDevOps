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
                script {
                    def sonarScript = '''
                    #!/bin/bash
                    mvn clean org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.0.2155:sonar \
                      -Dsonar.projectKey=nawel \
                      -Dsonar.projectName="nawel" \
                      -Dsonar.host.url=http://192.168.33.10:9000 \
                      -Dsonar.token=sqb_1fd79ddeea85094b5dd84f59cc0778457903de54
                    '''
                    writeFile file: 'sonar_analysis.sh', text: sonarScript
                    sh 'chmod +x sonar_analysis.sh'
                    sh './sonar_analysis.sh'
                }
            }
        }
    }
}

