pipeline {

 agent any

 tools {jdk 'JAVA_HOME’, maven 'M2_HOME'}

 stages {

 stage('GIT') {

           steps {

               git branch: 'MohamedKhalilMzali-5NIDS1-G1',

               url: ' https://github.com/MohamedKhalil-Mzali/5NIDS-G1-ProjetDevOps.git'

          }

     }

 stage ('Compile Stage') {

 steps {

 sh 'mvn clean compile'

 }

 }

 }

}
