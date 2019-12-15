# Co2Monitor backend server

## Used

* [Kotlin](https://kotlinlang.org/)
* [MySQL](https://www.mysql.com/)
* [Ktor server](https://ktor.io/)
* [GraphQL Java](https://github.com/graphql-java/graphql-java)
* [Moshi JSON library](https://github.com/square/moshi)
* [Exposed Kotlin SQL Framework](https://github.com/JetBrains/Exposed)
* [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization)

## Build

```bash
./gradlew buildReleaseJar
```

`backend/build/libs/backend-x.x-release.jar` should be created.

## Installation

The playbook installs mysql and http4k application wrapped it into a launchable on system boot service.

Prerequisites: Server (e.g. a Raspberry Pi) runs Debian and can be reached via ssh.

1. Create `ansible/inventory` from `ansible/inventory.sample`
2. Create `ansible/vars/co2-monitor-backend.yml` from `ansible/vars/co2-monitor-backend.sample.yml`
3. Create `ansible/vars/co2-monitor-mysql.yml` from `ansible/vars/co2-monitor-mysql.sample.yml`
4. Cteate `data/co2monitor.yml` from `data/co2monitor.sample.yml`
5. Run `cd ansible && ansible-playbook playbook.yml`

## Misc commands
```bash
ssh-copy-id -i ~/.ssh/id_rsa.pub pi@raspberrypi

# view service logs
journalctl -e -u co2-monitor-backend.service

# restart service
sudo systemctl restart co2-monitor-backend

# reload daemon after .service file changed
sudo systemctl daemon-reload

# disable swap file
dphys-swapfile swapoff
sudo swapoff -a
```
