pipeline {
    agent any

    environment {
        POSTGRES_HOST = 'localhost'
        POSTGRES_PORT = '5432'
        POSTGRES_DB = credentials('postgres-db')
        POSTGRES_USER = credentials('postgres-user')
        POSTGRES_PASSWORD = credentials('postgres-password')
        LOCALSTACK_AUTH_TOKEN = credentials('localstack-auth-token')
    }

    stages {

        stage('Create .env') {
            steps {
                bat '''
                    (
                        echo POSTGRES_DB=%POSTGRES_DB%
                        echo POSTGRES_USER=%POSTGRES_USER%
                        echo POSTGRES_PASSWORD=%POSTGRES_PASSWORD%
                        echo POSTGRES_HOST=localhost
                        echo POSTGRES_PORT=5432
                        echo LOCALSTACK_AUTH_TOKEN=%LOCALSTACK_AUTH_TOKEN%
                    ) > .env
                '''
            }
        }

        stage('Create test.properties') {
            steps {
                bat '''
                    if not exist "src\\test\\resources" mkdir "src\\test\\resources"

                    (
                        echo api.base.url=http://localhost:8080
                        echo db.url=jdbc:postgresql://localhost:5432/%POSTGRES_DB%
                        echo db.username=%POSTGRES_USER%
                        echo db.password=%POSTGRES_PASSWORD%
                        echo kafka.bootstrap.servers=localhost:9092
                    ) > "src\\test\\resources\\test.properties"
                '''
            }
        }

        stage('Start infrastructure') {
            steps {
                bat '''
                    docker compose --env-file .env -f docker/docker-compose.yml up -d

                    echo Waiting for infrastructure to start...
                    powershell -Command "Start-Sleep -Seconds 10"

                    docker compose --env-file .env -f docker/docker-compose.yml ps
                '''
            }
        }

        stage('Start application') {
              steps {
                    powershell '''
                        Start-Process -FilePath ".\\mvnw.cmd" -ArgumentList "spring-boot:run"
                        Start-Sleep -Seconds 10
                    '''
                }
        }

        stage('Run tests') {
            steps {
                bat '.\\mvnw.cmd clean test -Dtest=NotificationsTests'
            }
        }
    }

    post {
        always {
            allure([
                results: [[path: 'target/allure-results']]
            ])

            bat 'docker compose --env-file .env -f docker/docker-compose.yml down'

            bat 'if exist ".env" del /Q ".env"'

            bat 'if exist "src\\test\\resources\\test.properties" del /Q "src\\test\\resources\\test.properties"'
        }
    }
}