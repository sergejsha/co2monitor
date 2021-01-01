#!/usr/bin/env python

import paho.mqtt.client as mqtt

connected = False
connecting = False

def mqtt_on_connect(client, userdata, flags, rc):
    global connected
    global connecting
    connected = True
    connecting = False
    print("[mqtt] connected")

def mqtt_on_disconnect(client, userdata, rc):
    global connected
    global connecting
    connected = False
    connecting = False
    client.loop_stop()
    print("[mqtt] disconnected")

client = mqtt.Client()
client.on_connect = mqtt_on_connect
client.on_disconnect = mqtt_on_disconnect

def publish_mqtt(time, temperature, co2, mqtt_server, mqtt_port):
    global client
    global connected
    global connecting

    if (not connected) and (not connecting):
        print("[mqtt] connecting, server: \"%s\", port: %s" % (mqtt_server, mqtt_port))
        client.loop_start()
        try:
            client.connect(mqtt_server, mqtt_port, 60)
            connecting = True
        except Exception as e:
            print("[mqtt] connection error, %s" % e)
            client.loop_stop()
            connecting = False

    if connected:
        client.publish("sensors/co2m", "{ time: \"%s\", temperature: %i, co2: %i }" % (time, round(temperature * 10), co2))
