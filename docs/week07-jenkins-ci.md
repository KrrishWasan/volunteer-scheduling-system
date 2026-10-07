# Week 7 – Jenkins Installation and Continuous Integration Job

## 1. Jenkins installation (Windows laptop)
1. Install JDK 17 and add `JAVA_HOME`. Jenkins 2.x requires Java 17/21.
2. Download `jenkins.war` (LTS) from https://www.jenkins.io/download/ (or
   `jenkins.msi` installer, which installs and starts the service).
3. Run: `java -jar jenkins.war --httpPort=8081` (port 8081 avoids clashing with the app on 8080).
4. Open http://localhost:8081, unlock with the initial admin password from
   `secrets/initialAdminPassword`, install suggested plugins.
5. Manage Jenkins → Tools: add JDK `jdk17` (auto-install from adoptium) and
   Maven `maven` (auto-install 3.9.x). Names must match the `Jenkinsfile` environment block.
6. If Jenkins runs in Docker instead: mount the Docker socket so the pipeline can
   build images (`-v /var/run/docker.sock:/var/run/docker.sock`).

## 2. Freestyle CI job (before Pipeline as Code)
1. New Item → Freestyle project `vss-ci`.
2. Source Code Management → Git: `https://github.com/KrrishWasan/volunteer-scheduling-system.git`,
   branches `*/develop`.
3. Build Triggers → **Poll SCM** `H/2 * * * *` (checks GitHub every 2 minutes;
   a GitHub webhook to `http://<jenkins>/github-webhook/` is the preferred alternative).
4. Build → Invoke top-level Maven targets: `clean verify`.
5. Post-build → Archive the artifacts: `target/vss.war` (fingerprint);
   Publish JUnit test result report: `target/surefire-reports/*.xml`.

## 3. Trigger evidence
- Make any commit on `develop` (or wait ≤ 2 minutes): the job starts automatically.
  Screenshot: build history showing *"Started by an SCM change"*.
- Console output ends with:
  ```
  [INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Building war: .../target/vss.war
  [INFO] BUILD SUCCESS
  ```
- Archived artefact: job page → Last Successful Artifacts → `vss.war`.
