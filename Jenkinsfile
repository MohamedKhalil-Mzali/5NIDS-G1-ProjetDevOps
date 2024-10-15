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
                    // Configuration des propriétés SonarQube
                    def sonarProjectKey = 'mon.projet'  // La clé de votre projet
                    def sonarProjectName = 'mon projet'  // Nom de votre projet
                    def sonarHostUrl = 'http://192.168.33.10:9000'  // URL de votre serveur SonarQube
                    def sonarLogin = 'sqb_4ba87e36c05b50b9f207a969d3ac818eb81b429a'  // Votre token d'authentification

                    // Exécution de l'analyse SonarQube
                    sh "mvn sonar:sonar -Dsonar.projectKey=${sonarProjectKey} " +
                       "-Dsonar.projectName='${sonarProjectName}' " +
                       "-Dsonar.host.url=${sonarHostUrl} " +
                       "-Dsonar.login=${sonarLogin}"
                }
            }
        }
    }
}
