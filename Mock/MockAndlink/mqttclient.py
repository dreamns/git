# -*- coding: utf-8 -*-
import paho.mqtt.client as mqtt
import json
import threading
import os
import yaml
import time
from flask import Flask,jsonify,request

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
def inform(inform):
    fileNamePath = os.path.split(os.path.realpath(__file__))[0]
    print(fileNamePath)
    # 获取配置文件的路径 D:/WorkSpace/StudyPractice/Python_Yaml/YamlStudy\config.yaml
    yamlPath = os.path.join(fileNamePath,'test.yaml')
    print(yamlPath)
    # 加上 ,encoding='utf-8'，处理配置文件中含中文出现乱码的情况。
    f = open(yamlPath,'r',encoding='utf-8')
    cont = f.read()
    x = yaml.load(cont)    
    message = x[inform]
    print(message)
    return message

#Client(client_id="", clean_session=True, userdata=None, protocol=MQTTv311, transport="tcp")
client = mqtt.Client()
client.username_pw_set('andlink','andlink')
client.on_connect = on_connect
client.on_message = on_message
client.on_publish = on_publish
client.on_subscribe = on_subscribe
client.connect("112.13.168.6", 18090, 60)
# BootMessage = inform('Boot')


# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.

#publish(topic, payload=None, qos=0, retain=False)
#client.publish('/device/upward/data/test', payload="345", qos=0, retain=False)
#client.subscribe("/device/upward/data/24615AAD2000",0)
#inform_json = '''{"data":{"params":[{"paramCode":"outletStatus","paramValue":"0"}]},"eventType":"Inform","gwId":"24615AAD2000","sessionId":18,"deviceId":"CMCC-30212-34ea34cecd5a","timestamp":1529491162697}'''

#线程方式（废弃）
def listen():
    print('start MQTT listen')
    counter = 1
    while counter:
       
        time.sleep(2)
        counter -= 1
    
#thread1 = threading.Thread(target=listen)
#thread1.start() 
#调用join的线程等待
#thread1.join()
#thread2.join()
#print ("退出主线程")

app = Flask(__name__)#创建一个服务，赋值给APP
@app.route('/device/upward/data/<mac>/<commondType>',methods=['GET','POST'])#指定接口访问的路径，支持什么请求方式get，post
def device_upward_data(mac,commondType):
    data = inform(commondType)
    data_json = json.dumps(data)
    client.publish("/device/upward/data/%s" % (mac), data_json ,qos=1)   
    return 'Success publish %s to /device/upward/data/%s' %(data_json,mac)

@app.route('/cloud/downward/command/<mac>/<commandType>',methods=['GET','POST'])
def cloud_downward_command(mac,commandType):
    print(commandType)
    data = inform(commandType)
    data_json = json.dumps(data)
    client.publish("/cloud/downward/command/%s" % (mac), data_json ,qos=0)    
    return 'Success publish %s to /cloud/downward/command/%s' %(data_json,mac)

app.run(host='0.0.0.0',port=8802,debug=True)
#这个host：windows就一个网卡，可以不写，而liux有多个网卡，写成0:0:0可以接受任意网卡信息,通过访问127.0.0.1:8802/get_user，可返回data信息
#debug:调试的时候，可以指定debug=true；如果是提供接口给他人使用的时候，debug要去掉