import pythoncom
import pyHook
import time,threading
exitFlag = 0
def onMouseEvent(event):
    # ��������¼�
    print "MessageName:", event.MessageName
    #print "Message:", event.Message
    #print "Time:", event.Time
    #print "Window:", event.Window
    #print "WindowName:", event.WindowName
    #print "Position:", event.Position
    #print "Wheel:", event.Wheel
    #print "Injected:", event.Injected
    #print "---"

    # ���� True �Ա㽫�¼����������������
    # ע�⣬���������� False ��������¼�����ȫ������
    # Ҳ����˵�����꿴�����Ὡ���Ƕ����ƺ�ʧȥ��Ӧ��
    return True

def onKeyboardEvent(event):
    # ���������¼�
    
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
    
    # ͬ����¼����������ķ���ֵ
    return True


def keyboard():
    print "keyboard"
    # ����һ�������ӡ��������
    hm = pyHook.HookManager()

    # �������м����¼�
    hm.KeyDown = onKeyboardEvent
    # ���ü��̡����ӡ�
    hm.HookKeyboard()

    # ������������¼�
    #hm.MouseAll = onMouseEvent
    # ������ꡰ���ӡ�
    #hm.HookMouse()

    # ����ѭ�����粻�ֶ��رգ�����һֱ���ڼ���״̬
    pythoncom.PumpMessages()    

def test():
    while(True):
        print "test" 
        time.sleep(2)
    




class myThread (threading.Thread):   #�̳и���threading.Thread
    def __init__(self, threadID, name, counter):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.counter = counter
    def run(self):                   #��Ҫִ�еĴ���д��run�������� �߳��ڴ������ֱ������run���� 
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

# �������߳�

class pingpang(myThread):
    def run(self):
        print "123"
class alert(myThread):
    def run (self):
        print "321"

thread1 = pingpang(1, "Thread-1", 1)
thread2 = alert(2, "Thread-2", 2)

# �����߳�
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

    