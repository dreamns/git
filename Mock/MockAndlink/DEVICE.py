# -*- coding: utf-8 -*-
import paho.mqtt.client as mqtt
import json
import requests
import Tool
import os 
import yaml
import time
class Device:
    gwId= 'mock_gateway_001'
    deviceMac = 'mock-xhy-001'
    deviceType = '30187'
    productToken = 'wgUwtLA54DTnbOJC'
    ipAddress = '192.168.1.1'
    firmwareVersion = 'V1.0'
    softwareVersion = 'V1.0'
    deviceName = 'xhy-gateway'
    eventType = {'Boot','Inform','ParaChange'}
    deviceParam = {'powerStatus': 0}
    fatherOrChild = 0
    apiKey = 'Is1akR-Mdd-AdHpftfduigiTobcTfwwsYkPMy7E-R8YNkjZ9BidQx2k6DRUz9sfb'
    passId = '14095114698898'
    mobileNumber = '18867102935'
    #Msg_Boot = {}
    #Msg_Inform
    #Msg_ParamChange
    #Msg_Logout
    #Msg_Boot
    def Messageload(self):
        fileNamePath = os.path.split(os.path.realpath(__file__))[0]
        # 获取配置文件的路径 D:/WorkSpace/StudyPractice/Python_Yaml/YamlStudy\config.yaml
        yamlPath = os.path.join(fileNamePath,'test.yaml')
        # 加上 ,encoding='utf-8'，处理配置文件中含中文出现乱码的情况。
        f = open(yamlPath,'r',encoding='utf-8')
        cont = f.read()
        x = yaml.load(cont)   
        self.Msg_Boot = x['Boot']
        
        return x

        
        
    
    def __init__(self,gwid,devicemac,devicetype,producttoken,devicename,deviceparam,fatherorson):
        self.gwId=gwid
        self.deviceMac = devicemac
        self.deviceType = devicetype
        self.deviceName = devicename
        self.productToken=producttoken
        self.deviceParam = deviceparam
        self.fatherOrChild = fatherorson
        
    def DeviceInfo(self):
        print('deviceMac:%s' % self.deviceMac)
        print('deviceTpye: %s' % self.deviceType)
        print('deviceName: %s' % self.deviceName)

    def Switch_ON(self):
        self.DeviceSwitch = 1
    def Switch_OFF(self):
        self.DeviceSwitch = 0
    def Device_Boot(self):
        print ( 'boot')
        
    def Device_Inform(self):
        print('inform')
    def Device_Control(self,control_message):
        print('control:%s' % control_message)
        message_dict = json.loads(control_message)
        message_json = json.dumps(message_dict)
        device_param = message_dict['data']['params']
        print(device_param)
        for i in device_param:
            self.deviceParam[i['paramCode']] = i['paramValue']
            print(i['paramCode'])
            print(i['paramValue'])
            print(self.deviceParam[i['paramCode']])
    #智能家居平台绑定设备：0--网关；1--Andlink；2--
    def Device_Create(self,devicetype):
        if devicetype == 1:
            print('创建Andlink类设备')
            url = 'http://172.28.25.146:8085/espapi/m2m/rest/device'
            header = {'API_KEY':self.apiKey,'Content-Type':'application/json'}
            payload = {
                'deviceType':self.deviceType,
                'deviceMac': self.deviceMac ,
                'gwId': self.gwId,
                'timestamp': 1506492053309,
                'productToken':self.productToken  
            }
            payload_json = json.dumps(payload)
            print(payload_json)
            Tool.request_post(url,header,payload_json)
        elif devicetype == 2:
            print ("11")
        elif devicetype == 0:
            print('绑定网关')
            url = 'http://172.28.25.146:8085/espapi/platform/json/jk/notifyBindHG'
            header = {'API_KEY':self.apiKey,'Content-Type':'application/json'}
            payload = {
                'RPCMethod':"Notify",
                'ID': "2212" ,
                'CmdType': "NOTIFY_BIND_HG",
                'SequenceId': "12345678",
                'Parameter':{
                    "Passid":self.passId,
                    "MobileNumber":self.mobileNumber,
                    "MAC":self.gwId,
                    "OperateType":"BIND",
                    "OperateTime":"2017-05-24 10:38:43"
                  } 
            }
            payload_json = json.dumps(payload)
            print(payload_json)
            Tool.request_post(url,header,payload_json)
        
if __name__ == '__main__':
    device = Device('mock_gateway_001','mock-xyh-001','30187','wgUwtLA54DTnbOJC','xhy-gateway',{'ipAddress': '192.168.1.1'},0)
    device.DeviceInfo()
    control_message ='''{"childDeviceId":"310bee08004b1200","commandType":"Control","data":{"params":[{"paramCode":"powerStatus","paramValue":"1"},{"paramCode":"powerStatus2","paramValue":"2"}]},"deviceId":"CMCC-30187-58480100022e","gwId":"24615AAD2000","sessionId":10967,"timestamp":1534417827374}'''
    #device.Messageload()
    #device.Device_Control(control_message)
    #设备绑定
    device.Device_Create(1)
    #解绑
    #{"childDeviceId":"310bee08004b1200","commandType":"Unbind","data":{"params":[]},"deviceId":"CMCC-30187-58480100022e","gwId":"24615AAD2000","sessionId":18447,"timestamp":1534425183197}
    #允许入网
    #{"commandType":"Control","data":{"params":[{"paramCode":"permitJoining","paramValue":"1"}]},"deviceId":"CMCC-30187-58480100022e","gwId":"24615AAD2000","sessionId":18577,"timestamp":1534425295148}
    #星火原网关上线
    #{"data":{"ipAddress":"192.168.1.6","firmwareVersion":"XHY-X3","softwareVersion":"v3.01.13"},"eventType":"Boot","gwId":"24615AAD2000","sessionId":28,"deviceId":"CMCC-30187-58480100022e","timestamp":13580645}
    #星火原网关上线后inform
    #{"data":{"params":[{"paramCode":"firmware","paramValue":"v3.01.13"},{"paramCode":"softVersion","paramValue":"v3.01.13"},{"paramCode":"permitJoining","paramValue":"0"}]},"eventType":"Inform","gwId":"24615AAD2000","sessionId":29,"deviceId":"CMCC-30187-58480100022e","timestamp":13580714}
    #星火原一键开关boot
    #{"data":{"ipAddress":"","firmwareVersion":"XHY-X3","softwareVersion":"v3.01.13"},"eventType":"Boot","gwId":"24615AAD2000","sessionId":31,"deviceId":"CMCC-30187-58480100022e","childDeviceId":"310bee08004b1200","timestamp":13713671}
    #一键开关inform
    #{"data":{"params":[{"paramCode":"firmware","paramValue":"XHY-X3"},{"paramCode":"softVersion","paramValue":"v3.01.13"},{"paramCode":"powerStatus","paramValue":0}]},"eventType":"Inform","gwId":"24615AAD2000","sessionId":32,"deviceId":"CMCC-30187-58480100022e","childDeviceId":"310bee08004b1200","timestamp":13713672}
