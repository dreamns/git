import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

s.connect(('127.0.0.1', 19123))
str = b"""{"RPCMethod":"BootInitiation","ID":1,"Vendor":"CMDC","ProuductCLass":"CM112Z","SWVersion":"V1.0.18","HDVersion":"1.0","OSType":"OSGI","OSVersion":"v1.0","PROTVersion":"1.0.0","WanIPAddr":"192.168.3.225","WanIPv6Addr":"","MAC":"24615ACE13AC","BDAccount":"12345678","Password":"test0012"}"""
print(str)
s.send(str)
print(s.recv(1024).decode('utf-8'))
s.send(b'exit')
s.close()