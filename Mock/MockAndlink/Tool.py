
import requests

def request_post(url,header,payload):
    print(header)
    print(payload)
    r = requests.post(url,headers=header,data=payload)
    print(r.text)