pipeline {
    agent any // Run the pipeline on any available Jenkins agent

    // Define environment variables that can be used throughout the pipeline
    environment {
        NODE_ENV = 'test' // Set default value for the NODE_ENV variable
    }

    // Stages of the pipeline
    stages {
        stage('Checkout') {
            // Check out the Node.js project's source code from the version control system (e.g., Git)
            steps {
                checkout scm
            }
        }

        stage('Test') {
            // Run tests for the Node.js project
            steps {
                echo "Environment will be: ${NODE_ENV}" // Print the NODE_ENV variable
                sh 'node -v' // Print the current version of Node.js
                sh 'npm prune' // Prune unused npm packages
                sh 'npm install' // Install project dependencies
                sh 'npm test' // Run tests using npm
            }
        }

        stage('Build Docker') {
            // Build a Docker image for the Node.js project
            steps {
                sh './dockerBuild.sh' // Execute a shell script to build the Docker image
            }
        }

        stage('Deploy') {
            // Deploy the Docker container to a server
            steps {
                echo 'Pushing Docker image to the repository'
                sh './dockerPushToRepo.sh' // Execute a shell script to push the Docker image to a repository
                echo 'Starting the deployed container on a remote server (example command)'
                sh 'ssh deploy@xxxxx.xxxxx.com running/xxxxx/dockerRun.sh' // Start the deployed container on a remote server
            }
        }

        stage('Cleanup') {
            // Perform cleanup actions after the build and deployment
            steps {
                echo 'Pruning and cleaning up'
                sh 'npm prune' // Prune unused npm packages again (optional)
                sh 'rm node_modules -rf' // Delete the node_modules directory to clean up the workspace
            }
        }
    }

    // Post section to handle notifications and actions after the pipeline completion
    post {
        always {
            // If any error occurred during pipeline execution, set the overall build result to "FAILURE"
            script {
                if (currentBuild.result != 'SUCCESS') {
                    currentBuild.result = 'FAILURE'
                }
            }
        }

        success {
            // Send an email notification if the pipeline succeeds
            emailext (
                subject: 'Project build successful',
                body: 'The build for the Node.js project was successful.',
                to: 'yyyy@yyyy.com',
                from: 'xxxx@yyyy.com',
                replyTo: 'xxxx@yyyy.com',
            )
        }

        failure {
            // Send an email notification if the pipeline fails
            emailext (
                subject: 'Project build failed',
                body: "Project build error is here: ${env.BUILD_URL}",
                to: 'zzzzz@zzzz.com',
                from: 'xxxx@yyyy.com',
                replyTo: 'yyyy@yyyy.com',
            )
        }
    }
}
