#coding=utf-8
import appium 
import time
from appium import webdriver
import requests
import pytest
import request_test

desired_caps = {}
desired_caps['platformName'] = 'Android'
desired_caps['platformVersion'] = '6.0'
desired_caps['deviceName'] = 'Android Emulator'
desired_caps['appPackage'] = 'com.cmri.universalapp'

desired_caps['appActivity'] = 'com.cmri.universalapp.login.activity.SplashActivity'


driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
driver.implicitly_wait(10)

def test_login():
    print("start")    
    time.sleep(15)
    print("start")
    print(driver.current_activity)    
    if(driver.current_activity=='.SmartMainActivity'):
        print(driver.current_activity)
        #driver.find_element_by_id('com.cmri.universalapp:id/check_box_bottom_tab_find').click()
        #driver.find_element_by_id('com.cmri.universalapp:id/tvLogout').click()
        #driver.find_element_by_id('com.cmri.universalapp:id/dialog_ok_btn').click()    
    else:
        driver.find_element_by_id("com.cmri.universalapp:id/etPhone").send_keys("13735530767")
        
        driver.find_element_by_id("com.cmri.universalapp:id/switch_login_type").click() 
        driver.find_element_by_id("com.cmri.universalapp:id/etPassword").send_keys("uiop123456")  
        
        driver.find_element_by_id("com.cmri.universalapp:id/tvLogin").click()
    try:
        driver.find_element_by_id("com.cmri.universalapp:id/ivCancelDlg").click()
    except:
        print("没有推送页面，直接进入")
    device_add()
    driver.find_element_by_xpath("//android.widget.RelativeLayout[@resource-id=\"com.cmri.universalapp:id/check_box_bottom_tab_device\"]/android.widget.ImageView[1]").click()
    driver.find_element_by_name("njwlgw").click()
    
    print(111)
    driver.quit()    

def device_add():
    device_add_deviceid = 'CMCC50294D20B64D'
    device_add_devicetype='10074'
    device_add_url = 'http://open.home.komect.com:10087/espapi/cloud/json/devices?deviceId=%s&deviceType=%s&startTime=1500883128' %(device_add_deviceid,device_add_devicetype)
    print(device_add_url)
    device_add_header = {
        #'API_KEY':'KAiHIjVu7dA3QZHhLijuEKr9QH15pmd5m1pYeqJ2hmNd6q6HAIG3efCXe5PAo3eh',
        'API_KEY':'4YSpmWszCOiyVnjSz_EyHMKxNjUC9r7VtL9yogUnT4x0OgXK4h9fAfETopw3zqyc',
        #'API_KEY':'0ZSUmJtC_9JgqjNTOGUaRM-N_8Ij-sGe14bTtRgOfw4aHOLMz6MsW-nphbpr5om3',             
        'Content-Type':'application/json'            
    }   
    r=requests.post(device_add_url,headers=device_add_header)
    print(r.text)

if __name__ == "__main__":
    test_login()
    