# Week 11 – Docker Image and Container Lifecycle

## 1. Dockerfile (repo root, multi-stage)
- Stage `build` (`maven:3.9-eclipse-temurin-17`): `dependency:go-offline` then
  `mvn -DskipTests package` → `target/vss.war`.
- Stage `run` (`eclipse-temurin:17-jre`): copies the WAR, `VOLUME /data`
  (file-based H2 survives restarts), `EXPOSE 8080`, `HEALTHCHECK` on
  `/actuator/health`, `ENTRYPOINT java -jar /app/vss.war`.
- Build args: `APP_VERSION`, `APP_ENV` (baked in, overridable at run time).

## 2. Full lifecycle command log (run and paste output as evidence)
```bash
docker build --build-arg APP_VERSION=1.0.0 --build-arg APP_ENV=prod -t vss:1.0.0 .
docker images vss                         # image details (ID, size)
docker tag vss:1.0.0 krrishwasan/volunteer-scheduling-system:1.0.0
docker run -d --name vss --restart unless-stopped -p 8080:8080 \
  -e APP_ENV=prod vss:1.0.0               # run + port mapping
docker ps                                 # running container evidence
docker logs vss | head -30                # startup logs
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/version
docker stop vss && docker start vss       # stop / restart
docker exec vss wget -qO- http://localhost:8080/actuator/health
docker stop vss && docker rm vss          # remove
docker rmi vss:1.0.0                      # (optional) remove image
```

## 3. Expected results
- `docker ps` shows `vss` Up; `/actuator/health` → `{"status":"UP"}`;
  `/api/version` → `{"environment":"prod","version":"1.0.0",...}`.
- Data persists in the `/data` volume across `stop/start`.
