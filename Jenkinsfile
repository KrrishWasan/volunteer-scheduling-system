// Jenkins Declarative Pipeline – Volunteer Scheduling System
// Week 8 (Pipeline as Code) + Week 10 (Selenium quality gate) + Week 12 (Docker CD).
//
// Required Jenkins plugins: Pipeline, Git, JUnit, HTML Publisher (optional),
// Docker Pipeline, Credentials Binding.
// Required tools in Jenkins (Manage Jenkins -> Tools):
//   - JDK 17 named "jdk17", Maven named "maven" (auto-install).
// Required credentials (Manage Jenkins -> Credentials):
//   - "dockerhub" (username/password) for the registry push.

pipeline {
    agent any

    parameters {
        // Week 8: at least one parameterised environment setting.
        choice(name: 'APP_ENV', choices: ['dev', 'test', 'prod'], description: 'Target environment')
        string(name: 'APP_VERSION', defaultValue: '', description: 'Release version (default: pom version + build number)')
    }

    environment {
        IMAGE_NAME  = 'krrishwasan/volunteer-scheduling-system'
        APP_ENV     = "${params.APP_ENV}"
        JAVA_HOME   = tool 'jdk17'
        MAVEN_HOME  = tool 'maven'
        PATH        = "${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    triggers {
        // Week 7: build on every commit (poll every 2 minutes; prefer a GitHub webhook).
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.APP_VERSION = params.APP_VERSION?.trim()
                        ? params.APP_VERSION.trim()
                        : sh(script: "mvn -q -Dexec.executable=echo help:evaluate -Dexpression=project.version -DforceStdout evaluate 2>/dev/null || echo 1.0.0",
                             returnStdout: true).trim() + "-${env.BUILD_NUMBER}"
                }
                echo "Building version ${env.APP_VERSION} for environment ${env.APP_ENV}"
            }
        }

        stage('Build & Unit Test') {
            steps {
                // Week 7: Maven build job – compiles, runs unit/integration tests, packages WAR.
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Selenium Quality Gate') {
            steps {
                // Week 10: UI suite runs against the packaged app; failures stop the pipeline.
                sh 'mvn -B test -Pselenium'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('Package & Archive') {
            steps {
                // Week 7: archive the build artefact.
                archiveArtifacts artifacts: 'target/vss.war', fingerprint: true
            }
        }

        stage('Docker Build & Push') {
            steps {
                // Week 12: versioned image published to Docker Hub.
                script {
                    def img = docker.build("${env.IMAGE_NAME}:${env.APP_VERSION}",
                        "--build-arg APP_VERSION=${env.APP_VERSION} --build-arg APP_ENV=${env.APP_ENV} .")
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                        img.push()
                        img.push('latest')
                    }
                }
            }
        }

        stage('Deploy Container') {
            steps {
                // Week 12: fresh container after green tests (also used by Ansible in Week 14).
                sh '''
                    docker stop vss || true
                    docker rm vss || true
                    docker run -d --name vss --restart unless-stopped \\
                      -p 8080:8080 \\
                      -e APP_ENV="$APP_ENV" -e APP_VERSION="$APP_VERSION" \\
                      "$IMAGE_NAME:$APP_VERSION"
                    sleep 20
                    curl -f http://localhost:8080/actuator/health
                '''
            }
        }
    }

    post {
        always {
            echo "Pipeline finished: ${currentBuild.currentResult} (version ${env.APP_VERSION}, env ${env.APP_ENV})"
        }
    }
}
