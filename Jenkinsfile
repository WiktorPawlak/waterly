pipeline {
  agent any

  stages {
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
          sh 'pnpm install'
          sh 'pnpm run build'
        }
      }
    }

    stage('Build Docker image for Payara') {
      steps {
        dir('./deployment/payara') {
          sh 'chmod +x ./build-payara.sh'
          sh './build-payara.sh'
        }
      }
    }

    stage('Build Docker image for Nginx') {
      steps {
        dir('./deployment/nginx') {
          sh 'chmod +x ./build-nginx.sh'
          sh './build-nginx.sh'
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
}
