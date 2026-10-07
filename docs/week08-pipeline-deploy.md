# Week 8 – Pipeline as Code and Server Deployment

## 1. Jenkinsfile (repo root)
Stages: **Checkout → Build & Unit Test → Selenium Quality Gate → Package & Archive
→ Docker Build & Push → Deploy Container**. Full source in `Jenkinsfile`.

Key points:
- `parameters { choice APP_ENV; string APP_VERSION }` – the **parameterised
  environment setting** is `APP_ENV` (`dev`/`test`/`prod`), passed into the app as the
  `APP_ENV` environment variable and shown in the header badge and `/api/version`.
- `triggers { pollSCM('H/2 * * * *') }` – commit-triggered builds.
- `APP_VERSION` defaults to `<pom version>-<build number>` (e.g. `1.0.0-42`).
- JUnit reports published after both test stages; WAR fingerprinted and archived.

## 2. Pipeline job setup
1. New Item → Pipeline `vss-pipeline` → Definition: *Pipeline script from SCM*,
   SCM Git, repository URL, branch `*/develop`, script path `Jenkinsfile`.
2. Build with Parameters → choose `APP_ENV=test` → green run.
3. Evidence: stage view (all stages green), console log, `target/vss.war` artefact.

## 3. Deployment
The final stage runs a fresh container from the just-pushed image:
```bash
docker run -d --name vss --restart unless-stopped -p 8080:8080 \
  -e APP_ENV=test -e APP_VERSION=1.0.0-42 \
  krrishwasan/volunteer-scheduling-system:1.0.0-42
curl http://localhost:8080/actuator/health   # {"status":"UP",...}
curl http://localhost:8080/api/version       # {"environment":"test",...}
```
For a plain Tomcat server instead of Docker: copy `target/vss.war` to
`$CATALINA_BASE/webapps/vss.war` (Tomcat 10.1+, Java 17) and open
`http://<host>:8090/vss/`. The WAR is executable too (`java -jar vss.war`).

## 4. Configuration evidence
Screenshot `/api/version` showing the chosen `APP_ENV`, plus Jenkins
"Parameters" page showing `APP_ENV=test`.
