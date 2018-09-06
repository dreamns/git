-ignorewarnings
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable


##cmcc-enc
-keep class com.cmcc.security.**{*;}
-dontwarn com.cmcc.security.**

##greendao
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

##sso
-dontwarn com.cmcc.dhsso.**
-keep class com.cmcc.dhsso.** { *;}

##fastjson
-dontwarn android.support.**
-dontwarn com.alibaba.fastjson.**
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
-keep class com.alibaba.fastjson.** { *; }
-keepclassmembers class * {
public <methods>;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}
-keepattributes Signture

# Umeng Statistics
-dontwarn u.aly.**

# Alibaba And-Fix framework
-keep class * extends java.lang.annotation.Annotation
-keepclasseswithmembernames class * {
    native <methods>;
}
##swipemenulistview
-keep class com.baoyz.swipemenulistview.**{*;}
-dontwarn com.baoyz.swipemenulistview.**

##eventbus
-keep class org.greenrobot.**{*;}
-dontwarn org.greenrobot.**
-keepclassmembers class ** {
public void onMessage(**);
public void onEvent*(**);
void onEvent*(**);
}

-dontwarn org.**
-keep class org.** { *;}

##littlec
-dontwarn im.yixin.**
-keep class im.yixin.** { *;}

-dontwarn com.littlec.sdk.**
-keep class com.littlec.sdk.** { *;}

-dontwarn android.net.**
-keep class android.net.** { *;}

-dontwarn com.android.**
-keep class com.android.** { *;}

-dontwarn com.novell.**
-keep class com.novell.** { *;}

-dontwarn de.measite.smack.**
-keep class de.measite.smack.** { *;}

-dontwarn org.apache.**
-keep class org.apache.** { *;}

-dontwarn org.jivesoftware.**
-keep class org.jivesoftware.** { *;}

-dontwarn org.xbill.DNS.**
-keep class org.xbill.DNS.** { *;}

-dontwarn org.jivesoftware.smack.**
-keep class org.jivesoftware.smack.** { *;}

-dontwarn org.jivesoftware.smackx.**
-keep class org.jivesoftware.smackx.** { *;}

-dontwarn com.amap.api.services.**
-keep class com.amap.api.services.** { *;}

-dontwarn com.amap.api.**
-keep class com.amap.api.** { *;}

-dontwarn com.amap.api.location.**
-keep class com.amap.api.location.** { *;}

-dontwarn net.sqlcipher.**
-keep class net.sqlcipher.** { *;}

-dontwarn com.google.common.**
-keep class com.google.common.** { *;}

-dontwarn org.apach.commons.codec.**
-keep class org.apach.commons.codec.** { *;}

-dontwarn com.github.mikephil.charting.**
-keep class com.github.mikephil.charting.** { *;}


#3D 地图 V5.0.0之前：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}

#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#搜索
-keep   class com.amap.api.services.**{*;}

#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

#友盟分享start
-dontusemixedcaseclassnames
    -dontshrink
    -dontoptimize
    -dontwarn com.google.android.maps.**
    -dontwarn android.webkit.WebView
    -dontwarn com.umeng.**
    -dontwarn com.tencent.weibo.sdk.**
    -dontwarn com.facebook.**
    -keep public class javax.**
    -keep public class android.webkit.**
    -dontwarn android.support.v4.**
    -keep enum com.facebook.**
    -keepattributes Exceptions,InnerClasses,Signature
    -keepattributes *Annotation*
    -keepattributes SourceFile,LineNumberTable

    -keep public interface com.facebook.**
    -keep public interface com.tencent.**
    -keep public interface com.umeng.socialize.**
    -keep public interface com.umeng.socialize.sensor.**
    -keep public interface com.umeng.scrshot.**
    -keep class com.android.dingtalk.share.ddsharemodule.** { *; }
    -keep public class com.umeng.socialize.* {*;}


    -keep class com.facebook.**
    -keep class com.facebook.** { *; }
    -keep class com.umeng.scrshot.**
    -keep public class com.tencent.** {*;}
    -keep class com.umeng.socialize.sensor.**
    -keep class com.umeng.socialize.handler.**
    -keep class com.umeng.socialize.handler.*
    -keep class com.umeng.weixin.handler.**
    -keep class com.umeng.weixin.handler.*
    -keep class com.umeng.qq.handler.**
    -keep class com.umeng.qq.handler.*
    -keep class UMMoreHandler{*;}
    -keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
    -keep class com.tencent.mm.sdk.modelmsg.** implements   com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
    -keep class im.yixin.sdk.api.YXMessage {*;}
    -keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
    -keep class com.tencent.mm.sdk.** {
     *;
    }
    -keep class com.tencent.mm.opensdk.** {
   *;
    }
    -dontwarn twitter4j.**
    -keep class twitter4j.** { *; }

    -keep class com.tencent.** {*;}
    -dontwarn com.tencent.**
    -keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
    }
    -keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
        }
    -keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    }

    -keep class com.tencent.open.TDialog$*
    -keep class com.tencent.open.TDialog$* {*;}
    -keep class com.tencent.open.PKDialog
    -keep class com.tencent.open.PKDialog {*;}
    -keep class com.tencent.open.PKDialog$*
    -keep class com.tencent.open.PKDialog$* {*;}

    -keep class com.sina.** {*;}
    -dontwarn com.sina.**
    -keep class  com.alipay.share.sdk.** {
       *;
    }
    -keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
    }

    -keep class com.linkedin.** { *; }
    -keepattributes Signature
