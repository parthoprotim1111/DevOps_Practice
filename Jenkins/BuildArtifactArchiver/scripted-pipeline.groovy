// Jenkinsfile (Scripted Pipeline)

node {
    // Stage 1: Create build output
    stage("Create build output") {
        // Create the "output" directory if it doesn't exist
        sh "mkdir -p output"

        // Write a useful file, which will be archived
        writeFile file: "output/usefulfile.txt", text: "This file is useful, need to archive it."

        // Write a useless file, which will not be archived
        writeFile file: "output/uselessfile.md", text: "This file is useless, no need to archive it."
    }

    // Stage 2: Archive build output
    stage("Archive build output") {
        // Archive the build output artifacts (only the *.txt files)
        archiveArtifacts artifacts: 'output/*.txt', excludes: 'output/*.md'
    }
}
