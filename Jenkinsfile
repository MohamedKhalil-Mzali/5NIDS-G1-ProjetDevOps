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

        stage('SonarQube Analysis') {
            steps {
                script {
                    def sonarProjectKey = 'nawel'  
                    def sonarProjectName = 'nawel'  
                    def sonarHostUrl = 'http://192.168.33.10:9000'  
                    def sonarLogin = 'sqb_1fd79ddeea85094b5dd84f59cc0778457903de54'  // Nouveau token

                    sh "mvn sonar:sonar -Dsonar.projectKey=${sonarProjectKey} " +
                       "-Dsonar.projectName='${sonarProjectName}' " +
                       "-Dsonar.host.url=${sonarHostUrl} " +
                       "-Dsonar.login=${sonarLogin}"
                }
            }
        }
    }
}
