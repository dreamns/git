# -*- coding: UTF-8 -*-
import socket
import threading
import time
#哈哈
def tcplink(sock, addr):
    print('Accept new connection from %s:%s...' % addr)
    while True:
        data = sock.recv(1024)
        time.sleep(1)
        if not data or data.decode('utf-8') == 'exit':
            break
        print(data)
        if(b"BootInitiation" in data):
            str = b"""{"Result":0,"ChallengeCode":"4519600489575891","ID":1}"""
            str1 ="""{"Result":0,"ChallengeCode":"4519600489575891","ID":1}"""
            print(type(str1))
            print(type(str))
            sock.send(str)
        
        #sock.send(('Hello, %s!' % data.decode('utf-8')).encode('utf-8'))
    sock.close()
    print('Connection from %s:%s closed.' % addr)
    

if __name__ == "__main__":  
    s =socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    s.bind(('192.168.254.107',19123))
    s.listen(5)
    print('waiting for connection')
    while True:
        sock,addr=s.accept()
        t = threading.Thread(target=tcplink, args=(sock, addr))
        t.start()

        
     