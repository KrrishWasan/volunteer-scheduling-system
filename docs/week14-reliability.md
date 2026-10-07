# Week 14 – Automated Provisioning and Reliability Validation

Run on a **clean target** (fresh WSL instance, VM, or `docker stop vss && docker rm vss`
plus `apt purge` of prerequisites to simulate clean state).

## 1. Provision
```bash
ansible-playbook -i inventory.ini playbook.yml -e "app_env=prod app_version=1.0.0"
curl http://localhost:8080/api/version   # provisioned node evidence
```

## 2. Idempotency (second run changes nothing)
```bash
ansible-playbook -i inventory.ini playbook.yml -e "app_env=prod app_version=1.0.0"
# PLAY RECAP: ... changed=0 failed=0
```
`changed=0` on the configuration tasks proves idempotency (only the
pull/health-check tasks may report ok). Screenshot the recap.

## 3. Health check
```bash
curl -f http://localhost:8080/actuator/health   # exit code 0, "status":"UP"
```

## 4. Rollback / recovery
```bash
# Simulate a bad release, then recover to the last stable tag:
ansible-playbook -i inventory.ini rollback.yml -e "app_version=1.0.0"
# => "Rolled back successfully: {name: ..., version: 1.0.0, environment: ...}"
curl http://localhost:8080/actuator/health   # UP again within ~1 minute
```
Target: rollback completed in < 2 minutes with health `UP`.
