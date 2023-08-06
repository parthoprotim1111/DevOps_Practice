// Jenkins Declarative Pipeline
pipeline {
    // Agent section: Define where the pipeline will run (on any available agent)
    agent any
    
    // Stages section: Define the stages of the pipeline and their steps
    stages {
        // Stage: Checkout
        stage('Stage Checkout') {
            // Steps to be executed in this stage
            steps {
                // Checkout code from repository and update any submodules
                checkout scm
                sh 'git submodule update --init'
            }
        }
        
        // Stage: Build
        stage('Stage Build') {
            // Steps to be executed in this stage
            steps {
                // Get the branch name from Jenkins environment variables
                def branchName = env.BRANCH_NAME
                echo "My branch is: ${branchName}"

                // Extract the flavor from the branch name
                def appFlavor = extractFlavor(branchName)
                echo "Building flavor ${appFlavor}"

                // Build the Gradle flavor, passing the current build number as a parameter to Gradle
                sh "./gradlew clean assemble${appFlavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
            }
        }

        // Stage: Archive
        stage('Stage Archive') {
            // Steps to be executed in this stage
            steps {
                // Tell Jenkins to archive the APKs
                archiveArtifacts artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true
            }
        }

        // Stage: Upload To Fabric
        stage('Stage Upload To Fabric') {
            // Steps to be executed in this stage
            steps {
                // Upload the APKs to Firebase Crashlytics using the extracted flavor
                def appFlavor = extractFlavor(env.BRANCH_NAME)
                sh "./gradlew crashlyticsUploadDistribution${appFlavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
            }
        }
    }
}

// Function to extract the Android flavor from the branch name
@NonCPS
def extractFlavor(branchName) {
    def matcher = (branchName =~ /QA_([a-z_]+)/)
    assert matcher.matches()
    matcher[0][1]
}
