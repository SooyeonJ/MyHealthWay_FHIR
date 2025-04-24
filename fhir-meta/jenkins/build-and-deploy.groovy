#!groovy

String getBranchName(branch) {
    branchTemp = sh returnStdout: true, script: """echo "$branch" |sed -E "s#origin/##g" """
    if (branchTemp) {
        branchTemp = branchTemp.trim()
    }
    return branchTemp
}

node {
    // Gitlab의 프로젝트 이름
    // ##수정포인트: 부그룹, 프로젝트 이름
    def subGroup = 'fhir'
    def project = 'fhir-meta'
//    def startedBy = '{Unknown}'
    def defaultBranch = 'dev'
    def branchName
    def gitCommitId
    // Nexus 릴리즈 저장소
    def repository = 'fhir-dev'
    // Nexus 릴리즈 저장소 url
    def nexusDeploymentRepository = 'http://cicd.myhealthway.go.kr:18081/repository/mhw-fhir-dev-repository/'
    // Nexus 릴리즈 저장소 접근 key
    def nexusCredentialsId = 'nexus-fhir-dev-key'
    // Nexus 릴리즈 저장소의 서버 ID
    def nexusServerId = 'mhw-Nexus-Fhir-Dev'

    stage('Prepare') {
        env.JAVA_HOME = tool "OpenJDK_17.0.5"
        env.MAVEN_HOME = tool "MYHW_Maven"
        env.PATH = "${env.PATH}:${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin"

        if(currentBuild.buildCauses.size() > 0) {
            buildCause = currentBuild.buildCauses[0]
            switch (buildCause._class) {
                case "hudson.model.Cause\$RemoteCause":
                    startedBy = "remote host ${buildCause.addr}"
                    break
                case "com.dabsquared.gitlabjenkins.cause.GitLabWebHookCause":
                    if(buildCause.shortDescription != null) {
                        startIdx = buildCause.shortDescription.indexOf("GitLab")
                        startedBy = buildCause.shortDescription.substring(startIdx)
                    }
                    break
                    // case "hudson.model.Cause\$UserIdCause":
                default:
                    if(buildCause.shortDescription != null) {
                        startIdx = buildCause.shortDescription.indexOf(" by ") + 4
                        startedBy = buildCause.shortDescription.substring(startIdx)
                    }
                    break
            }
        }
        env.startedBy = startedBy
    }

    // github에서 소스 얻어오기
    stage('Checkout') {

        branchName = params.branch != null ? getBranchName(params.branch) : defaultBranch

        // Git URL 경로
        gitObj = git url: "ssh://git@cicd.myhealthway.go.kr:10022/medi-my-data/${subGroup}/${project}.git",
                credentialsId: "ssh_git_key2",
                branch: "${branchName}"
        gitCommitId = gitObj.GIT_COMMIT.substring(0, 8)

        println "*********** Target ***********\n" +
                "   Project: ${project}\n" +
                "   Branch: ${branchName}\n" +
                "   commitId: ${gitCommitId}\n"
        "******************************"
    }

    stage('Build') {
//        mavenOptions = params.getOrDefault("mavenOptions", "-Dmaven.test.failure.ignore=true")
//        sh "mvn ${mavenOptions} clean install deploy"
        sh "mvn clean install -DskipTests"
    }

    stage('Deploy') {
        withCredentials([usernamePassword(credentialsId: "${nexusCredentialsId}", usernameVariable: 'id', passwordVariable: 'pass')]) {
            def artifact = findFiles(glob: 'target/ROOT.war').first()
            if (artifact != null) {
                def groupId = "kr.co.iteyes"
                def artifactId="fhir-meta"
                def version = "3.0.1-SNAPSHOT"

                def credentials = "-D${repository}.username=${id} -D${repository}.password=${pass}"
                def repositoryUrl = "-DaltSnapshotDeploymentRepository=${nexusServerId}::${nexusDeploymentRepository}"

//                sh "mvn ${credentials} ${repositoryUrl} deploy -DskipTests"
                sh "mvn clean install deploy:deploy-file -DskipTests -Dfile=${artifact} -DgroupId=${groupId} -Durl=${nexusDeploymentRepository} -DartifactId=${artifactId} -Dversion=${version} -Dpackaging=war -DrepositoryId=${nexusServerId} ${credentials}"

            } else {
                error 'WAR 파일을 찾을 수 없습니다.'
            }
        }
    }

}

