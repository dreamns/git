#!/usr/bin/env python
# encoding: utf-8

import json
import sys
import traceback
import urllib2

URL = "http://218.205.115.238:28080/deviceio/mljson"


def main():
    proxy_id = "judy-10074-test"
    water_sensor_id = "judy-test-10077-water-sensor"
    entry_sensor_id = "judy-test-10079-entry-sensor"
    motion_detector_id = "judy-10075-motion-sensor"
    outlet_id = "judy-10076-outlet"
    smoke_detector_id = "judy-10078-smoke-detector"
    gas_sensor_id = "judy-10080-gas-sensor"

    # add_device(proxy_id, 10075, motion_detector_id)
    # add_device(proxy_id, 10076, outlet_id)
    # add_device(proxy_id, 10077, water_sensor_id)
    # add_device(proxy_id, 10078, smoke_detector_id)
    # add_device(proxy_id, 10079, entry_sensor_id)
    # add_device(proxy_id, 10080, gas_sensor_id)

    # send_entry_sensor_、measurements(proxy_id, water_sensor_id)
    # send_touch_sensor_measurements(proxy_id, touch_sensor_id)
    # send_temperature_sensor_measurements(proxy_id, temperature_sensor_id)
    # send_humidity_sensor_measurements(proxy_id, humidity_sensor_id)
    send_motion_detector_measurements(proxy_id, motion_detector_id)

    import threading
    t = threading.Thread(target=listen_forever, args=(proxy_id,))
    t.start()

    # send_proxy_measurements(proxy_id)


def add_device(proxy_id, device_type, device_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "addDevices": [
            {
                "deviceId": device_id,
                "deviceType": device_type,
                "params": [
                    {
                        "name": "relativeHumidity",
                        "value": "ANY"
                    }
                    ,
                    {
                        "name": "degC",
                        "value": "ANY"
                    }

                ]
            }
        ]
    }

    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


def send_entry_sensor_measurements(proxy_id, device_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "measures": [
            {
                "deviceId": device_id,
                "params": [
                    {
                        "name": "batteryLevel",
                        "value": "93"
                    },
                    {
                        "name": "batteryVoltage",
                        "value": "2.8",
                    },
                    {
                        "name": "lqi",
                        "value": "208"
                    },
                    {
                        "name": "rssi",
                        "value": "-56"
                    },
                    {
                        "name": "manufacturer",
                        "value": "CMCC"
                    },
                    {
                        "name": "model",
                        "value": "m01"
                    },
                    {
                        "name": "doorStatus",
                        "value": "false"
                    }
                ]
            }
        ]
    }
    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


def send_water_sensor_measurements(proxy_id, device_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "measures": [
            {
                "deviceId": device_id,
                "params": [
                    {
                        "name": "batteryLevel",
                        "value": "93"
                    },
                    {
                        "name": "batteryVoltage",
                        "value": "2.8",
                    },
                    {
                        "name": "lqi",
                        "value": "208"
                    },
                    {
                        "name": "rssi",
                        "value": "-56"
                    },
                    {
                        "name": "manufacturer",
                        "value": "CMCC"
                    },
                    {
                        "name": "model",
                        "value": "m01"
                    },
                    {
                        "name": "waterLeak",
                        "value": "false"
                    }
                ]
            }
        ]
    }
    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


def send_touch_sensor_measurements(proxy_id, device_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "measures": [
            {
                "deviceId": device_id,
                "params": [
                    {
                        "name": "batteryLevel",
                        "value": "93"
                    },
                    {
                        "name": "batteryVoltage",
                        "value": "2.8",
                    },
                    {
                        "name": "lqi",
                        "value": "208"
                    },
                    {
                        "name": "rssi",
                        "value": "-56"
                    },
                    {
                        "name": "manufacturer",
                        "value": "CMCC"
                    },
                    {
                        "name": "model",
                        "value": "m01"
                    },
                    {
                        "name": "vibrationStatus",
                        "value": "false"
                    }
                ]
            }
        ]
    }
    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


def send_temperature_sensor_measurements(proxy_id, device_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "measures": [
            {
                "deviceId": device_id,
                "params": [
                    {
                        "name": "batteryLevel",
                        "value": "5"
                    },
                    {
                        "name": "batteryVoltage",
                        "value": "2.5",
                    },
                    {
                        "name": "lqi",
                        "value": "208"
                    },
                    {
                        "name": "rssi",
                        "value": "-56"
                    },
                    {
                        "name": "manufacturer",
                        "value": "CMCC"
                    },
                    {
                        "name": "model",
                        "value": "m01"
                    },
                    {
                        "name": "degC",
                        "value": "2200"
                    }
                ]
            }
        ]
    }
    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


