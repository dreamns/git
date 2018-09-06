#!/usr/bin/python
# -*- coding: UTF-8 -*-

class WIFI:

    Channel = ''
    Guest = ''
    PowerLevel = ''
    Hidden = ''
    Enable = ''
    ENCRYPT = ''
    PWD = ''
    SSIDIndex = ''
    SSID = ''


    def __init__ (self,channel,guest,powerlevel,hidden,enable,encrypt,pwd,ssidindex,ssid):
        self.Channel = channel
        self.Guest = guest;
        self.PowerLevel = powerlevel 
        self.Hidden = hidden
        self.Enable = enable
        self.ENCRYPT = encrypt
        self.PWD = pwd
        self.SSIDIndex = ssidindex
        self.SSID = ssid


    def displayWIFI(self):
        print 'WIFI: ' + self.Channel +' '+ self.Guest +' '+ self.PowerLevel+' '+ self.Hidden +' '+self.Enable +' '+ self.ENCRYPT +' '+ self.PWD +' '+ self.SSIDIndex +' '+self.SSID

if __name__ == '__main__':
    WIFI1=WIFI('8','0','100','0','1','5','11111111','1','CMCC-WIFI')
    WIFI1.displayWIFI()