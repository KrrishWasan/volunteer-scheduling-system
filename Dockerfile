# Volunteer Scheduling System – multi-stage Docker build (Week 11).
# Stage 1 builds the WAR with Maven; stage 2 runs it on a small JRE image.
# Build:  docker build --build-arg APP_VERSION=1.0.0 --build-arg APP_ENV=prod -t vss:1.0.0 .
# Run:    docker run -d --name vss -p 8080:8080 -e APP_ENV=prod vss:1.0.0

ARG APP_VERSION=1.0.0
ARG APP_ENV=prod

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:17-jre
ARG APP_VERSION
ARG APP_ENV
ENV APP_VERSION=${APP_VERSION} \
    APP_ENV=${APP_ENV} \
    PORT=8080 \
    DB_URL="jdbc:h2:file:/data/vss;DB_CLOSE_DELAY=-1;AUTO_SERVER=FALSE" \
    SEED_DATA=true
WORKDIR /app
VOLUME /data
COPY --from=build /app/target/vss.war /app/vss.war
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["sh", "-c", "java -jar /app/vss.war"]