def send_humidity_sensor_measurements(proxy_id, device_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "measures": [
            {
                "deviceId": device_id,
                "params": [
                    {
                        "name": "batteryLevel",
                        "value": "91"
                    },
                    {
                        "name": "batteryVoltage",
                        "value": "2.4",
                    },
                    {
                        "name": "lqi",
                        "value": "208"
                    },
                    {
                        "name": "rssi",
                        "value": "-56"
                    },
                    {
                        "name": "manufacturer",
                        "value": "CMCC"
                    },
                    {
                        "name": "relativeHumidity",
                        "value": "5200"
                    }

                    ,
                    {
                        "name": "degC",
                        "value": "2200"
                    }

                ]
            }
        ]
    }
    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


def send_motion_detector_measurements(proxy_id, device_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "measures": [
            {
                "deviceId": device_id,
                "params": [
                    {
                        "name": "batteryLevel",
                        "value": "93"
                    },
                    {
                        "name": "batteryVoltage",
                        "value": "2.8",
                    },
                    {
                        "name": "lqi",
                        "value": "208"
                    },
                    {
                        "name": "rssi",
                        "value": "-56"
                    },
                    {
                        "name": "manufacturer",
                        "value": "CMCC"
                    },
                    {
                        "name": "model",
                        "value": "m01"
                    },
                    {
                        "name": "motionStatus",
                        "value": "true"
                    }
                ]
            }
        ]
    }
    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


def send_proxy_measurements(proxy_id):
    data = {
        "version": "2",
        "proxyId": proxy_id,
        # "hubId": proxy_id,
        "sequenceNumber": "1",
        "measures": [
            {
                "deviceId": proxy_id,
                "dateTime": "2016-11-25T18:34:29+08:00",
                # "dateTime": "2016-11-25T10:34:30",

                # "timestamp": "1480070036000",
                "paramsMap": {
                    "firmware": "test_1.08"
                }
                # "params": [
                #     {
                #         "name": "firmware",
                #         "value": "test_1.03"
                #     },
                #     {
                #         "name": "ipAddress",
                #         "value": "127.0.0.1"
                #     },
                #     {
                #         "name": "model",
                #         "value": "CMCC_Gateway"
                #     },
                #     {
                #         "name": "numberOfChildren",
                #         "value": "3"
                #     },
                #     {
                #         "name": "permitJoining",
                #         "value": "false"
                #     },
                #     {
                #         "name": "zbChannel",
                #         "value": "11"
                #     }
                # ]
            }
        ]
    }

    j = json.dumps(data)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    # return j


def listen_forever(proxy_id):
    while True:
        try:
            print('listening...')
            listen(proxy_id)
        except:
            e = sys.exc_info()[0]
            # if DEBUG or TESTRUN:
            #     raise(e)
            s = traceback.format_exc()
            sys.stderr.write(s + "\n\n")
            # print('sleeping for 10s')
            # time.sleep(10)


def listen(proxy_id):
    params = {'id': proxy_id, 'timeout': 60}
    req_url = " %s?id=%s&timeout=%s" % (URL, proxy_id, 60)
    print req_url
    http_headers = {"Content-Type": "application/json"}
    res = urllib2.urlopen(req_url)
    response_text = res.read()
    print('listen response: ' + response_text)
    if response_text is not None and response_text != '':
        j = json.loads(response_text)
        print(j)
        # do sth
        # ack_commands(proxy_id, j)
    else:
        print('60s timeout')


def ack_commands(proxy_id, response):
    cmd_list = []
    commands = response['commands']
    for cmd in commands:
        cmd_list.append({'commandId': cmd['commandId'], 'result': 1})

    data = {
        "version": "2",
        "proxyId": proxy_id,
        "sequenceNumber": "1",
        "responses": cmd_list
    }

    j = json.dumps(data)
    print(j)

    http_headers = {"Content-Type": "application/json"}
    req = urllib2.Request(URL, headers=http_headers, data=j)
    res = urllib2.urlopen(req)
    j = json.loads(res.read())
    print(j)
    return j


if __name__ == '__main__':
    main()
