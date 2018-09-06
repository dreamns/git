#!/usr/bin/python
# -*- coding: UTF-8 -*-
import json
class Device:

    DeviceMAC = ''
    IP = ''
    DevName = ''
    DevHostName = ''
    DownLoadLimit = ''
    AccessInternet = ''
    OnlineTime = ''
    SSIDIndex = ''
    ConnectType = ''
    UpLoadLimit = ''
    StorageAccessStatus = ''
    MyClass=''
    Enable=''
    def __init__ (self,ip,devicemac,onlinetime,accessinternet,ssidindex,connecttype,downloadlimit,uploadlimit,storage,myclass,devname,devhostname,enable):
        self.DeviceMAC = devicemac
        self.IP = ip;
        self.DevName = devname 
        self.DevHostName = devhostname
        self.DownLoadLimit = downloadlimit
        self.AccessInternet = accessinternet
        self.OnlineTime = onlinetime
        self.SSIDIndex = ssidindex
        self.ConnectType = connecttype
        self.UpLoadLimit = uploadlimit
        self.StorageAccessStatus = storage
        self.MyClass = myclass
        self.Enable = enable

    def displayDevice(self):
        print "Device: " +self.DeviceMAC+" "+ self.IP+' '+self.DevName+' '+self.DevHostName+' '+self.DownLoadLimit+' ' + self.AccessInternet+' '+ \
        self.OnlineTime + ' '+self.SSIDIndex + ' '+self.ConnectType + ' '+self.UpLoadLimit + ' '+self.StorageAccessStatus +' '

    def __repr__(self):
        print((self.IP,self.DevName))

if __name__ == '__main__':
    device1=Device('XYH-PC','100','192.168.1.3','XYH-PC-1','154','1','B0:E2:35:C6:AD:00','1','1','20','ON','11')
    device1.displayDevice()
    device1.__repr__()
    devices=[device1,device1]
    print json.dumps(['foo', {'bar': ('baz', None, 1.0, 2)}])
    Infostr = json.dumps(devices,default=lambda o: o.__dict__, sort_keys=True, indent=2)
    print Infostr