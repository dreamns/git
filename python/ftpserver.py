import os
import sys
from pyftpdlib.authorizers import DummyAuthorizer,AuthenticationFailed
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer

from hashlib import md5


class MyHandler(FTPHandler):

    def on_connect(self):
        print ("%s:%s connected" % (self.remote_ip, self.remote_port))

    def on_disconnect(self):
        # do something when client disconnects
        pass

    def on_login(self, username):
        # do something when user login
        pass

    def on_logout(self, username):
        # do something when user logs out
        pass

    def on_file_sent(self, file):
        # do something when a file has been sent
        pass

    def on_file_received(self, file):
        # do something when a file has been received
        pass

    def on_incomplete_file_sent(self, file):
        # do something when a file is partially sent
        pass

    def on_incomplete_file_received(self, file):
        # remove partially uploaded files
        import os
        os.remove(file)

class DummyMD5Authorizer(DummyAuthorizer):
    def validate_authentication(self,username,password,handler):
        #sys.version_info(major=3, minor=6, micro=3, releaselevel='final', serial=0)
        print (sys.version_info)
        if sys.version_info >= (3,0):
            print(password,type(password))
            hash = md5(password.encode('latin1')).hexdigest()
            print(hash,type(hash))

        else:
            hash = md5(password).hexdigest()
        try:
            if self.user_table[username]['pwd'] !=hash:
                raise KeyError
        except KeyError: 
            raise AuthenticationFailed
        

def main():
    
    hash = md5('12345'.encode('UTF-8')).hexdigest()
    authorizer = DummyAuthorizer()
    #authorizer = DummyMD5Authorizer()
    #authorizer.add_user('user1',hash,os.getcwd(),perm='elradfmwMT')
    authorizer.add_anonymous(os.getcwd())
    handler = MyHandler
    handler.authorizer = authorizer
    handler.banner = "pyftpdlib based ftpd ready."
    
    address = ('192.168.1.2', 21)
    server = FTPServer(address, handler)
    
    server.max_cons = 256
    server.max_cons_per_ip = 5    
    server.serve_forever()

if __name__ == '__main__':
    main()
    