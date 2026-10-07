# Week 13 – Configuration Management Script

Tool selected: **Ansible** (agentless, YAML, works from WSL; no master server needed).

## 1. Server prerequisites identified
| Category | Requirement |
|----------|-------------|
| Packages | `openjdk-17-jre-headless`, `curl`, `docker.io` |
| Service | `docker` running + enabled |
| User | system user `vss`, member of `docker` group |
| Folders/files | `/opt/vss/data` (H2 data, `vss:docker` owned), `/opt/vss/vss.env` (env settings) |
| Ports | 8080 → container 8080 |
| Health | `/actuator/health` must return `UP` |

## 2. Ansible artefacts (`ansible/`)
| File | Purpose |
|------|---------|
| `inventory.ini` | `vss` group; default `vss-node ansible_connection=local` |
| `ansible.cfg` | local defaults (no retry files) |
| `playbook.yml` | installs packages → creates user → folders/env file → pulls versioned image → runs container → health check; handlers restart on env change |
| `rollback.yml` | stops current container, starts a previous image tag, verifies health + version |

Variables (`-e` overrides): `app_env` (default `prod`), `app_version` (default `1.0.0`).

## 3. First execution log
```bash
# WSL Ubuntu: sudo apt install ansible docker.io; sudo service docker start
ansible-galaxy collection install community.docker
ansible-playbook -i inventory.ini playbook.yml -e "app_env=test app_version=1.0.0"
# PLAY RECAP: ok=... changed=N failed=0
curl http://localhost:8080/actuator/health   # {"status":"UP",...}
```
Paste the `PLAY RECAP` + `changed` count as evidence.
