# Week 15 – Final End-to-End Release, Documentation and Viva

## 1. End-to-end release drill (do this live in the demo)
1. `git checkout develop`; make a visible change (e.g. add a seed slot); commit;
   push → GitHub.
2. Jenkins `vss-pipeline` triggers (poll SCM): Checkout → Build & Unit Test (14 tests)
   → Selenium Quality Gate (5 journeys) → Package (`vss.war`) → Docker Build & Push
   (`:1.0.0-<build>`) → Deploy Container → health check `UP`.
3. `curl http://localhost:8080/api/version` shows the new build number.
4. `ansible-playbook -i inventory.ini playbook.yml` → `changed=0` (already converged).
5. `git tag v1.1.0 && git push origin v1.1.0`; merge `develop` → `main` via PR.

## 2. Deliverables checklist
- [ ] Final repository (all branches, PRs #1/#2, tags `v1.0.0`).
- [ ] Live demo (register → book → confirm → cancel → status) + pipeline run.
- [ ] Complete report (`docs/week01` … `week14` + this file) with screenshots/video.
- [ ] Presentation (10–12 slides: problem, architecture, demo, pipeline, testing,
      Docker, Ansible, results vs success criteria, limitations, future work).
- [ ] Viva preparation below.

## 3. Likely viva questions (with one-line answers)
- *Why Maven over Gradle?* Course standard; declarative lifecycle; Jenkins-native.
- *Why WAR + Tomcat?* Deployable to existing Tomcat servers and executable anywhere.
- *How is over-booking prevented?* Transactional service check: active bookings <
  capacity; covered by `capacityIsNeverExceeded` + Selenium booking journey.
- *How do failed tests stop deployment?* Jenkins stages after the Selenium gate
  never execute when it fails (demonstrated in Week 10).
- *Idempotency?* Re-running the playbook yields `changed=0`.
- *Rollback?* `rollback.yml` restarts the previous image tag; health-verified.
- *Weakest point?* No authentication (demo coordinator), H2 instead of managed DB,
  single-node Docker (no orchestration) – all listed as future work.

## 4. Limitations & future enhancements
No login/roles; no e-mail/SMS notifications; H2 file DB (swap via `DB_URL` to
PostgreSQL/MySQL); single host (next: Docker Compose/Kubernetes); no monitoring
dashboard (next: Prometheus + Grafana + alerting); no HTTPS (next: Nginx reverse
proxy with TLS).

## 5. Troubleshooting guide
| Symptom | Fix |
|---------|-----|
| Port 8080 in use | `PORT=8090 java -jar target/vss.war` |
| Selenium driver error | Re-run one-time `drivers/` setup (see Week 9 doc) |
| Jenkins can't find JDK/Maven | Tool names must be exactly `jdk17` / `maven` |
| Docker push denied | `docker login` / check `dockerhub` credentials ID |
| Ansible `changed>0` on rerun | Normal only for image-pull/health tasks |
| H2 console | Set `H2_CONSOLE=true`, open `/h2-console`, JDBC URL from `DB_URL` |
