# Week 12 – Jenkins-Docker Continuous Deployment

The `Jenkinsfile` already implements the full commit-to-container flow
(Weeks 8 + 10 + 12 combined):

```
commit → Checkout → Build & Unit Test → Selenium Quality Gate (blocks on failure)
       → Package & Archive (vss.war) → Docker Build & Push (:<version> + :latest)
       → Deploy Container (stop old, run new, curl health check)
```

## 1. Versioned image
`APP_VERSION` = parameter or `<pom>-<build#>` (e.g. `1.0.0-42`); every build pushes
an immutable tag plus moves `latest`:
```groovy
def img = docker.build("${env.IMAGE_NAME}:${env.APP_VERSION}", "--build-arg ... .")
docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
    img.push()
    img.push('latest')
}
```

## 2. Registry evidence (Docker Hub)
Repo `krrishwasan/volunteer-scheduling-system` → Tags tab shows `1.0.0-42`, `latest`
with push timestamps. Screenshot this page.

## 3. End-to-end evidence
1. Push a commit (e.g. docs change) to `develop`.
2. Jenkins: poll-SCM triggers build; stage view goes green through Deploy.
3. `curl http://localhost:8080/api/version` reports the new build's version –
   proof that the running container came from this exact commit.
