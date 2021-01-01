#!/usr/bin/env python

# based on code by henryk ploetz
# https://hackaday.io/project/5301-reverse-engineering-a-low-cost-usb-co-monitor/log/17909-all-your-base-are-belong-to-us
# https://raw.githubusercontent.com/wooga/office_weather/master/monitor.py

import os, sys, fcntl, time, socket, datetime, yaml
import publisher
import publisher_mqtt

def decrypt(key,  data):
    cstate = [0x48,  0x74,  0x65,  0x6D,  0x70,  0x39,  0x39,  0x65]
    shuffle = [2, 4, 0, 7, 1, 6, 5, 3]

    phase1 = [0] * 8
    for i, o in enumerate(shuffle):
        phase1[o] = data[i]

    phase2 = [0] * 8
    for i in range(8):
        phase2[i] = phase1[i] ^ key[i]

    phase3 = [0] * 8
    for i in range(8):
        phase3[i] = ( (phase2[i] >> 3) | (phase2[ (i-1+8)%8 ] << 5) ) & 0xff

    ctmp = [0] * 8
    for i in range(8):
        ctmp[i] = ( (cstate[i] >> 4) | (cstate[i]<<4) ) & 0xff

    out = [0] * 8
    for i in range(8):
        out[i] = (0x100 + phase3[i] - ctmp[i]) & 0xff

    return out

def hd(d):
    return " ".join("%02X" % e for e in d)

if __name__ == "__main__":
    """main"""

    try:
        s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        s.bind('\0postconnect_gateway_notify_lock')
    except socket.error as e:
        print ("Skipping, already reading %s" % e)
        sys.exit(0)

    script_base_dir = os.path.dirname(os.path.realpath(sys.argv[0])) + "/"
    with open(script_base_dir + "config.yml", 'r') as ymlfile:
        config = yaml.full_load(ymlfile)
        publish_server_url = config["publish_server_url"]
        publish_mqtt_server = config.get("publish_mqtt_server")
        publish_mqtt_port = config.get("publish_mqtt_port")

    key = [0xc4, 0xc6, 0xc0, 0x92, 0x40, 0x23, 0xdc, 0x96]
    fp = open(sys.argv[1], "a+b",  0)
    HIDIOCSFEATURE_9 = 0xC0094806
    set_report = "\x00" + "".join(chr(e) for e in key)
    fcntl.ioctl(fp, HIDIOCSFEATURE_9, set_report)

    values = {}
    co2_seen = 0
    temperature_seen = 0

    while True:
        data = list(ord(e) for e in fp.read(8))
        decrypted = decrypt(key, data)
        if decrypted[4] != 0x0d or (sum(decrypted[:3]) & 0xff) != decrypted[3]:
            print(hd(data), " => ", hd(decrypted),  "Checksum error")
        else:
            op = decrypted[0]
            val = decrypted[1] << 8 | decrypted[2]
            values[op] = val

            if (0x50 in values) and (0x42 in values):
                co2 = values[0x50]
                temperature = round(values[0x42]/16.0-273.15, 1)

                if (co2 > 5000 or co2 < 0 or (co2_seen == co2 and temperature_seen == temperature)):
                    continue

                co2_seen = co2
                temperature_seen = temperature
                time = datetime.datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')

                try:
                    if publish_mqtt_server and publish_mqtt_port:
                        publisher_mqtt.publish_mqtt(time, temperature, co2, publish_mqtt_server, publish_mqtt_port)

                    status_code = publisher.publish(time, temperature, co2, publish_server_url)
                    print("[%s] %ippm, %3.1fc, /%s" % (time, co2, temperature, status_code))
                except Exception as e:
                    print("[%s] %ippm, %3.1fc, /%s" % (time, co2, temperature, str(e)))
                    