# Volunteer Scheduling System (VSS)

A web application that lets NGOs and event organisers publish volunteer shifts, lets volunteers
register and request slots, and lets coordinators confirm or cancel requests — built and delivered
through a complete DevOps toolchain (Git/GitHub, Maven, Jenkins, Selenium, Docker, Ansible).

## Tech stack
| Layer | Choice |
|-------|--------|
| Language | Java 17 |
| Framework | Spring Boot 3.3 (Web MVC, Thymeleaf, Data JPA, Validation, Actuator) |
| Database | H2 (in-memory for dev/test, file-based in containers) |
| Build tool | Apache Maven 3.9 |
| Deployment target | Apache Tomcat 10.1 (WAR) / embedded Tomcat in Docker |

## Run locally
```bash
mvn clean package
java -jar target/vss.war          # http://localhost:8080
```

## Project structure
```
src/main/java/com/vss      application code
src/main/resources         templates, static files, configuration
src/test/java              unit / integration tests
docs/                      weekly project documentation
.github/                   issue & PR templates
```

See [CONTRIBUTING.md](CONTRIBUTING.md) for the branch naming and commit rules.
