pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start infrastructure') {
            steps {
                bat 'docker compose up -d'
            }
        }

        stage('Run tests') {
            steps {
                bat '.\\mvnw.cmd clean test'
            }
        }
    }

    post {
        always {
            allure([
                results: [[path: 'target/allure-results']]
            ])

            bat 'docker compose down'
        }
    }
}