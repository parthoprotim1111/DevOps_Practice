// Jenkins Scripted Pipeline - Node.js Project Build and Deployment with Email Notifications

// Define the Jenkins pipeline to run on a node named 'node'
node('node') {

    try {
        // Set the overall build result to "SUCCESS" initially
        currentBuild.result = "SUCCESS"

        // Stage 1: Checkout - Check out the Node.js project's source code from the version control system (e.g., Git)
        stage('Checkout') {
            checkout scm
        }

        // Stage 2: Test - Run tests for the Node.js project
        stage('Test') {
            // Set the environment variable NODE_ENV to "test" for testing environment
            env.NODE_ENV = "test"
            echo "Environment will be: ${env.NODE_ENV}"

            // Print the current version of Node.js for debugging purposes
            sh 'node -v'

            // Prune unused npm packages to clean up the node_modules directory and optimize testing
            sh 'npm prune'

            // Install project dependencies (npm packages) to ensure test dependencies are available
            sh 'npm install'

            // Run tests for the Node.js project using npm
            sh 'npm test'
        }

        // Stage 3: Build Docker - Build a Docker image for the Node.js project
        stage('Build Docker') {
            // Execute a shell script (dockerBuild.sh) to build the Docker image
            sh './dockerBuild.sh'
        }

        // Stage 4: Deploy - Deploy the Docker container to a server
        stage('Deploy') {
            // Print a message indicating that the project will be pushed to a repository
            echo 'Pushing Docker image to the repository'

            // Execute a shell script (dockerPushToRepo.sh) to push the Docker image to a repository
            sh './dockerPushToRepo.sh'

            // Print a message indicating that the deployed container will be started on a remote server
            echo 'Starting the deployed container on a remote server (example command)'
            sh 'ssh deploy@xxxxx.xxxxx.com running/xxxxx/dockerRun.sh'
        }

        // Stage 5: Cleanup - Perform cleanup actions after the build and deployment
        stage('Cleanup') {
            // Print a message indicating that pruning and cleanup are happening
            echo 'Pruning and cleaning up'

            // Prune unused npm packages again to optimize the final artifact
            sh 'npm prune'

            // Delete the node_modules directory to clean up the workspace and reduce artifacts size
            sh 'rm node_modules -rf'
        }
        
    } catch (err) {
        // If any error occurs during pipeline execution:

        // Set the overall build result to "FAILURE"
        currentBuild.result = "FAILURE"

        // Send an email notification about the build failure using the emailext plugin
        emailext body: "Project build error is here: ${env.BUILD_URL}",
            mimeType: 'text/html',
            subject: 'Project build failed',
            to: 'zzzzz@zzzz.com'

        // Propagate the error to the Jenkins pipeline system
        throw err
    } finally {
        // Send an email notification about the build result (success or failure) using the emailext plugin
        emailext body: "Project build status: ${currentBuild.result}",
            mimeType: 'text/html',
            subject: 'Project build result',
            to: 'yyyy@yyyy.com'
    }
}