#友盟分享end
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.** {*;}
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.tencent.mm.sdk.** {*;}

-keep class com.cmri.universalapp.family.member.model.** {*;}
-keep class com.cmri.universalapp.family.group.model.** {*;}
-keep class com.cmri.universalapp.family.charge.model.** {*;}
-keep class com.cmri.universalapp.family.motivation.model.** {*;}
-keep class com.cmri.universalapp.im.model.littlec.** {*;}
-keep class com.cmri.universalapp.familyalbum.imageaction.bean.** {*;}
-keep class com.cmri.universalapp.familyalbum.bean.** {*;}
-keep class com.cmri.universalapp.im.FeedBack.** {*;}
-keep class com.cmri.universalapp.im.model.** {*;}

-keep class com.cmri.universalapp.push.model.** {*;}
-keep class com.cmri.universalapp.device.ability.home.model.** {*;}
-keep class com.cmri.universalapp.device.gateway.device.model.** {*;}
-keep class com.cmri.universalapp.device.gateway.device.view.devicedetail.**{*;}
-keep class com.cmri.universalapp.device.gateway.devicehistory.model.** {*;}
-keep class com.cmri.universalapp.device.gateway.gateway.model.** {*;}
-keep class com.cmri.universalapp.device.gateway.wifisetting.model.** {*;}
-keep class com.cmri.universalapp.device.push.model.** {*;}
-keep class com.cmri.universalapp.setting.model.** {*;}
-keep class * implements java.io.Serializable {*;}
#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# 打印映射文件，以后打包补丁需要使用
-printmapping mapping.txt

-keep class com.google.** {*;}
-keepclassmembers class com.google.** {*;}
-dontwarn com.google.**
#和目相关模块keep
-keep class com.arcsoft.** {*;}
-keep class com.cmcc.hemu.** {*;}

#voip
-dontwarn com.example.voiplibs.**
-keep class com.example.voiplibs.** {*;}
-dontwarn com.mobile.voip.sdk.**
-keep class com.mobile.voip.sdk.** {*;}
-dontwarn org.mediasdk.**
-keep class org.mediasdk.** {*;}

#webview上传文件
-keepclassmembers class * extends android.webkit.WebChromeClient{
   		public void openFileChooser(...);
}

-dontwarn org.bouncycastle.**
-keep class  org.bouncycastle.** {*;}
-dontwarn com.sun.crypto.provider.**
-keep class com.sun.crypto.provider.** {*;}

#react native
-dontwarn com.facebook.**
-keep class com.facebook.** {*;}
-dontwarn com.facebook.react.**
-keep class com.facebook.react.** {*;}
-dontwarn com.facebook.soloader.**
-keep class com.facebook.soloader.** {*;}
-dontwarn okio.**
-keep class okio.** {*;}
-dontwarn okhttp3.**
-keep class okhttp3.** {*;}
-keep class net.sunniwell.stbclient.** {*;}
-keep class com.cmri.universalapp.reactnative.** {*;}
-keep class com.iflytek.** {*;}
#UPnP
-dontwarn com.sun.tools.javadoc.**
-keep class com.sun.tools.javadoc.** {*;}
#pinyin4j
#-libraryjars libs/pinyin4j-2.5.0.jar
-dontwarn demo.**
-keep class demo.**{*;}
-dontwarn net.sourceforge.pinyin4j.**
-keep class net.sourceforge.pinyin4j.**{*;}
-keep class net.sourceforge.pinyin4j.format.**{*;}
-keep class net.sourceforge.pinyin4j.format.exception.**{*;}
#umeng analytics
-dontwarn com.umeng.analytics.**
-keep class com.umeng.analytics.**{*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.cmri.universalapp.R$*{
public static final int *;
}

# SmartCloudMobileSDK
-keep class com.jd.smart.jdlink.** {*;}
-keep class com.jd.smartcloudmobilesdk.** {*;}

#HAIER
-keep class com.haier.uhome.** {*;}