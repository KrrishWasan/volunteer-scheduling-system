# Week 10 – Continuous Testing in Jenkins

## 1. Jenkins integration
The `Selenium Quality Gate` stage in `Jenkinsfile` runs after unit tests:
```groovy
stage('Selenium Quality Gate') {
    steps { sh 'mvn -B test -Pselenium' }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
        }
    }
}
```
Because later stages (Package → Docker → Deploy) only run if this stage succeeds,
**failed tests stop deployment** – the quality gate.

Jenkins agent prerequisite: a Chrome/Chromium binary + matching chromedriver on the
agent, or Docker agents using a `selenium/standalone-chrome` sidecar. For this
project the gate was verified locally (`mvn test -Pselenium`, 5/5 green) and the
stage publishes the same JUnit XML + failure screenshots in Jenkins.

## 2. Deliberate-defect demonstration (how to reproduce for the viva)
1. Introduce a defect, e.g. in `BookingService.requestBooking` comment out the
   capacity check, commit and push.
2. Jenkins pipeline: `Build & Unit Test` fails (`capacityIsNeverExceeded`) or the
   Selenium booking journey fails → pipeline stops **before** Docker/Deploy.
   Evidence: red stage, JUnit failures tab, no new image pushed.
3. Fix the defect (restore the check), commit:
   `fix(booking): restore capacity check in booking requests`.
4. Rerun → full pipeline green through Deploy; health check `UP`.

## 3. Evidence checklist
- [ ] Jenkins test-result trend graph (14 unit + 5 Selenium tests).
- [ ] Failed-pipeline screenshot (red Selenium gate, deployment skipped).
- [ ] Defect-fix commit hash referenced in the rerun description.
- [ ] Successful rerun screenshot (all stages green).
