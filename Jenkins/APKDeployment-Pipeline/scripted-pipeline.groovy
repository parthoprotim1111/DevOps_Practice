// Define a function to extract the Android flavor from the branch name.
// The @NonCPS annotation is used to ensure that this function can perform blocking operations safely
@NonCPS
def extractFlavor(branchName) {
    def matcher = (branchName =~ /QA_([a-z_]+)/)
    assert matcher.matches()
    matcher[0][1]
}

// Define the main pipeline function
def buildPipeline() {
    // Start the pipeline execution on any available agent
    node {
        // Stage Checkout
        stage('Checkout') {
            // Checkout code from the repository and update any submodules
            checkout scm
            sh 'git submodule update --init'
        }

        // Stage Build
        stage('Build') {
            // Get the branch name from Jenkins environment variables
            def branchName = env.BRANCH_NAME
            echo "My branch is: ${branchName}"

            // Extract the flavor from the branch name using the previously defined function
            def appFlavor = extractFlavor(branchName)
            echo "Building flavor ${appFlavor}"

            // Build the Gradle flavor, passing the current build number as a parameter to Gradle
            sh "./gradlew clean assemble${appFlavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
        }

        // Stage Archive
        stage('Archive') {
            // Tell Jenkins to archive the APKs
            archiveArtifacts artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true
        }

        // Stage Upload To Fabric
        stage('Upload To Fabric') {
            // Upload the APKs to Firebase Crashlytics using the extracted flavor
            def appFlavor = extractFlavor(env.BRANCH_NAME)
            sh "./gradlew crashlyticsUploadDistribution${appFlavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
        }
    }
}

// Call the buildPipeline function to initiate the pipeline execution
buildPipeline()
