# -*- coding: utf-8 -*-
import paho.mqtt.client as mqtt
import json

def on_connect(client,userdata,flags,rc):
    print("Connected with result code "+str(rc))
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.  
    
# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.qos) + " " + str(msg.payload))
    payload = str(msg.payload)
    payload1=msg.payload.decode()
    topic = msg.topic
    if(topic.find('upward')):
        print('设备消息上报upward：')    
    payload_dict = json.loads(payload1)
    #for(k,v) in  payload_dict.items():
        #print( k ,":",v)
    gwId = payload_dict['gwId']
    print("gwId:" + gwId)
    deviceId = payload_dict['deviceId']
    print("deviceId:" + deviceId)
    try:
        childDeviceId = payload_dict['childDeviceId']
        print("childDeviceId:" + childDeviceId)
    except:
        print('childDevice: NULL')
    eventType = payload_dict['eventType']
    print("eventType:" + eventType)
    data = payload_dict['data']
    print("data:" + str(data))
    if(eventType == 'Boot'):
        check_boot_info(topic,payload1)
    
def on_publish(client,userdata, mid):
    print("mid:"+str(mid))
    
def on_subscribe(client, obj, mid, granted_qos):
    print("Subscribed: " + str(mid) + " " + str(granted_qos))


def on_log(client, obj, level, string):
    print(string)

def check_boot_info(topic,payload):
    if payload is None or len(payload) == 0 :
        raise ValueError("invalid payload")
    payload_dict = json.loads(payload)
    data = payload_dict['data']
    firmwareVersion = data['firmwareVersion']
    softwareVersion = data['softwareVersion']
    print('固件版本：'+firmwareVersion)
    print('软件版本：'+softwareVersion)
        
def check_inform_info(topic,payload):
    pass
#Client(client_id="", clean_session=True, userdata=None, protocol=MQTTv311, transport="tcp")
client = mqtt.Client(client_id='test222',clean_session=False)

client.username_pw_set('andlink','andlink')
client.on_connect = on_connect
client.on_message = on_message
client.on_publish = on_publish
client.on_subscribe = on_subscribe

client.connect("172.28.25.153", 1883, 60)

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.

#publish(topic, payload=None, qos=0, retain=False)
client.publish('bench/11', payload="345", qos=0, retain=False)
#client.subscribe("/device/upward/data/C43306201802",0)
