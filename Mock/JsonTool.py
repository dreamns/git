#!/usr/bin/python
import demjson
import json
import struct

def get_json_value(data,key):
    json=demjson.decode(data)
    value = json[key]
    return value

def replace_json_from_json(data,targetdata,key):
    json=demjson.decode(data)
    value=json[key]
    targetlist=demjson.decode(targetdata)
    targetlist[key]=value
    targetjson=demjson.encode(targetlist)
    return targetjson

def replace_json_value(value,data,key):
    json=demjson.decode(data)
    json[key]=value
    targetjson=demjson.encode(json)
    return targetjson
def toHex(s):
    lst = []
    for ch in s:
        hv = hex(ord(ch)).replace('0x', '')
        if len(hv) == 1:
            hv = '0'+hv
        lst.append(hv)
   
    return reduce(lambda x,y:x+y, lst)



if __name__ == '__main__':
    json = '{"RPCMethod": "BootInitiation","ID": 123,"PROTVersion": "1.0.0","MAC": "E06066FF92FA"}';
    print json
    a = toHex(json)
    print a 
    
    lena=int(len(json))
    print type(lena),lena

    x16 = hex(lena)
    print type(x16),x16
    print struct.pack(">L",0x56)
    print struct.pack(">L",86)

    len16 =  struct.pack(">L",86)
    str = len16+json 
    print str 
    str1 = len16 + a 
    print str1 