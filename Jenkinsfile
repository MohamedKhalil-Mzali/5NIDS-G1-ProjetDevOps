pipeline {
    agent any

    stages {
        stage('Git') {
            steps {
                git branch: 'AchrefWerchfeni-5NIDS1-G1', url: 'https://github.com/MohamedKhalil-Mzali/5NIDS-G1-ProjetDevOps.git'
            }
        }
        stage('Maven') {
            steps {
                sh 'mvn clean'
            }
        }
    }
}
