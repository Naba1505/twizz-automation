pipeline {
  agent any
  tools {
    jdk 'JDK21'
    maven 'Maven3.9'
  }

  options {
    timestamps()
    ansiColor('xterm')
  }

  parameters {
    choice(name: 'RUN_SUITE', choices: ['build_only', 'sequential_xml', 'parallel_xml'], description: 'Select which test suite to run')
    string(name: 'MAVEN_OPTS_EXTRA', defaultValue: '', description: 'Extra Maven properties, e.g. -Dplaywright.cli.install=true')
  }

  environment {
    MAVEN_CMD = 'mvn -B'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        script {
          if (isUnix()) {
            sh "${MAVEN_CMD} clean install -DskipTests ${params.MAVEN_OPTS_EXTRA}"
          } else {
            bat "${MAVEN_CMD} clean install -DskipTests %MAVEN_OPTS_EXTRA%"
          }
        }
      }
    }

    stage('Install Playwright Browsers (first run)') {
      when { expression { return params.RUN_SUITE != 'build_only' } }
      steps {
        // Safe no-op if already installed
        script {
          if (isUnix()) {
            sh "${MAVEN_CMD} -Dplaywright.cli.install=true test -DskipTests ${params.MAVEN_OPTS_EXTRA}"
          } else {
            bat "${MAVEN_CMD} -Dplaywright.cli.install=true test -DskipTests %MAVEN_OPTS_EXTRA%"
          }
        }
      }
    }

    stage('Test') {
      when { expression { return params.RUN_SUITE != 'build_only' } }
      steps {
        script {
          def suiteArg = ''
          if (params.RUN_SUITE == 'sequential_xml') {
            suiteArg = '"-Dsurefire.suiteXmlFiles=testng.xml"'
          } else if (params.RUN_SUITE == 'parallel_xml') {
            suiteArg = '"-Dsurefire.suiteXmlFiles=testng-parallel.xml"'
          }
          if (isUnix()) {
            sh "${MAVEN_CMD} ${suiteArg} test ${params.MAVEN_OPTS_EXTRA}"
          } else {
            bat "${MAVEN_CMD} ${suiteArg} test %MAVEN_OPTS_EXTRA%"
          }
        }
      }
      post {
        always {
          // Archive common artifacts
          archiveArtifacts allowEmptyArchive: true, artifacts: 'traces/**/*.zip, screenshots/**/*, target/surefire-reports/**/*'
          // Publish Allure report if Jenkins Allure plugin is installed
          script {
            try {
              allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            } catch (err) {
              echo "Allure plugin not configured or results missing: ${err}"
            }
          }
        }
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }
  }
}
