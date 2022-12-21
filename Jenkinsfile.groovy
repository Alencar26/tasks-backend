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
          dir('api-test') {
            git branch: 'main', url: 'https://github.com/Alencar26/tasks-api-test'
            sh 'mvn test' 
          }
        }
      }
      stage('Build Frontend') {
        steps {
           dir('frontend') {
              git branch: 'master', url: 'https://github.com/Alencar26/tasks-frontend'
              sh 'mvn clean package -DskipTests=true'
          }
        }
      }
      stage('Deploy Frontend') {
          steps {
              dir('frontend') {
                deploy adapters: [tomcat8(credentialsId: 'TomCatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
      stage('Functional Tests') {
         steps {
          dir('functional-test') {
            git branch: 'main', url: 'https://github.com/Alencar26/tasks-functionar-test'
            sh 'mvn test' 
          }
        }
      }
      stage('Deploy Producao') {
          steps {
              sh 'docker compose build'
              sh 'docker compose up -d'
            }
        }
      stage("Health Check") {
          steps {
              sleep(5)
              dir('functional-test') {
                  sh 'mvn verify -Dskip.surefire.tests'
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, api-test/target/surefire-reports/*xml, functional-test/target/surefire-reports/*.xml, functional-test/target/failsafe-reports/*.xml'
          }
      }
  }
