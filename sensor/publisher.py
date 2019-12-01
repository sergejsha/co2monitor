#!/usr/bin/env python

import datetime, requests

def publish(time, temperature, co2, publish_server_url):
    data = {"query":"mutation { createMeasurement(measurement: {time: \"%s\", temperature: %i, co2: %i}) { id } }" % (time, round(temperature * 10), co2) }
    return requests.post(publish_server_url, json = data).status_code
