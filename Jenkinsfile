pipeline {
  agent any
  tools {
    jdk 'JDK21'
    maven 'Maven3.9'
  }

  options {
    timestamps()
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
          // Resolve tool homes and export JAVA_HOME/M2_HOME + PATH explicitly
          def jdkHome = tool name: 'JDK21', type: 'jdk'
          def mvnHome = tool name: 'Maven3.9', type: 'maven'
          def mvnCmdAbs = isUnix() ? "${mvnHome}/bin/mvn" : "${mvnHome}\\bin\\mvn.cmd"
          withEnv([
            "JAVA_HOME=${jdkHome}",
            "M2_HOME=${mvnHome}",
            isUnix() ? "PATH+JAVA=${jdkHome}/bin" : "Path+JAVA=${jdkHome}\\bin",
            isUnix() ? "PATH+MAVEN=${mvnHome}/bin" : "Path+MAVEN=${mvnHome}\\bin",
            "MVN=${mvnCmdAbs}"
          ]) {
            if (isUnix()) {
              sh 'echo Using JAVA_HOME=$JAVA_HOME && $JAVA_HOME/bin/java -version || true'
              sh 'echo Using MVN=$MVN && $MVN -v || true'
              sh "${MAVEN_CMD} clean install -DskipTests ${params.MAVEN_OPTS_EXTRA}"
            } else {
              bat 'echo JAVA_HOME=%JAVA_HOME%'
              bat '"%JAVA_HOME%\\bin\\java.exe" -version'
              bat 'echo MVN=%MVN%'
              bat '"%MVN%" -v'
              bat '"%MVN%" -B clean install -DskipTests %MAVEN_OPTS_EXTRA%'
            }
          }
        }
      }
    }

    stage('Install Playwright Browsers (first run)') {
      when { expression { return params.RUN_SUITE != 'build_only' } }
      steps {
        // Safe no-op if already installed
        script {
          def jdkHome = tool name: 'JDK21', type: 'jdk'
          def mvnHome = tool name: 'Maven3.9', type: 'maven'
          def mvnCmdAbs = isUnix() ? "${mvnHome}/bin/mvn" : "${mvnHome}\\bin\\mvn.cmd"
          withEnv([
            "JAVA_HOME=${jdkHome}",
            "M2_HOME=${mvnHome}",
            isUnix() ? "PATH+JAVA=${jdkHome}/bin" : "Path+JAVA=${jdkHome}\\bin",
            isUnix() ? "PATH+MAVEN=${mvnHome}/bin" : "Path+MAVEN=${mvnHome}\\bin",
            "MVN=${mvnCmdAbs}"
          ]) {
            if (isUnix()) {
              sh 'echo Using JAVA_HOME=$JAVA_HOME && $JAVA_HOME/bin/java -version || true'
              sh 'echo Using MVN=$MVN && $MVN -v || true'
              sh "${MAVEN_CMD} -Dplaywright.cli.install=true test -DskipTests ${params.MAVEN_OPTS_EXTRA}"
            } else {
              bat 'echo JAVA_HOME=%JAVA_HOME%'
              bat '"%JAVA_HOME%\\bin\\java.exe" -version'
              bat 'echo MVN=%MVN%'
              bat '"%MVN%" -v'
              bat '"%MVN%" -B -Dplaywright.cli.install=true test -DskipTests %MAVEN_OPTS_EXTRA%'
            }
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
            suiteArg = '-Dsurefire.suiteXmlFiles=testng.xml'
          } else if (params.RUN_SUITE == 'parallel_xml') {
            suiteArg = '-Dsurefire.suiteXmlFiles=testng-parallel.xml'
          }
          def jdkHome = tool name: 'JDK21', type: 'jdk'
          def mvnHome = tool name: 'Maven3.9', type: 'maven'
          def mvnCmdAbs = isUnix() ? "${mvnHome}/bin/mvn" : "${mvnHome}\\bin\\mvn.cmd"
          withEnv([
            "JAVA_HOME=${jdkHome}",
            "M2_HOME=${mvnHome}",
            isUnix() ? "PATH+JAVA=${jdkHome}/bin" : "Path+JAVA=${jdkHome}\\bin",
            isUnix() ? "PATH+MAVEN=${mvnHome}/bin" : "Path+MAVEN=${mvnHome}\\bin",
            "MVN=${mvnCmdAbs}"
          ]) {
            if (isUnix()) {
              sh 'echo Using JAVA_HOME=$JAVA_HOME && $JAVA_HOME/bin/java -version || true'
              sh 'echo Using MVN=$MVN && $MVN -v || true'
              sh "${MAVEN_CMD} ${suiteArg} test ${params.MAVEN_OPTS_EXTRA}"
            } else {
              bat 'echo JAVA_HOME=%JAVA_HOME%'
              bat '"%JAVA_HOME%\\bin\\java.exe" -version'
              bat 'echo MVN=%MVN%'
              bat '"%MVN%" -v'
              bat "\"%MVN%\" -B ${suiteArg} test %MAVEN_OPTS_EXTRA%"
            }
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
