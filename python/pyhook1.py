import pythoncom
import pyHook
import time,threading
exitFlag = 0
def onMouseEvent(event):
    # 监听鼠标事件
    print "MessageName:", event.MessageName
    #print "Message:", event.Message
    #print "Time:", event.Time
    #print "Window:", event.Window
    #print "WindowName:", event.WindowName
    #print "Position:", event.Position
    #print "Wheel:", event.Wheel
    #print "Injected:", event.Injected
    #print "---"

    # 返回 True 以便将事件传给其它处理程序
    # 注意，这儿如果返回 False ，则鼠标事件将被全部拦截
    # 也就是说你的鼠标看起来会僵在那儿，似乎失去响应了
    return True

def onKeyboardEvent(event):
    # 监听键盘事件
    
    print "MessageName:", event.MessageName
    #print "Message:", event.Message
    #print "Time:", event.Time
    #print "Window:", event.Window
    #print "WindowName:", event.WindowName
    #print "Ascii:", event.Ascii, chr(event.Ascii)
    #print "Key:", event.Key
    #print "KeyID:", event.KeyID
    #print "ScanCode:", event.ScanCode
    #print "Extended:", event.Extended
    #print "Injected:", event.Injected
    #print "Alt", event.Alt
    #print "Transition", event.Transition
    #print "---"
    
    # 同鼠标事件监听函数的返回值
    return True


def keyboard():
    print "keyboard"
    # 创建一个“钩子”管理对象
    hm = pyHook.HookManager()

    # 监听所有键盘事件
    hm.KeyDown = onKeyboardEvent
    # 设置键盘“钩子”
    hm.HookKeyboard()

    # 监听所有鼠标事件
    #hm.MouseAll = onMouseEvent
    # 设置鼠标“钩子”
    #hm.HookMouse()

    # 进入循环，如不手动关闭，程序将一直处于监听状态
    pythoncom.PumpMessages()    

def test():
    while(True):
        print "test" 
        time.sleep(2)
    




class myThread (threading.Thread):   #继承父类threading.Thread
    def __init__(self, threadID, name, counter):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.counter = counter
    def run(self):                   #把要执行的代码写到run函数里面 线程在创建后会直接运行run函数 
        print "Starting " + self.name
        print_time(self.name, self.counter, 5)
        print "Exiting " + self.name

def print_time(threadName, delay, counter):
    while counter:
        if exitFlag:
            threading.Thread.exit()
        time.sleep(delay)
        print "%s: %s" % (threadName, time.ctime(time.time()))
        counter -= 1

# 创建新线程

class pingpang(myThread):
    def run(self):
        print "123"
class alert(myThread):
    def run (self):
        print "321"

thread1 = pingpang(1, "Thread-1", 1)
thread2 = alert(2, "Thread-2", 2)

# 开启线程
thread1.start()
thread2.start()

#print "Exiting Main Thread"


#if __name__ == "__main__":
    #threads =[]
    #t1 = threading.Thread(target=test,name = "test")
    #threads.append(t1)
    #t2 = threading.Thread(target=keyboard,name = "keyboard")
    #threads.append(t2)
    
    #for t in threads:
        #t.setDaemon(True)
        #t.start()
        #t.join()

    