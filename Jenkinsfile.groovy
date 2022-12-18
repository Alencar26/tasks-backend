pipeline {
  agent any
  stages {
      stage('Build Backend') {
          steps {
              sh 'mvn clean package -DskipTests=true'
            }
        }
      stage('Unit Tests') {
          steps {
              sh 'mvn test'
            }
        }
      stage('Deploy Backend') {
          steps {
              deploy adapters: [tomcat8(credentialsId: 'TomCatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
      stage('API Tests') {
          steps {
            git branch: 'main', url: 'https://github.com/Alencar26/tasks-api-test'
            sh 'mvn test'
            }
        }
    }
  }
