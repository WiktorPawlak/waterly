pipeline {
  agent any

  environment {
  	DOCKERHUB_CREDENTIALS=credentials('dockerhub-cred')
  }

  stages {

    stage('Stop nginx and payara containers') {
      steps {
        dir('./deployment/nginx') {
          sh 'chmod +x ./stop-nginx.sh'
          sh './stop-nginx.sh'
        }
        dir('./deployment/payara') {
           sh 'chmod +x ./stop-payara.sh'
           sh './stop-payara.sh'
        }
      }
    }

    stage('Build WAR') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

// TEST STAGE DISABLED DUE TO LIMITED RESOURCES ON PRODUCTION
//     stage('Run Backend Tests') {
//       steps {
//         sh 'mvn test'
//       }
//     }

    stage('Build SPA') {
      steps {
        dir('./frontend') {
          sh 'pnpm install --no-frozen-lockfile'
          sh 'pnpm run build'
        }
      }
    }

    stage('Login to dockerhub') {
      steps {
        sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
      }
    }

    stage('Build Docker image for Payara and push') {
      steps {
        dir('./deployment/payara') {
          sh 'chmod +x ./build-payara.sh'
          sh './build-payara.sh'
          sh 'docker tag payara-img matino1/ssbd06:payara-1'
          sh 'docker push matino1/ssbd06:payara-1'
        }
      }
    }

    stage('Build Docker image for Nginx and push') {
      steps {
        dir('./deployment/nginx') {
          sh 'chmod +x ./build-nginx.sh'
          sh './build-nginx.sh'
          sh 'docker tag nginx-img matino1/ssbd06:nginx-1'
          sh 'docker push matino1/ssbd06:nginx-1'
        }
      }
    }

    stage('Deploy WAR to Payara') {
      steps {
        dir('./deployment/payara') {
          sh 'chmod +x ./deploy-payara.sh'
          sh './deploy-payara.sh'
        }
      }
    }

    stage('Deploy SPA to Nginx') {
      steps {
        dir('./deployment/nginx') {
          sh 'chmod +x ./deploy-nginx.sh'
          sh './deploy-nginx.sh'
        }
      }
    }
  }

  post {
    always {
        sh 'docker logout'
    }
  }
}
