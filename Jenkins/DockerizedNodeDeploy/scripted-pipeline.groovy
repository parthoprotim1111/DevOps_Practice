// Jenkins Scripted Pipeline
// This pipeline builds, tests, and deploys a Node.js project with Docker integration.

node('node') { // Run the pipeline on a Jenkins agent named 'node'

    try {
        // Set the overall build result to "SUCCESS" initially
        currentBuild.result = "SUCCESS"

        // Stage: Checkout - Check out the project's source code
        stage('Checkout') {
            checkout scm
        }

        // Stage: Test - Run tests for the Node.js project
        stage('Test') {
            env.NODE_ENV = "test" // Set the NODE_ENV environment variable to "test"
            echo "Environment will be: ${env.NODE_ENV}"
            sh 'node -v' // Print the current version of Node.js
            sh 'npm prune' // Remove unused npm packages
            sh 'npm install' // Install project dependencies
            sh 'npm test' // Run tests using npm
        }

        // Stage: Build Docker - Build a Docker image for the Node.js project
        stage('Build Docker') {
            sh './dockerBuild.sh' // Execute a shell script to build the Docker image
        }

        // Stage: Deploy - Deploy the Docker container
        stage('Deploy') {
            echo 'Push to Repository'
            sh './dockerPushToRepo.sh' // Execute a shell script to push the Docker image to a repository
            echo 'ssh deploy@xxxxx.xxxxx.com running/xxxxx/dockerRun.sh' // Run the deployed container on a remote server (example command)
        }

        // Stage: Cleanup - Perform cleanup actions
        stage('Cleanup') {
            echo 'Prune and cleanup'
            sh 'npm prune' // Prune unused npm packages again (optional)
            sh 'rm node_modules -rf' // Delete the node_modules directory
        }
        
    } catch (err) {
        // If any error occurs during pipeline execution:

        // Set the overall build result to "FAILURE"
        currentBuild.result = "FAILURE"

        // Send an email notification about the build failure
        mail body: "Project build error is here: ${env.BUILD_URL}",
            from: 'xxxx@yyyy.com',
            replyTo: 'yyyy@yyyy.com',
            subject: 'project build failed',
            to: 'zzzzz@zzzz.com'

        // Propagate the error to the Jenkins pipeline system
        throw err
    }
}
