#!/usr/bin/python
# -*- coding: UTF-8 -*-
import WIFI
import DEVICE
import JsonTool
import json
import socket,sys,datetime
import threading
import random
import demjson
import base64
import struct
import hashlib
from time import sleep
class Gateway:

    #MAC='E06066FF92FA'
    MAC = '74ADB72FEFA1'
    Type = ''
    PluginName = ''
    CmdType = ''
    UserID = ''
    RPCMethod = ''
    ID = ''
    SequenceId = ''
    RESPONSEList= {'MAC':MAC}
    SPEED_REPORT_POLICY_TIME = '5'
    
    key1 = 'SequenceId'
    key2 = 'UserId'
    key3 = 'ID'
    
    #测试环境
    #HOST = "218.205.115.238"
    #PORT = 15683
    #生产环境
    HOST = "112.13.168.6"
    PORT = 15683
    
    DEVICEHOST = "218.205.115.244"
    DEVICEPORT = 19123

    
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s1 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    #WIFI
    SSID1 = WIFI.WIFI('8','0','100','0','1','5','11111111','1','CMCC-ssid1')
    SSID2 = WIFI.WIFI('8','0','60','0','1','5','22222222','2','CMCC-Share')
    SSID3 = WIFI.WIFI('8','0','40','0','1','5','33333333','3','CMCC-Guest')
    SSID4 = WIFI.WIFI('8','0','100','0','1','5','44444444','4','CMCC-ssid4')
    WIFIList = [SSID1,SSID2,SSID3,SSID4]
    #Device
    Device1 = DEVICE.Device('192.168.1.3','B0:E2:35:C6:AD:00','154','1','1','1','100','20','ON','ONLINE','XYH-PC','XYH-PC-1','0')
    Device2 = DEVICE.Device('192.168.1.2','70:81:EB:39:49:EC','0','1','5','1','120','0','ON','ONLINE','MI5','MI5','1')
    Device3 = DEVICE.Device('192.168.1.5','68:F7:28:BB:77:DA','0','1','3','1','800','0','ON','ONLINE','IPhone','IPhone','2')
    Device4 = DEVICE.Device('192.168.1.6','00:0E:C6:C8:18:00','0','1','5','1','300','0','ON','ONLINE','Android','Android','0')
    DeviceList = [Device1,Device2,Device3,Device4]

    NetTimeList = [{"bTime": 69600,"week": 14,"eTime": 69900,"netTimeId": "1","enable": 1},{"bTime": 69600,"week": 1,"eTime": 69900,"netTimeId": "2","enable": 1},{"bTime": 6960,"week": 40,"eTime": 69900,"netTimeId": "3","enable": 1}]

    def my_print(self,info):
        time_info="[%s]" % datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        print "%s:%s" % (time_info,info)

    def connect_to_server(self): 
        try :        
            self.s.connect((self.HOST, self.PORT))
            #login 
            msg="""{"Type":"request","msgType":"REQUEST","PluginName":"authPlugin","RPCMethod":"Set","UserId":"NA","ID":"NA","CmdType":"login","MAC":"%s","SequenceId":"NA"} \n""" % self.MAC           
            print msg
            self.s.send(msg)
           
        except :
            log= 'Unable to connect tcp server,script stopped!'
            self.my_print(log)
            sys.exit()
    def connect_to_device_server(self): 
        try :        
            self.s1.connect((self.DEVICEHOST, self.DEVICEPORT))
            #login 
            msg='{"RPCMethod": "BootInitiation","ID": 123,"PROTVersion": "1.0.0","MAC": "%s"}' % self.MAC  
            #msg= """{"RPCMethod":"BootInitiation","ID":1,"Vendor":"CMDC","ProuductCLass":"CM115Z","SWVersion":"V1.0.8","HDVersion":"1.0","OSType":"OSGI","OSVersion":"1.0.2","PROTVersion":"1.0.0","WanIPAddr":"192.168.199.27","WanIPv6Addr":"","MAC":"74ADB73082E0","BDAccount":"12345678","Password":"00000001"}"""
            print 'msg' , msg
            lena=int(len(msg))
            len16 =  struct.pack(">L",lena)
            str = len16+msg 
            print '-------send to device ---------',str 
            self.s1.send(str)

           
        except :
            log= 'Unable to connect tcp server,script stopped!'
            self.my_print(log)
            sys.exit()

    def get_device_request(self):
        BUFSIZ=1024
        key1 = 'SequenceId'
        key2 = 'UserId'
        while 1:
            recv_data = self.s1.recv(BUFSIZ)
            log= "Data from device server:%s" %recv_data
            log=log.replace("\\nr"," ")
            self.my_print(log)
            if ('"ChallengeCode"' in recv_data):
                #each request use 2s to handle mock responser
                print '11111111'
                self.my_print("-----------device register---------%s" % recv_data) 
                #sleep(1)
                response=self.device_register(recv_data)
                self.s1.send(response+'\n')
                self.my_print("send response"+response)
            if ('"RPCMethod":"Install"' in recv_data):
                #each request use 2s to handle mock responser
                self.my_print("-----------plugin install---------%s" % recv_data) 
                #sleep(1)
                response=self.plugin_install(recv_data)
                self.s1.send(response+'\n')
                self.my_print("send response"+response)
            

    def get_request(self): 
        BUFSIZ=1024
        key1 = 'SequenceId'
        key2 = 'UserId'
        while 1:
            recv_data = self.s.recv(BUFSIZ)
            if (recv_data != ""):
                
                log= "Data from server:%s" %recv_data
                log=log.replace("\\nr"," ")
                self.my_print(log)
                #if ('{"MAC":"74ADB72FEFA1","Type":"pong"}\n' in recv_data):
                    #each request use 2s to handle mock response
                    #sleep(2)
                    
                    #response_list.append("""{"MAC":"74ADB72FEFA1","Type":"ping"}\n""")
                if ("""{"CmdType":"GET_LAN_NET_INFO""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    #response='{"CmdType":"GET_LAN_NET_INFO","ID":"","MAC":"E06066FF92FA","PluginName":"LanDeviceConfig","RPCMethod":"Get","Result":"1000000","ResultData":{"Info":[{"AccessInternet":"1","ConnectType":"1","DevHostName":"MI5-android-xiami555","DevName":"MI5-android-xiami555","DeviceMAC":"B0:E2:35:C6:AD:00","DownLoadLimit":"0","IP":"192.168.1.5","OnlineTime":"175","SSIDIndex":"1","StorageAccessStatus":"ON","UpLoadLimit":"0"},{"AccessInternet":"1","ConnectType":"1","DevHostName":"ymhiPhone111","DevName":"ymhiPhone111","DeviceMAC":"70:81:EB:39:49:EC","DownLoadLimit":"0","IP":"192.168.1.4","OnlineTime":"185","SSIDIndex":"1","StorageAccessStatus":"ON","UpLoadLimit":"0"},{"AccessInternet":"1","ConnectType":"0","DevHostName":"XYH-PC","DevName":"XYH-PC","DeviceMAC":"68:F7:28:BB:77:DA","DownLoadLimit":"0","IP":"192.168.1.3","OnlineTime":"160","Port":"LAN4","StorageAccessStatus":"ON","UpLoadLimit":"0"},{"AccessInternet":"1","ConnectType":"0","DevHostName":"1-PC","DevName":"1-PC","DeviceMAC":"00:0E:C6:C8:18:00","DownLoadLimit":"0","IP":"192.168.1.2","OnlineTime":"340","Port":"LAN1","StorageAccessStatus":"ON","UpLoadLimit":"0"}],"Num":"4"},"SequenceId":"049b9829","Type":"response","UserId":"5916078243847","msgType":"RESPONSE"}'
                    response=self.get_lan_net_info(recv_data)
                    #data = JsonTool.replace_json_from_json(recv_data,response,key1)
                    #data = JsonTool.replace_json_from_json(recv_data,data,key2)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                    #sleep(1)
    
                if ("""{"CmdType":"GET_WIFI_SSID_INFO""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    #response='{"CmdType":"GET_LAN_NET_INFO","ID":"","MAC":"E06066FF92FA","PluginName":"LanDeviceConfig","RPCMethod":"Get","Result":"1000000","ResultData":{"Info":[{"AccessInternet":"1","ConnectType":"1","DevHostName":"MI5-android-xiami555","DevName":"MI5-android-xiami555","DeviceMAC":"B0:E2:35:C6:AD:00","DownLoadLimit":"0","IP":"192.168.1.5","OnlineTime":"175","SSIDIndex":"1","StorageAccessStatus":"ON","UpLoadLimit":"0"},{"AccessInternet":"1","ConnectType":"1","DevHostName":"ymhiPhone111","DevName":"ymhiPhone111","DeviceMAC":"70:81:EB:39:49:EC","DownLoadLimit":"0","IP":"192.168.1.4","OnlineTime":"185","SSIDIndex":"1","StorageAccessStatus":"ON","UpLoadLimit":"0"},{"AccessInternet":"1","ConnectType":"0","DevHostName":"XYH-PC","DevName":"XYH-PC","DeviceMAC":"68:F7:28:BB:77:DA","DownLoadLimit":"0","IP":"192.168.1.3","OnlineTime":"160","Port":"LAN4","StorageAccessStatus":"ON","UpLoadLimit":"0"},{"AccessInternet":"1","ConnectType":"0","DevHostName":"1-PC","DevName":"1-PC","DeviceMAC":"00:0E:C6:C8:18:00","DownLoadLimit":"0","IP":"192.168.1.2","OnlineTime":"340","Port":"LAN1","StorageAccessStatus":"ON","UpLoadLimit":"0"}],"Num":"4"},"SequenceId":"049b9829","Type":"response","UserId":"5916078243847","msgType":"RESPONSE"}'
                    response = self.get_wifi_ssid_info(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                    #sleep(1)
    
                if ("""{"CmdType":"SET_WIFI_SSID_INFO""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_wifi_ssid_info(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                    #sleep(1)
                if ("""{"CmdType":"SET_WIFI_SSID_ONOFF""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_wifi_ssid_onoff(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                    #sleep(1)
    
                if ("""{"CmdType":"SET_LAN_SPEED_REPORT_POLICY""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_lan_speed_report_policy(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                    #sleep(1)
    
                if ("""{"CmdType":"GET_LAN_DEVICE_ONLINE""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.get_lan_device_online(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                    #sleep(1)
    
                if ("""CmdType":"SET_LAN_DEVICE_ONLINE""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_lan_device_online(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
    
                if ("""CmdType":"getInfo""" in recv_data) and ( """PluginName":"health""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.get_health_info(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
    
                if ("""CmdType":"setAdd""" in recv_data) and ( """PluginName":"health""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_add_health_info(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
    
                if ("""CmdType":"setDel""" in recv_data) and ( """PluginName":"health""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_del_health_info(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                if ("""CmdType":"setEdit""" in recv_data) and ( """PluginName":"health""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_edit_health_info(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
                if ("""CmdType":"setSwitch""" in recv_data) and ( """PluginName":"health""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_switch_health_info(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)
    
                if ("""CmdType":"setSpeed""" in recv_data) and ( """PluginName":"smartNet""" in recv_data):
                    self.my_print("-----------get request---------%s" % recv_data) 
                    #sleep(1)
                    response=self.set_speed_smart_net(recv_data)
                    self.s.send(response+'\n')
                    self.my_print("send sesponse"+response)

    def send_response(self):
        while 1:
            #my_print("Response list will send to server %s" % response_list)
            if (len(response_list)):
                response_date=response_list[0]
                if response_date:
                    self.s.send(response_date)
                    self.my_print("Response will send to server:%s" % response_date)
                    del response_list[0]  
            #else:
                #my_print("No response date need send to tcp server!")
            #sleep(0.5) 

    def send_heart_beat(self):
        print('123213213213213213213213')
        while 1:
            try:
                msg='{"MAC":"%s","Type":"ping"} \n' % self.MAC  
                self.s.send(msg)
                self.my_print("Success to send heart beat to server:%s" % msg)    
                sleep(500)
            
            except:
                self.my_print("Lost connection with server,try to reconnect...")
                
                if (self.reconnect_to_server()):
                    my_print("Reconnect success!")
                else:
                    my_print("Reconnect failed,try to reconnect in 3s...")
                    #sleep(3)

    def send_speed(self):

        while 1:
                msg='{"Type":"report","PluginName":"LanDeviceConfig","RPCMethod":"Report","msgType":"REPORT","UserId":"NA","Parameter":{"Message":{"Devices":[{"DsBandwidth":"36","UsBandwidth":"5","DeviceMAC":"B0:E2:35:C6:AD:00"},{"DsBandwidth":"0","UsBandwidth":"0","DeviceMAC":"70:81:EB:39:49:EC"},{"DsBandwidth":"81","UsBandwidth":"2","DeviceMAC":"68:F7:28:BB:77:DA"},{"DsBandwidth":"82","UsBandwidth":"3","DeviceMAC":"00:0E:C6:C8:18:00"}]},"HGSpeed":"test","Time":"20161221224924","MD5":"test"},"ID":"NA","CmdType":"REPORT_LAN_DEVICE_SPEED","MAC":"%s","SequenceId":"NA"} \n' % self.MAC
                json=demjson.decode(msg)
                value = json['Parameter']['Message']['Devices']
                print value
                if (len(value) != 0 ):    
                    for x in xrange(0,len(value)):
                        upspeed=random.randint(10,50)
                        downspeed=random.randint(100,500)
                        json['Parameter']['Message']['Devices'][x]['UsBandwidth']=upspeed
                        json['Parameter']['Message']['Devices'][x]['DsBandwidth']=downspeed
                tmpmsg=demjson.encode(json)  
                tmpmsg = tmpmsg + '\n' 
                self.s.send(tmpmsg)
                self.my_print("Success to send heart beat to server:%s" % tmpmsg)    
                sleep(5)       

    def get_lan_net_info(self,recv_data):
        print("------get_lan_net_info------------")
        Num = len(self.DeviceList)
        devices=[]
        for x in xrange(0,Num):
            devices.append({
                'IP':self.DeviceList[x].IP,
                'OnlineTime':self.DeviceList[x].OnlineTime,
                'AccessInternet':self.DeviceList[x].AccessInternet,
                'DeviceMAC':self.DeviceList[x].DeviceMAC,
                'SSIDIndex':self.DeviceList[x].SSIDIndex,
                'ConnectType':self.DeviceList[x].ConnectType,
                'DownLoadLimit':self.DeviceList[x].DownLoadLimit,
                'UpLoadLimit':self.DeviceList[x].UpLoadLimit,
                'StorageAccessStatus':self.DeviceList[x].StorageAccessStatus,
                'DevName':self.DeviceList[x].DevName,
                'DevHostName':self.DeviceList[x].DevHostName})
        info = json.dumps(devices)
        ResultData={"Type": "response",
            "ResultData":{"Num": Num,"Info":devices},
            "PluginName": "LanDeviceConfig",
            "RPCMethod": "Get",
            "msgType": "RESPONSE",
            "UserId": "5916078243847",
            "ID": "",
            "CmdType": "GET_LAN_NET_INFO",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "049b9829"}
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data
        #print('{"Type":"response","ResultData":{"Num":"4","Info":[{"DevName":"MI5-android-xiami555","DownLoadLimit":"0","IP":"192.168.1.5","DevHostName":"MI5-android-xiami555","OnlineTime":"175","AccessInternet":"1","DeviceMAC":"B0:E2:35:C6:AD:00","SSIDIndex":"1","ConnectType":"1","UpLoadLimit":"0","StorageAccessStatus":"ON"},{"DevName":"ymhiPhone111","DownLoadLimit":"0","IP":"192.168.1.4","DevHostName":"ymhiPhone111","OnlineTime":"185","AccessInternet":"1","DeviceMAC":"70:81:EB:39:49:EC","SSIDIndex":"1","ConnectType":"1","UpLoadLimit":"0","StorageAccessStatus":"ON"},{"DevName":"XYH-PC","DownLoadLimit":"0","IP":"192.168.1.3","Port":"LAN4","DevHostName":"XYH-PC","OnlineTime":"160","AccessInternet":"1","DeviceMAC":"68:F7:28:BB:77:DA","ConnectType":"0","UpLoadLimit":"0","StorageAccessStatus":"ON"},{"DevName":"1-PC","DownLoadLimit":"0","IP":"192.168.1.2","Port":"LAN1","DevHostName":"1-PC","OnlineTime":"340","AccessInternet":"1","DeviceMAC":"00:0E:C6:C8:18:00","ConnectType":"0","UpLoadLimit":"0","StorageAccessStatus":"ON"}]},"PluginName":"LanDeviceConfig","RPCMethod":"Get","msgType":"RESPONSE","UserId":"7513464897573","ID":"","CmdType":"GET_LAN_NET_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"049b9829"}')

    def get_wifi_ssid_info(self,recv_data):
        print('------get_wifi_ssid_info---------')
        #print('{"Type":"response","ResultData":{"WIFIList":[{"Channel":"8","Guest":"0","PowerLevel":"100","Hidden":"0","Enable":"1","ENCRYPT":"5","PWD":"ODc2NTQzMjE=","SSIDIndex":"1","SSID":"LeiXing-512"}]},"PluginName":"WifiConfig","RPCMethod":"Get","msgType":"RESPONSE","UserId":"7513464897573","ID":"c52b402c","CmdType":"GET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"c52b402c"}')
        wifis = []
        Num=len(self.WIFIList)
        for x in xrange(0,Num):
            wifis.append({
                'Channel':self.WIFIList[x].Channel,
                'Guest':self.WIFIList[x].Guest,
                'PowerLevel':self.WIFIList[x].PowerLevel,
                'Hidden':self.WIFIList[x].Hidden,
                'Enable':self.WIFIList[x].Enable,
                'ENCRYPT':self.WIFIList[x].ENCRYPT,
                #Wifi密码Base64编码
                'PWD':base64.b64encode(self.WIFIList[x].PWD),
                'SSIDIndex':self.WIFIList[x].SSIDIndex,
                'SSID':self.WIFIList[x].SSID,
                })
        wifiinfo=json.dumps(wifis)
        ResultData={
            "Type": "response",
            "ResultData":{"WIFIList":wifis},
            "PluginName": "WifiConfig",
            "RPCMethod": "Get",
            "msgType": "RESPONSE",
            "UserId": "7513464897573",
            "ID": "c52b402c",
            "CmdType": "GET_WIFI_SSID_INFO",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "c52b402c"
        }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_wifi_ssid_info(self,recv_data):
        print('-------set_wifi_ssid_info---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        print parameter
        print type(parameter)
        ssid = parameter['SSIDIndex']
        pwd = parameter['PWD']
        print ssid 
        self.WIFIList[int(ssid)-1].Enable = parameter['Enable']
        #WIFI密码Base64解码
        self.WIFIList[int(ssid)-1].PWD = base64.b64decode(parameter['PWD'])
        self.WIFIList[int(ssid)-1].EncryptionMode = parameter['EncryptionMode']
        self.WIFIList[int(ssid)-1].SSIDAdvertisementEnabled = parameter['SSIDAdvertisementEnabled']
        self.WIFIList[int(ssid)-1].Channel = parameter['Channel']
        self.WIFIList[int(ssid)-1].SSID = parameter['SSIDName']
        print self.WIFIList[int(ssid)-1].SSID
        self.WIFIList[int(ssid)-1].PowerLevel = parameter['PowerLevel']
        self.WIFIList[int(ssid)-1].SSIDIndex = parameter['SSIDIndex']

        ResultData={
            "Type": "response",
            "ResultData":{},
            "PluginName": "WifiConfig",
            "RPCMethod": "Set",
            "UserId": "7513464897573",
            "ID": "c52b402c",
            "CmdType": "SET_WIFI_SSID_INFO",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "c52b402c"
        }
        ResultJson=json.dumps(ResultData)
        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_wifi_ssid_onoff(self,recv_data):
        print('-------set_wifi_ssid_info---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        print parameter
        ssid = parameter['SSIDIndex']
        enable = parameter['Enable']
        self.WIFIList[int(ssid)-1].Enable = parameter['Enable']

        ResultData={
            "Type": "response",
            "PluginName": "WifiConfig",
            "RPCMethod": "Set",
            "msgType": "RESPONSE",
            "UserId": "7660294766593",
            "ID": "8f12f30e",
            "CmdType": "SET_WIFI_SSID_ONOFF",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "8f12f30e"
        }
        ResultJson=json.dumps(ResultData)
        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_lan_speed_report_policy(self,recv_data):
        print('------set_lan_speed_report_policy---------')
        #print('{"Type":"response","ResultData":{"Enable":"1","Time":"5"},"PluginName":"LanDeviceConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"","CmdType":"SET_LAN_SPEED_REPORT_POLICY","MAC":"E06066FF92FA","Result":"1000000","SequenceId":""}')
        ResultData={
            "Type": "response",
            "ResultData": {"Enable": "1","Time": "5"},
            "PluginName": "LanDeviceConfig",
            "RPCMethod": "Set",
            "msgType": "RESPONSE",
            "UserId": "7513464897573",
            "ID": "c52b402c",
            "CmdType": "SET_LAN_SPEED_REPORT_POLICY",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "c52b402c"
        }
        ResultJson=json.dumps(ResultData)
        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key2)
        return data
    def report_lan_device_speed(self,recv_data):
        print('---------report_lan_device_speed-----------')
        #print('{"Type":"report","PluginName":"LanDeviceConfig","RPCMethod":"Report","msgType":"REPORT","UserId":"NA","Parameter":{"Message":{"Devices":[{"DsBandwidth":"36","UsBandwidth":"5","DeviceMAC":"B0:E2:35:C6:AD:00"},{"DsBandwidth":"0","UsBandwidth":"0","DeviceMAC":"70:81:EB:39:49:EC"},{"DsBandwidth":"81","UsBandwidth":"2","DeviceMAC":"68:F7:28:BB:77:DA"},{"DsBandwidth":"82","UsBandwidth":"3","DeviceMAC":"00:0E:C6:C8:18:00"}]},"HGSpeed":"test","Time":"20161221224924","MD5":"test"},"ID":"NA","CmdType":"REPORT_LAN_DEVICE_SPEED","MAC":"E06066FF92FA","SequenceId":"NA"}')

    def get_lan_device_online(self,recv_data):
        print('--------get_lan_device_online----------')
        #print('{"Type":"response","ResultData":{"Devices":[{"Enable":"2","DeviceMAC":"00:0E:C6:C8:18:00"},{"Enable":"2","DeviceMAC":"68:F7:28:BB:77:DA"},{"Enable":"2","DeviceMAC":"70:81:EB:39:49:EC"},{"Enable":"2","DeviceMAC":"B0:E2:35:C6:AD:00"}]},"PluginName":"LanDeviceConfig","RPCMethod":"Get","msgType":"RESPONSE","UserId":"5238179889157","ID":"","CmdType":"GET_LAN_DEVICE_ONLINE","MAC":"74ADB76C4800","Result":"1000000","SequenceId":"46120731"}')
        devices = []
        Num = len(self.DeviceList)
        for x in xrange(0,Num):
            devices.append({
                'DeviceMAC':self.DeviceList[x].DeviceMAC,
                'Enable':self.DeviceList[x].Enable,
                })
        info = json.dumps(devices)
        ResultData={
            "Type": "response",
            "ResultData":{"Devices":devices},
            "PluginName": "LanDeviceConfig",
            "RPCMethod": "Get",
            "msgType": "RESPONSE",
            "UserId": "8022996418574",
            "ID": "",
            "CmdType": "GET_LAN_DEVICE_ONLINE",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "88677041"
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_lan_device_online(self,recv_data):
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        print parameter
        device = parameter['Devices']
        devicemac=device[0]['DeviceMAC']
        deviceenable=device[0]['Enable']
        print devicemac 
        print deviceenable
        Num= len(self.DeviceList)
        for x in xrange(0,Num):
            if(devicemac==self.DeviceList[x].DeviceMAC):
                self.DeviceList[x].Enable=deviceenable
                print self.DeviceList[x].Enable
                info = [{"Enable":self.DeviceList[x].Enable,"DeviceMAC":self.DeviceList[x].DeviceMAC}]

        ResultData={
            "Type": "response",
            "ResultData":{"Devices":info},
            "PluginName": "LanDeviceConfig",
            "RPCMethod": "set",
            "msgType": "RESPONSE",
            "UserId": "5408803520540",
            "ID": "",
            "CmdType": "SET_LAN_DEVICE_ONLINE",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "82827434"
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def get_health_info(self,recv_data):
        print('-------get_health_info---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        print self.NetTimeList
        ResultData={
            "Type": "response",
            "ResultData":{"reason": "have","netTimeList":self.NetTimeList},
            "PluginName": "health",
            "RPCMethod": "get",
            "msgType": "RESPONSE",
            "UserId": "5408803520540",
            "ID": "",
            "CmdType": "getInfo",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "94221234"
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_add_health_info(self,recv_data):
        print('-------set_add_health_info---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        print parameter
        Num = len(self.NetTimeList)
        NetTime = parameter
        NetTime['enable']=1
        NetTime['netTimeId']=Num+1
        print NetTime
        print self.NetTimeList
        self.NetTimeList.append(NetTime)
        print self.NetTimeList

        ResultData={
            "Type": "response",
            "ResultData":{"reason": "success","netTimeId":Num+1,"bTime":NetTime['bTime'],"eTime":NetTime['eTime'],"week":NetTime['week']},
            "PluginName": "health",
            "RPCMethod": "get",
            "msgType": "RESPONSE",
            "UserId": "5408803520540",
            "ID": "",
            "CmdType": "setAdd",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "94221234"
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_del_health_info(self,recv_data):
        print('-------set_del_health_info---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        print parameter
        Num = len(self.NetTimeList)
        NetTimeID = parameter['netTimeId']
        index = 0
        print NetTimeID
        print self.NetTimeList
        for x in xrange(0,Num):
            print x
            print self.NetTimeList[x]['netTimeId']
            if (self.NetTimeList[x]['netTimeId'] == NetTimeID):
                index = x
                print index 
        del self.NetTimeList[x]
        Num = len(self.NetTimeList)
        print Num
        for x in xrange(0,Num):
            self.NetTimeList[x]['netTimeId'] == x+1

        print self.NetTimeList

        ResultData={
                  "Type": "response",
                  "ResultData": {
                    "reason": "success"
                  },
                  "PluginName": "health",
                  "RPCMethod": "set",
                  "msgType": "RESPONSE",
                  "UserId": "5250425028624",
                  "ID": "",
                  "CmdType": "setDel",
                  "MAC": self.MAC,
                  "Result": "1000000",
                  "SequenceId": "1916672f"
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        print data 
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        print data 
        return data

    def set_edit_health_info(self,recv_data):
        print('-------set_del_health_info---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        index = parameter['netTimeId']
        print index
        print self.NetTimeList
        self.NetTimeList[int(index)-1]['eTime']=parameter['eTime']
        self.NetTimeList[int(index)-1]['week']=parameter['week']
        self.NetTimeList[int(index)-1]['bTime']=parameter['bTime']
        print self.NetTimeList

        ResultData={
            "Type": "response",
            "ResultData":{"reason": "success","netTimeId":parameter['netTimeId'],"bTime":parameter['bTime'],"eTime":parameter['eTime'],"week":parameter['week']},
            "PluginName": "health",
            "RPCMethod": "set",
            "msgType": "RESPONSE",
            "UserId": "5250425028624",
            "ID": "",
            "CmdType": "setEdit",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "cd7cbe34"
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_switch_health_info(self,recv_data):
        print('-------set_switch_health_info---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        index = parameter['netTimeId']
        print index
        print self.NetTimeList
        self.NetTimeList[int(index)-1]['enable']=parameter['enable']
        print self.NetTimeList

        ResultData={
            "Type": "response",
            "ResultData":{"reason": "success","netTimeId":parameter['netTimeId'],"enable":parameter['enable']},
            "PluginName": "health",
            "RPCMethod": "set",
            "msgType": "RESPONSE",
            "UserId": "5250425028624",
            "ID": "",
            "CmdType": "setEdit",
            "MAC": self.MAC,
            "Result": "1000000",
            "SequenceId": "cd7cbe34"
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def set_speed_smart_net(self,recv_data):
        print('-------set_speed_smart_net---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        parameter = JsonTool.get_json_value(recv_data,'Parameter')
        devmac = parameter['devMac']
        print devmac
        for x in xrange(0,len(self.DeviceList)):
            if(self.DeviceList[x].DeviceMAC == devmac):
                self.DeviceList[x].DownLoadLimit = parameter['download']
                self.DeviceList[x].UpLoadLimit = parameter['upload']

        ResultData={
            "ResultData":{"reason": "success"},
            "MAC": "12345678",
              "Type": "response",
              "PluginName": "smartNet",
              "CmdType": "setSpeed",
              "UserId": "12341234",
              "RPCMethod": "set",
              "ID": "12341234",
              "SenquenceId": "12341234",
              "Result": "1000000",
            }
        ResultJson=json.dumps(ResultData)

        data = JsonTool.replace_json_from_json(recv_data,ResultJson,self.key1)
        data = JsonTool.replace_json_from_json(recv_data,data,self.key2)
        return data

    def device_register(self,recv_data):
        print('-------device_register---------')
        #print('{"Type":"response","PluginName":"WifiConfig","RPCMethod":"Set","msgType":"RESPONSE","UserId":"7513464897573","ID":"85471939","CmdType":"SET_WIFI_SSID_INFO","MAC":"E06066FF92FA","Result":"1000000","SequenceId":"85471939"}')
        print recv_data
        str = recv_data[4:]
        print str 
        challengecode=JsonTool.get_json_value(str,'ChallengeCode')
        print challengecode
        sn = 'SHYGW000000AAC-H112345678admin'
        password = '00000001'
        msg = challengecode+sn+password
        print 'pinjie',msg
        m = hashlib.md5()
        m.update(msg)  
        m_d=m.hexdigest()
        ResultData={"RPCMethod":"Register","ID":1,"MAC":"74ADB72FEFA1","CheckGateway":m_d,"DevRND":"1234567890123456"}
        ResultJson=json.dumps(ResultData)
        print 'data:111',ResultJson
        lena=int(len(ResultJson))
        len16 =  struct.pack(">L",lena)
        ResultJson = len16+ResultJson
        return ResultJson
    
    def plugin_install(self,recv_data):
        print('-------plugin_install---------')
        print recv_data[4:]
        ResultData={"ID":1914969,"Result":0}
        ResultJson=json.dumps(ResultData)
        data = JsonTool.replace_json_from_json(recv_data[4:],ResultJson,self.key3)
        print 'data:---',data
        lena=int(len(data))
        len16 =  struct.pack(">L",lena)
        print lena,len16
        ResultJson = len16+data
        print 'ResultJson',ResultJson
        return ResultJson


if __name__ == '__main__':
    
    gateway1=Gateway()
    print len(gateway1.WIFIList)
    for x in xrange(0,len(gateway1.WIFIList)):
        gateway1.WIFIList[x].displayWIFI()
    for i in xrange(0,len(gateway1.DeviceList)):
        gateway1.DeviceList[i].displayDevice()


    gateway1.connect_to_server()
    #gateway1.connect_to_device_server()
    threads = []
    threads.append( threading.Thread(target=gateway1.send_heart_beat,args=()))
    threads.append( threading.Thread(target=gateway1.get_request,args=()))
    #threads.append( threading.Thread(target=gateway1.get_device_request,args=()))
    threads.append( threading.Thread(target=gateway1.send_speed,args=()))
    
    for t in threads:
        t.setDaemon(True)
        t.start()
 
    t.join()

    


