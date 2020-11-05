pipeline {
    environment {
        //以下环境变量根据实际项目信息进行变更
        APP_NAME = 'leqee-boot'
    }
    agent any
    options {
        skipStagesAfterUnstable()
    }
    parameters {
        choice(name: 'DEPLOY_TO', choices: ['YES', 'Build-Only'], defaultValue: "Build-Only"
            , description: '是否发布到maven仓库：YES：发布jar包，Build Only：只编译代码；')
    }
    stages {
        stage('Build') {
            when {
                branch 'master'
                expression { params.DEPLOY_TO == 'YES' }
            }
            steps {
                echo 'Build jar and upload them onto maven repo..'
                sh 'mvn deploy -Dmaven.test.skip -U'
            }
        }
        stage('Compile') {
            when {
                expression { params.DEPLOY_TO != 'YES' }
            }
            steps {
                echo 'Compiling source code..'
                sh 'mvn clean package -Dmaven.test.skip -U'
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