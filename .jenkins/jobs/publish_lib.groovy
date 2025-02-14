pipelineJob('onx-Numeral-js/publish') {
    description('publish onx-Numeral-js to artifactory')
    parameters {
        stringParam('branch_name', 'master', 'Branch from onx-Numeral-js repo')
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github('omni3x/Numeral-js', 'ssh')
                        credentials('github-molly-brown-ssh-key')
                        refspec('+refs/heads/${branch_name}:refs/remotes/origin/${branch_name}')
                    }
                    branch('origin/${branch_name}')
                    extensions {
                        cloneOptions {
                            depth(1)
                            honorRefspec(true)
                            noTags(true)
                            shallow(true)
                            reference('/var/lib/gitcache/Numeral-js.git')
                        }
                    }
                }
            }
            scriptPath('.jenkins/jenkinsfiles/build_and_publish.Jenkinsfile')
        }
    }
    logRotator {
        artifactDaysToKeep(90)
        artifactNumToKeep(150)
        daysToKeep(90)
        numToKeep(150)
    }
}