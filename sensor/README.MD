# Co2Monitor sensor script

Simple python script reading CO2 and Temperature values from "TFA Dostmann AirCO2ntrol Mini CO2-Monitor" and posting them to `backend` server.

## Installation

The playbook installs `monitor.py` script and wraps it into a launchable on system boot service. 

Prerequisites: Raspberry Pi runs Debian and can be reached via ssh.

1. Create `ansible/inventory` from `ansible/inventory.sample`
2. Create `ansible/vars/co2-monitor.yml` from `ansible/vars/co2-monitor.sample.yml`
3. Run `cd ansible && ansible-playbook playbook.yml`

## Misc commands

```bash
# view co2-monitor service logs
sudo journalctl -e -u co2-monitor.service

# stop co2-monitor service
sudo systemctl stop co2-monitor

# add user to "plugdev" group
sudo usermod -G plugdev <user>

# launch script from command line for testing purposes (co2-monitor service must be stopped)
python co2-monitor/monitor.py /dev/hidraw0
```
