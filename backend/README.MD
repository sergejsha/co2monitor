# Co2Monitor backend server

## Used

* [Kotlin](https://kotlinlang.org/)
* [Ktor server](https://ktor.io/)
* [GraphQL Java](https://github.com/graphql-java/graphql-java)
* [Sqlite](https://www.sqlite.org/)
* [Moshi JSON library](https://github.com/square/moshi)

## Build

```bash
./gradlew build
```

`backend/build/distributions/backend-shadow-<version>.zip` should be created.

## Installation

1. Download and install [Raspberry Pi OS (32-bit) Lite](https://www.raspberrypi.org/downloads/raspberry-pi-os/) image onto a sd-card.
2. On the sd-card, create an empty `ssh` file for enabling headless startup.
3. Launch the raspberrypi using create sc-card image and run ansible setup.

## Ansible setup

Prerequisites: Server (e.g. a Raspberry Pi) runs Debian and can be reached via ssh.

1. Create `ansible/inventory` from `ansible/inventory.sample`
3. Create `ansible/vars/backend.yml` from `ansible/vars/backend.sample.yml`
6. Run `cd ansible && ansible-playbook playbook.yml`

## Misc commands
```bash
ssh-copy-id -i ~/.ssh/id_rsa.pub pi@raspberrypi

# view service logs
sudo journalctl -e -u co2-monitor-backend.service

# restart service
sudo systemctl restart co2-monitor-backend

# reload daemon after .service file changed
sudo systemctl daemon-reload

# download database
scp pi@raspberrypi:/home/pi/co2-monitor/backend/data/co2monitor.db ~/Downloads

# disable swap file
sudo dphys-swapfile swapoff
sudo swapoff -a
```
