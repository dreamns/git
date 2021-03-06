# 说明

## 用例说明

用例分为三部分

- testinfo 表示用例介绍
- testcase 用例的执行步骤
- check 用例的检查点

```
testinfo:
    - id: test0003
      title: 发布文章
testcase:
    - element_info: //android.view.View[@content-desc='请输入标题']
      find_type: xpath
      operate_type: set_value
      text: pls input title
    - element_info: //android.view.View[@content-desc='请输入正文']
      find_type: xpath
      operate_type: set_value
      text: auto test msg
    - element_info: com.jianshu.haruki:id/tv_publish
      find_type: id
      operate_type: click
check:
    - element_info: //android.widget.TextView[@text='好东西值得与更多好朋友分享，分享文章到你的社交帐号上吧！']
      find_type: xpath
```


## 用例的关键字分析

- get_value: 1 .表示需要取元素到值


```
testcase:
    - element_info: //android.widget.ListView[1]/android.widget.LinearLayout[2]/android.widget.RelativeLayout[2]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]//android.widget.TextView[@resource-id='com.huawei.works.knowledge:id/title']
      find_type: xpath
      operate_type: click
      get_value: 1 # 表示需要取元素到值
```


- find_type 元素到类型。现在支持id,xpath


```
testcase:
    - element_info: //android.widget.RadioButton[@text='知识']
      find_type: xpath
      operate_type: click
```

- operate_type 元素到操作类型.现在支持click(表示点击),set_value（便是文本框输入值，一般需要和text合用），get_value（取元素的文本值）

```
testcase:
    - element_info: //android.widget.RadioButton[@text='知识']
      find_type: xpath
      operate_type: click
    - element_info: //android.widget.TextView[@text='技术专区']
      find_type: xpath
      operate_type: click
    - element_info: //android.widget.ListView[1]/android.widget.LinearLayout[1]//android.widget.TextView[@resource-id='com.huawei.works.knowledge:id/title']
      find_type: xpath
      operate_type: get_value
    - element_info: //android.view.View[@content-desc='请输入标题']
      find_type: xpath
      operate_type: set_value
      text: pls input title
```

- isWebView: 1 表示切换到webview,可以用到testcase和check中

```
check:
    - element_info: //*[@id="app"]/div/div[2]/section[2]/div[1]/div
      find_type: xpath
      isWebView: 1
```
