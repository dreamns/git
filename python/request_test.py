import requests
import json
#import pythoncom
#import pyHook
import threading
import time

device_id = 'CMCC50294D20B6BD'

server_url = "http://hsop.komect.com:16680"
server_ip =""
server_port=""
API_KEY=""
device_type=""
class devices:
   
    def dooralert(self,doorstatus):
        print(111)
        device_id = "AC0501DE0E004B1200"
        proxy_id = "CMCC50294D20B61D"
        #url = "http://hsop.komect.com:16680/deviceio/mljson"
        url = "http://218.205.115.238:28080/deviceio/mljson"
        now =int(time.time())    
        data ='{"measures":[{"deviceId":"%s","params":[{"name":"doorStatus","value":"%s"},{"name":"protectionStatus","value":"1"}],"timestamp":%s}],"proxyId":"%s","sequenceNumber":"0501DE0E01","version":2}' % (device_id,doorstatus,now,proxy_id)
        print(data)
        j = json.dumps(data)
        http_headers = {"Host":"218.205.115.238:28080","Content-Type": "text/xml","Accept":"*/*","User-Agent":"IOT Proxy","Expect":"100-continue","Content-Length":"231"}
        r = requests.post(url,data=data, headers=http_headers)
        #r1 =r.requests.post(url,data=j)
        respon = r.text
        print(respon)
        
    def device_send_pingpang(self,device_id):
        #device_pingpong_url = 'http://hsop.komect.com:16680/deviceio/mljson?id=%s&timeout=60' % device_id
        now1 = time.asctime( time.localtime(time.time()) )
        print( now1)             
        device_pingpong_url = '%s/deviceio/mljson?id=%s&timeout=60' %  (server_url,device_id)
        device_add_header = {
            'Host':'hsop.komect.com:16680',
            'Accept':'*/*',
            'User-Agent':'IOT Proxy'
        }
        r=requests.get(device_pingpong_url,headers=device_add_header,timeout=70)
           
        return r
    
    
    def device_add(self):
        device_add_deviceid = 'CMCC50294D20B64D'
        device_add_devicetype='10074'
        device_add_url = 'http://open.home.komect.com:10087/espapi/cloud/json/devices?deviceId=%s&deviceType=%s&startTime=1500883128' %(device_add_deviceid,device_add_devicetype)
        print(device_add_url)
        device_add_header = {
            'API_KEY':'KAiHIjVu7dA3QZHhLijuEKr9QH15pmd5m1pYeqJ2hmNd6q6HAIG3efCXe5PAo3eh',
            #'API_KEY':'0ZSUmJtC_9JgqjNTOGUaRM-N_8Ij-sGe14bTtRgOfw4aHOLMz6MsW-nphbpr5om3',            
            'Content-Type':'application/json'            
        }   
        r=requests.post(device_add_url,headers=device_add_header)
        print(r.text)
    #def device_addd(self):

        
        
    def device_del(self):
        device_add_deviceid = 'CMCC50294D20B64D'
        device_add_devicetype='10074'
        #device_add_url = 'http://open.home.komect.com:10087/espapi/cloud/json/devices/%s?clear=true&keepOnAccount=false&keepSlave=false&keepSlaveOnGateway=true' %(device_add_deviceid)
       
        device_add_url  = 'http://open.home.komect.com:10087/espapi/cloud/json/devices/%s?clear=false&keepOnAccount=false&keepSlave=false&keepSlaveOnGateway=true' %(device_add_deviceid)
        print(device_add_url)
        device_add_header = {
            'API_KEY':'KAiHIjVu7dA3QZHhLijuEKr9QH15pmd5m1pYeqJ2hmNd6q6HAIG3efCXe5PAo3eh',
            'Content-Type':'application/json'            
        }
        r=requests.delete(device_add_url,headers=device_add_header)
        print(r.text)        
        
    def device_connect(self):
        global device_id
        global server_url
        i = 1 
        while(i>0):
            i = i+1
            #
            r=self.device_send_pingpang(device_id)
            #
            if(r.status_code==200):    
                if( r.text== ''):
                    now1 = now1 = time.asctime( time.localtime(time.time()) )
                    print('pingpang success %s times' % i, now1)
                elif('"parameters":[{"name"' in r.text):
                    s = json.loads(r.text)
                    gwname = s["commands"][0]['parameters'][0]['name']
                    gwVolume = s["commands"][0]['parameters'][0]['value']
                    print(gwname + ':' + gwVolume)
                    commandId = s["commands"][0]['commandId']
                    #get 1 times
                    device_url1= '%s/deviceio/mljson?id=%s&timeout=60' %  (server_url,device_id)
                    device_header1={
                        'Host': 'hsop.komect.com:16680',
                        'Accept': '*/*',
                        'User-Agent': 'IOT Proxy'                        
                        }
                    r = requests.get(device_url1,headers=device_header1)
                    #post (2 times)
                    for num in range(0,2):
                        device_url1 = 'http://hsop.komect.com:16680/deviceio/mljson'
                        device_header1 = {
                            'Host':'hsop.komect.com:16680',
                            'Accept':'*/*',
                            'User-Agent':'IOT Proxy',
                            'Content-Type':'text/xml',
                            'Content-Length': '107',
                            'Expect': '100-continue'                        
                        }
                        device_body = '{"proxyId":"%s","responses":[{"commandId":%s,"result":1}],"sequenceNumber":"1","version":2}' %(device_id,commandId)
                        r = requests.post(device_url1,data=device_body,headers=device_header1) 
                        if(r.status_code==200):
                            print('success')
                        
                else:
                    print(r.text)
                    break
                

if __name__ == "__main__":
    d = devices()
    #d.dooralert(0)
    #d.device_add()
    #d.device_del()
    d.device_connect()
    #threads = []
    #t1 = threading.Thread(target=d.device_connect)
    #threads.append(t1)
    #t2 = threading.Thread(target=d.json_test)
    #threads.append(t2)
    #for t in threads:
        ##t.setDaemon(True)
        #t.start() 
    ##t.join()
        