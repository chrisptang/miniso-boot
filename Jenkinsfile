pipeline {
    environment {
        //以下环境变量根据实际项目信息进行变更
        APP_NAME = 'leqee-boot'
    }
    agent any
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Build') {
            steps {
                echo 'Build jar and upload them onto maven repo..'
                sh 'mvn deploy -Dmaven.test.skip -U'
            }
        }
        stage('Prepare Post Actions'){
            steps {
                echo 'About to checkout deployment script...'
                sh 'pwd ; ls -l'
                dir("${env.WORKSPACE}/infra"){
                    checkout([$class: 'GitSCM', branches: [[name: 'master']],
                        userRemoteConfigs: [[url: 'https://git.leqee.com/promotion/infra']]])
                    sh "chmod +x ./ansible/post_deploy.sh"
                }
            }
        }
    }
    post {
        success {
            dir("${env.WORKSPACE}/infra/ansible"){
                withEnv(["JENKINS_BUILD_STATUS=success"]){
                    sh "./post_deploy.sh"
                }
            }
        }
        unsuccessful {
            dir("${env.WORKSPACE}/infra/ansible"){
                withEnv(["JENKINS_BUILD_STATUS=unsuccessful"]){
                    sh "./post_deploy.sh"
                }
            }
        }
    }
}