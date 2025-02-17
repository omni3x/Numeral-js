def tag = ''

pipeline {
  agent { label 'linux' }

  environment {
    // Credential ID from https://jenkins.service.internal.projecticeland.net/credentials/
    ARTIFACTORY_USER = 'jenkins'
    ARTIFACTORY_PASSWORD = credentials('36e76c3b-881f-417d-ab6c-b65917a48477')
  }

  stages {
    stage('Get Current Version') {
      steps {            
        script {
           withCredentials([sshUserPrivateKey(credentialsId: 'github-molly-brown-ssh-key', keyFileVariable: 'GITHUB_KEY')]) {
            withEnv(["GIT_SSH_COMMAND=ssh -i $GITHUB_KEY"]) {
              // find the latest tag from remote, default to 1.0.0 if it doesn't exist
              sh "git remote set-url origin git@github.com:omni3x/Numeral-js.git"
              def command = $/ git ls-remote --quiet --tags --refs | awk -v def=1.0.0 -F\\\/ '{ print $3 } END { if(NR==0) {print def} }' | sort -V | tail -n 1/$
              def version = sh(returnStdout: true, script: command).trim()

              echo "found version ${version}"
              echo "Current Branch ${env.GIT_BRANCH}"

              def versions = version.split('\\.')
              major = versions[0]
              minor = versions[1]
              patch = versions[2]
              tag = "${major}.${minor}.${patch}"
              echo "new version ${tag}"
            }
          } 
        }
      }
    }

    stage('publish ONX Numeral-js') {
      steps {
        sh '''
          export ARTIFACTORY_TOKEN="$(echo -n ${ARTIFACTORY_USER}:${ARTIFACTORY_PASSWORD} | base64)"

          #echo "cafile=$GEMINI_INTERNAL_ROOT" >> .npmrc
          #echo "always-auth=true" >> .npmrc
          #echo "email=jenkins@projecticeland.net" >> .npmrc
          #echo "//artifactory.service.internal.projecticeland.net/artifactory/api/npm/:_auth=$ARTIFACTORY_TOKEN" >> .npmrc

          # Must point to npm-local for publishing
          #echo "registry=https://artifactory.service.internal.projecticeland.net/artifactory/api/npm/npm-local" >> .npmrc

          cp $GEMINI_INTERNAL_ROOT .
          
          docker build --build-arg GEMINI_INTERNAL_ROOT=$GEMINI_INTERNAL_ROOT --build-arg ARTIFACTORY_TOKEN=$ARTIFACTORY_TOKEN . 
	      '''
      }
    }
  }

  post {
    success {
      slackSend(
        channel: '#eotc-jenkins',
        message: "SUCCESS: onx Numeral-js was released. version: ${tag}. '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})",
        color: 'good',
        teamDomain: 'iceland',
        tokenCredentialId: '2b40808d-04b7-415b-8401-a4d500a84aab'
      )
    }
    failure {
      slackSend(
        channel: '#eotc-jenkins',
        message: "FAILED: onx Numeral-js failed to be released. version: ${tag}. '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})",
        color: 'danger',
        teamDomain: 'iceland',
        tokenCredentialId: '2b40808d-04b7-415b-8401-a4d500a84aab'
      )
    }
  }  
}