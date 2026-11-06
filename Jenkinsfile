pipeline {
    agent any
    tools {
        maven 'Maven 3.8.6'
        jdk 'JDK21'
    }
    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    triggers { pollSCM('H/5 * * * *') }
    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }
        stage('Build') {
            steps {
                script {
                    if (isUnix()) { sh 'mvn -B -ntp clean compile' }
                    else { bat 'mvn -B -ntp clean compile' }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    if (isUnix()) { sh 'mvn -B -ntp test' }
                    else { bat 'mvn -B -ntp test' }
                }
            }
            post {
                always { junit 'target/surefire-reports/*.xml' }
            }
        }
        stage('Package') {
            steps {
                script {
                    if (isUnix()) { sh 'mvn -B -ntp -DskipTests package' }
                    else { bat 'mvn -B -ntp -DskipTests package' }
                }
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        stage('Run') {
            steps {
                script {
                    if (isUnix()) { sh 'java -jar target/my-maven-app-1.0.0.jar' }
                    else { bat 'java -jar target/my-maven-app-1.0.0.jar' }
                }
            }
        }
    }
    post {
        success { echo 'Pipeline completed successfully.' }
        failure { echo 'Pipeline failed. Check Console Output and Test Result.' }
        cleanup { deleteDir() }
    }
}
