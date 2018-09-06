package com.cmri.universalapp.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alipay.euler.andfix.patch.PatchManager;
import com.cmri.universalapp.BuildConfig;
import com.cmri.universalapp.R;
import com.cmri.universalapp.base.http2.HttpConstant;
import com.cmri.universalapp.base.http2.HttpController;
import com.cmri.universalapp.base.http2extension.ErrorMap;
import com.cmri.universalapp.contact.common.CommonConstants;
import com.cmri.universalapp.device.base.GatewayMsgManager;
import com.cmri.universalapp.device.gateway.gatewayinterface.GatewayModuleInterface;
import com.cmri.universalapp.family.MemberModuleImpl;
import com.cmri.universalapp.family.home.NewMsgManager;
import com.cmri.universalapp.family.home.PopPushManager;
import com.cmri.universalapp.family.member.MemberModuleInterface;
import com.cmri.universalapp.family.member.PushHandler;
import com.cmri.universalapp.family.member.view.inviteprecess.HasInviteActivity;
import com.cmri.universalapp.family.member.view.pushwarn.PushNotifyActivity;
import com.cmri.universalapp.family.push.FamilyEventSubscriber;
import com.cmri.universalapp.familyalbum.FamilyAlbumModuleInterface;
import com.cmri.universalapp.familyalbum.impl.FamilyAlbumModuleImpl;
import com.cmri.universalapp.gatewaymodule.impl.GatewayModuleImpl;
import com.cmri.universalapp.im.IMuserManager;
import com.cmri.universalapp.im.manager.IMuserManagerImpl;
import com.cmri.universalapp.index.IndexModuleImpl;
import com.cmri.universalapp.index.domain.PlatformInformationManager;
import com.cmri.universalapp.index.task.IPlatformInformationDataSource;
import com.cmri.universalapp.index.task.PlatformInformationDataSource;
import com.cmri.universalapp.indexinterface.IndexModuleInterface;
import com.cmri.universalapp.login.action.ActionManager;
import com.cmri.universalapp.login.action.ILoginActionManager;
import com.cmri.universalapp.login.activation.ActivationManager;
import com.cmri.universalapp.login.activity.LoginActivity;
import com.cmri.universalapp.login.activity.PasswordLoginActivity;
import com.cmri.universalapp.login.activity.SplashActivity;
import com.cmri.universalapp.login.activity.WelcomeActivity;
import com.cmri.universalapp.login.model.PersonalInfo;
import com.cmri.universalapp.mainmodule.MainModuleInterface;
import com.cmri.universalapp.mainmodule.impl.MainModuleImpl;
import com.cmri.universalapp.news.NewsModuleInterface;
import com.cmri.universalapp.news.domain.NewsManager;
import com.cmri.universalapp.news.modle.datasource.INewsDataSource;
import com.cmri.universalapp.news.modle.datasource.NewsCacheDataSource;
import com.cmri.universalapp.news.modle.datasource.NewsRemoteDataSource;
import com.cmri.universalapp.news.newsmodule.impl.NewsModuleImpl;
import com.cmri.universalapp.push.PushService;
import com.cmri.universalapp.reactnative.ReactNativeContainerActivity;
import com.cmri.universalapp.reactnative.ReactNativeContainerHost;
import com.cmri.universalapp.reactnative.ReactNativeCustomPackage;
import com.cmri.universalapp.resourcestore.CommonResource;
import com.cmri.universalapp.setting.CloseActivity;
import com.cmri.universalapp.setting.SettingModuleInterface;
import com.cmri.universalapp.setting.util.SettingModuleInterfaceImpl;
import com.cmri.universalapp.smarthome.SmartHomeModuleInterface;
import com.cmri.universalapp.smarthome.devicelist.domain.SmartHomeMsgManager;
import com.cmri.universalapp.smarthome.hemu.HeMuMsgManager;
import com.cmri.universalapp.smarthome.impl.SmartHomeModuleImpl;
import com.cmri.universalapp.speedup.SpeedUpManagerInterface;
import com.cmri.universalapp.speedup.speedupimpl.SpeedUpManagerImpl;
import com.cmri.universalapp.util.CommonUtil;
import com.cmri.universalapp.util.FileUtil;
import com.cmri.universalapp.util.ImageUtil;
import com.cmri.universalapp.util.MyLogger;
import com.cmri.universalapp.util.ToastUtils;
import com.cmri.universalapp.voipinterface.IVoipManager;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.littlec.conference.manager.IVoipManagerImpl;
import com.littlec.conference.talk.activity.CallActivity;
import com.littlec.conference.talk.activity.ConferenceVideoActivity;
import com.rnfs.RNFSPackage;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import cmcc.ueprob.agent.ReportPolicy;
import cmcc.ueprob.agent.UEProbAgent;
import cmcc.ueprob.agent.background.AppBgServiceManager;

/**
 * main application.
 */
public class UniversalApp extends Application implements ReactApplication {
    public static final boolean DEBUG = Boolean.parseBoolean("true");
    //  SP  start
    public static final String SP_TAG = "UniAppSp";
    public static final String SP_TAG_PASS_ID = "passId";
    public static final String SP_TAG_SESSION_ID = "sessionId";
    public static final String SP_TAG_TEL = "tel";
    public static final String SP_TAG_PERSONAL_INFOR = "PersonalInfo";
    public static final String SP_TAG_PERSONAL_PHONE_NUM = "phoneNo";
    public static final String SP_TAG_MSG_NOTIFY_VIBRATE = "msgVibrateFlag";
    public static final String SP_TAG_MSG_NOTIFY_RINGTONE = "msgRingtoneFlag";
    public static final String SP_TAG_MSG_NOTIFY_MAIN_SW = "msgMainSwFlag";
    public static final String SP_TAG_SYS_MSG_NOTIFICATION_SW = "sysMsgNotifySwFlag";
    public static final String SP_TAG_HAS_NEW_FAMILY_PUSH_MSG = "hasNewFamilyPushMsgFlag";
    public static final String SP_TAG_SUGGEST_CONTACT = "suggestContact";
    public static final String SP_TAG_CITY_LIST = "cityList";
    public static final String SP_TAG_CITY_PROCODE = "city_procode";
    public static final String SP_TAG_NEW_NOTICE = "new_notice";
    /**
     * use {@link #SP_TAG_HAS_NEW_FAMILY_PUSH_MSG}
     */
    public static final String SP_TAG_HAS_NEW_FAMILY_PUSH_MSG_OLD = "hasNewFamilyPushMsg";
    //    public static final String SP_TAG_HAS_NEW_DEVICE_PUSH_MSG = "hasNewDevicePushMsg";
    public static final String IS_MANAGER = "isManager"; //是否是管理员
    public static final String SP_SHOULD_SET_FAMILY_MEMBER_MASK_SHOW = "shouldSetFamilyMemberMaskShow";
    public static final String SP_SIGNED = "signed_today";
    public static final String SP_GUIDANCE_ONE_ISFIRST = "isfirst";
    public static final String SP_GUIDANCE_TWO_ISFIRST = "isfirst";
    public static final String SP_PERSONINFOGUIDANCE_ISSHOW = "personInfoGuidanceIsShow";
    public static final String SP_GUIDANCE_FOUR_ISFIRST = "isfirst";
    public static final String SP_SHOW_HE_PASS_NOTICE = "sp_show_he_pass_notice";
    public static final String SP_SET_STATIC_PASSWORD = "sp_set_static_password";
    /**
     * 统一认证APPID APPKEY
     */
    public static String APP_ID = BuildConfig.APPID;
    public static String APP_KEY = BuildConfig.APPKEY;
    public static String TRACE_AND_NEWS = "AndNews";
    private static UniversalApp universalApp;
    private static Context context;
    private static String dataPath;
    private static String mVoicePath;
    private static String mPhotoPath;
    private static String mTemptPhotoFileDir;

    static {
        CommonConstants.XX_VOIP_SERVER = BuildConfig.XIAOXI_VOIP_SERVER;
        CommonConstants.HTTP_APP_SERVER = BuildConfig.HTTP_SERVER_DEV;
        CommonConstants.XIAOXI_APPKEY_TOKEN = BuildConfig.XIAOXI_APPKEY_TOKEN;

        AppConstant.xiaoxiAppKey = BuildConfig.XIAOXI_APPKEY;
        AppConstant.APP_ID = BuildConfig.APPID;
        AppConstant.APP_KEY = BuildConfig.APPKEY;
        AppConstant.ENVIRONMENT_ID = BuildConfig.ENVIRONMENTID;
        AppConstant.SOURCE_ID = BuildConfig.SOURCEID;

        HttpConstant.VERSION = BuildConfig.VERSION;
        HttpConstant.HTTPPROTOCOL = BuildConfig.HTTPPROTOCOL;
        HttpConstant.HTTPSPROTOCOL = BuildConfig.HTTPSPROTOCOL;
        HttpConstant.SERVER = BuildConfig.SERVER;
        HttpConstant.PORT_DEFAULT = BuildConfig.PORT_DEFAULT;
        HttpConstant.PORT_PLATFORM_WEBSOCKET = BuildConfig.PORT_PLATFORM_WEBSOCKET;
        HttpConstant.SERVER_GATEWAY = BuildConfig.SERVER_GATEWAY;
        HttpConstant.PORT_GATEWAY = BuildConfig.PORT_GATEWAY;
        HttpConstant.CERTIFICATE = BuildConfig.CERTIFICATE;
        HttpConstant.CERTIFICATE_KEY = BuildConfig.CERTIFICATE_KEY;

        HttpConstant.PORT_USER_PROTOCLOL = BuildConfig.PORT_USER_PROTOCLOL;
        HttpConstant.SERVER_CUSTOMER_SERVICE = BuildConfig.SERVER_CUSTOMER_SERVICE;
        HttpConstant.PORT_SERVER_CUSTOMER_SERVICE = BuildConfig.PORT_CUSTOMER_SERVICE;
        HttpConstant.ORDER_SERVICE_URL = BuildConfig.ORDER_SERVICE_URL;

        AppConstant.PEOPER_POWER_SERVER = BuildConfig.PEOPER_POWER_SERVER;
        AppConstant.PEOPER_POWER_PORT = BuildConfig.PEOPER_POWER_PORT;
        AppConstant.PEOPLE_POWER_STATIC_PREFIX = BuildConfig.PEOPLE_POWER_STATIC_PREFIX;

        AppConstant.GREY_FLAG = BuildConfig.GREY_FLAG;
        AppConstant.DEBUG = BuildConfig.DEBUG;

        AppConstant.BUILD_TYPE = BuildConfig.BUILD_TYPE;

        AppConstant.JD_APP_KEY = BuildConfig.JD_APP_KEY;
        AppConstant.JD_REDIRECT_URI = BuildConfig.JD_REDIRECT_URI;

        AppConstant.HEMU_URL = BuildConfig.HEMU_URL;
        AppConstant.HEMU_UDP_PORT = BuildConfig.HEMU_UDP_PORT;
    }

    private static MyLogger mLogger = MyLogger.getLogger(UniversalApp.class.getSimpleName());

    public MODULE currentmodule = MODULE.UNSPECIFIED;
    public SharedPreferences sp;
    private boolean pictureIsUploading = false;
    /**
     * 手机imei号
     */
    private String imei = "";
    /**
     * 平台维护的sessionId
     */
    private String sessionId = "";
    /**
     * 统一认证返回的用户唯一ID
     */
    private String passId = "";
    //  SP  end
    /**
     * 当前App版本号
     */
    private String version;
    /**
     * 当前用户手机号
     */
    private String tel = "";
    /**
     * APP最新版本下载地址，登录成功之后平台返回
     */
    private String url;
    /**
     * hotfix热补丁框架manager
     */
    private PatchManager mPatchManager;
    private Map<String, ReactNativeHost> reactNativeHostMap;
    private String currentReactNativeUrl;
    private List<ReactPackage> reactPackages;

    /**
     * 单例
     *
     * @return
     */
    public static UniversalApp getInstance() {
        return universalApp;
    }

    public static Context getContext() {
        return context;
    }

    public static String getDataPath() {
        return dataPath;
    }

    public static void setDataPath(String datePath) {
        dataPath = datePath;
    }

    public static String getVoicePath() {
        return mVoicePath;
    }

    public static void setVoicePath(String voicePath) {
        mVoicePath = voicePath;
    }

    public static String getPhotoPath() {
        return mPhotoPath;
    }

    public static void setPhotoPath(String mPhotoPath) {
        UniversalApp.mPhotoPath = mPhotoPath;
    }

    public static String getTemptPhotoFileDir() {
        return mTemptPhotoFileDir;
    }

    public static void setTemptPhotoFileDir(String mPhotoPath) {
        UniversalApp.mTemptPhotoFileDir = mPhotoPath;
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public MODULE getCurrentmodule() {
        return currentmodule;
    }

    public void setCurrentmodule(MODULE currentmodule) {
        this.currentmodule = currentmodule;
        mLogger.d("current module: " + currentmodule);
    }

    @Override
    public void onCreate() {
        CommonConstants.XX_VOIP_SERVER = BuildConfig.XIAOXI_VOIP_SERVER;
        CommonConstants.HTTP_APP_SERVER = BuildConfig.HTTP_SERVER_DEV;
        CommonConstants.XIAOXI_APPKEY_TOKEN = BuildConfig.XIAOXI_APPKEY_TOKEN;

        AppConstant.xiaoxiAppKey = BuildConfig.XIAOXI_APPKEY;
        AppConstant.APP_ID = BuildConfig.APPID;
        AppConstant.APP_KEY = BuildConfig.APPKEY;
        AppConstant.ENVIRONMENT_ID = BuildConfig.ENVIRONMENTID;
        AppConstant.SOURCE_ID = BuildConfig.SOURCEID;

        HttpConstant.VERSION = BuildConfig.VERSION;
        HttpConstant.HTTPPROTOCOL = BuildConfig.HTTPPROTOCOL;
        HttpConstant.HTTPSPROTOCOL = BuildConfig.HTTPSPROTOCOL;
        HttpConstant.SERVER = BuildConfig.SERVER;
        HttpConstant.PORT_DEFAULT = BuildConfig.PORT_DEFAULT;
        HttpConstant.PORT_PLATFORM_WEBSOCKET = BuildConfig.PORT_PLATFORM_WEBSOCKET;
        HttpConstant.SERVER_GATEWAY = BuildConfig.SERVER_GATEWAY;
        HttpConstant.PORT_GATEWAY = BuildConfig.PORT_GATEWAY;
        HttpConstant.CERTIFICATE = BuildConfig.CERTIFICATE;
        HttpConstant.CERTIFICATE_KEY = BuildConfig.CERTIFICATE_KEY;

        HttpConstant.PORT_USER_PROTOCLOL = BuildConfig.PORT_USER_PROTOCLOL;
        HttpConstant.SERVER_CUSTOMER_SERVICE = BuildConfig.SERVER_CUSTOMER_SERVICE;
        HttpConstant.PORT_SERVER_CUSTOMER_SERVICE = BuildConfig.PORT_CUSTOMER_SERVICE;
        HttpConstant.ORDER_SERVICE_URL = BuildConfig.ORDER_SERVICE_URL;

        AppConstant.PEOPER_POWER_SERVER = BuildConfig.PEOPER_POWER_SERVER;
        AppConstant.PEOPER_POWER_PORT = BuildConfig.PEOPER_POWER_PORT;
        AppConstant.PEOPLE_POWER_STATIC_PREFIX = BuildConfig.PEOPLE_POWER_STATIC_PREFIX;
        
        AppConstant.GREY_FLAG = BuildConfig.GREY_FLAG;
        AppConstant.DEBUG = BuildConfig.DEBUG;

        AppConstant.BUILD_TYPE = BuildConfig.BUILD_TYPE;


        AppConstant.JD_APP_KEY = BuildConfig.JD_APP_KEY;
        AppConstant.JD_REDIRECT_URI = BuildConfig.JD_REDIRECT_URI;

        CommonResource.getInstance().setContext(getApplicationContext());
        mLogger.d("onCreate enter." + this);
        super.onCreate();
        //多进程如独立的service会重新调用onCreate，无需做主程序相关初始化
        String getProcessName = CommonUtil.getCurProcessName(this);
        if (getProcessName != null && getProcessName.contains("com.cmcc.dhsso.service.SsoService")) {
            mLogger.d("onCreate enter.SsoService return" + this);
            return;
        }
        if (BuildConfig.UNIT_TEST_FLAG == 0 && !isAppUseCorrectSign()) {
            System.exit(0);
        }
        HttpController.init(this);

        MainModuleInterface.init(new MainModuleImpl());
        GatewayModuleInterface.init(new GatewayModuleImpl(this));
        SmartHomeModuleInterface.init(new SmartHomeModuleImpl(this));
        ILoginActionManager.init(new ActionManager());
        SettingModuleInterface.init(new SettingModuleInterfaceImpl());
        NewsModuleInterface.init(new NewsModuleImpl(getApplicationContext()));
        SpeedUpManagerInterface.init(new SpeedUpManagerImpl(this));
        IPlatformInformationDataSource piDataSource = new PlatformInformationDataSource(HttpController.getInstance(), EventBus.getDefault());
        PlatformInformationManager.init(PersonalInfo.getInstance(), piDataSource, EventBus.getDefault());
        IndexModuleInterface.init(new IndexModuleImpl());
        FamilyAlbumModuleInterface.init(new FamilyAlbumModuleImpl());
        initErrorMap();
        NewMsgManager.init();
        PopPushManager.init();

        sp = getSharedPreferences(SP_TAG, Context.MODE_PRIVATE);

        context = getApplicationContext();

        ImageUtil.initImageLoader(this, "com.cmri.universalapp/imageloader");
//        LeakCanary.install(this);
        ToastUtils.init(this);
        ActivationManager.init(this);
        INewsDataSource remoteDataSource = new NewsRemoteDataSource(HttpController.getInstance(), EventBus.getDefault());
        INewsDataSource cacheDataSource = new NewsCacheDataSource(EventBus.getDefault(), context.getApplicationContext());
        NewsManager.init(remoteDataSource, cacheDataSource, EventBus.getDefault());

        universalApp = this;
//        initHotFix();
        initDefaultConfig();

        // if(BuildConfig.UNIT_TEST_FLAG == 0) {
        IVoipManager.init(new IVoipManagerImpl());
        IVoipManager.getInstance().initVoip(this);
        // }

        initDataDir();

        //if(BuildConfig.UNIT_TEST_FLAG == 0) {
        IMuserManager.init(new IMuserManagerImpl(UniversalApp.getContext()));
        IMuserManager.getInstance().initIMWithAppKey(this, BuildConfig.XIAOXI_APPKEY);
        IMuserManager.getInstance().changeDBPassword(" ");
        // }

        if (BuildConfig.UNIT_TEST_FLAG == 1) {
            ParserConfig.getGlobalInstance().setAsmEnable(false);
        }

        ActivityLifecycleListener callback = new ActivityLifecycleListener();
        registerActivityLifecycleCallbacks(callback);
        registerComponentCallbacks(callback);

        initPersonalInfo();

        initProb();

        initFamilyDomain();

        new Handler().postDelayed(new Runnable() {
            public void run() {
//                //MessageManager.getService().getDraftMessage(0l);
                CrashReport.initCrashReport(getApplicationContext(), AppConstant.BUGLY_APP_ID, true);
            }
        }, 1000);

        if (BuildConfig.UNIT_TEST_FLAG == 0) {
            SmartHomeMsgManager.init(getApplicationContext());
            GatewayMsgManager.init(getApplicationContext());
            HeMuMsgManager.init(getApplicationContext());
            initYouMeng();
        }
//        GreenDaoManager.getInstance();
        mLogger.d("onCreate end.");
    }

    private void initYouMeng() {
        UMShareAPI.get(this);
//        Config.DEBUG = true;
        PlatformConfig.setWeixin("wxbebdb38c33abf805", "db701e711c75f9fb2883905d611d305f");
        PlatformConfig.setQQZone("1106128760", " bhLI8RUrdBEjFNO6");

        MobclickAgent.startWithConfigure(
            new MobclickAgent.UMAnalyticsConfig(this, "591aa74fc62dca6e980008b3", "Umeng",
                MobclickAgent.EScenarioType.E_UM_NORMAL));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        SmartHomeModuleInterface.getInstance().onTerminate();
        super.onTerminate();
        if (BuildConfig.UNIT_TEST_FLAG == 0) {
            IVoipManager.getInstance().destroyVoip();
        }
    }

    /**
     * 确保在所有请求发出前初始化
     */
    private void initErrorMap() {
        String[] code = getResources().getStringArray(R.array.error_code);
        String[] message = getResources().getStringArray(R.array.error_message);
        ErrorMap.init(code, message);
    }

    private void initFamilyDomain() {
        String[] whiteList = new String[]{
            SplashActivity.class.getSimpleName(),
            WelcomeActivity.class.getSimpleName(),
            LoginActivity.class.getSimpleName(),
            HasInviteActivity.class.getSimpleName(),
            PushNotifyActivity.class.getSimpleName(), // 正在处理
            CloseActivity.class.getSimpleName(),
            CallActivity.class.getSimpleName(),
            ConferenceVideoActivity.class.getSimpleName(),
            PasswordLoginActivity.class.getSimpleName(),
        };
        PushHandler pushHandler = PushHandler.init(UniversalApp.this, whiteList);
        MemberModuleInterface.init(new MemberModuleImpl(EventBus.getDefault(), HttpController.getInstance(), IMuserManager.getInstance(), PersonalInfo.getInstance(), pushHandler));
        FamilyEventSubscriber.init(EventBus.getDefault());
    }

    /**
     * 获取imei号
     *
     * @return
     */
    public String getImei() {
        // 获取IMEI号
        if (TextUtils.isEmpty(imei)) {
            TelephonyManager tm = (TelephonyManager) this
                .getSystemService(TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        }
        return imei;
    }

    private void initPersonalInfo() {
        String processName = getProcessName(this, android.os.Process.myPid());
        mLogger.d("onCreate " + processName);
        if (getPackageName().equals(processName)) {
            if (PersonalInfo.getInstance().getPassId() == null) {
                ActionManager.getInstance().getPersonalInfoLocal();
                if (PersonalInfo.getInstance().getPassId() != null) {
                    PersonalInfo.getInstance().setLogin(true);
                }
                // 每次启动应用都由Session控制进行Login
                ActivationManager.getInstance().appStart();
            }
        }
    }

    /**
     * 初始化热补丁框架
     */
    private void initDataDir() {
        String rootDir = FileUtil.getDataDir(this);
        this.setDataPath(rootDir + "/UniApp");
        this.setVoicePath(getDataPath() + "/voice");
        this.setPhotoPath(getDataPath() + "/photo");
        this.setTemptPhotoFileDir(getDataPath() + "/temptPhoto");
    }

    /**
     * 初始化热补丁框架
     */
    private void initHotFix() {
        // initialize
        mPatchManager = new PatchManager(this);
        mPatchManager.init("1.0");
        mLogger.d("initHotFix --> HotFix inited.");

        // load patch
        mPatchManager.loadPatch();
        mLogger.d("initHotFix --> patch loaded.");

        // add patch at runtime
        try {
            // .apatch file path
            String root = FileUtil.getDataDir(this) + File.separator + AppConstant.PATCH_DIR;
            File rootFile = new File(root);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }

            String patchFileString = root + AppConstant.PATCH_NAME;
            File patchFile = new File(patchFileString);

            if (patchFile.exists()) {
                mLogger.d("initHotFix --> patch:" + patchFileString + " added.");
                mPatchManager.addPatch(patchFileString);
                patchFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDefaultConfig() {

    }

    /**
     * 初启动推送service
     */
    public void initPushService() {
        mLogger.d("push service -- > initPushService");
        Intent intent = new Intent(this, PushService.class);
        startService(intent);
    }

    /**
     * 统一认证返回的passId标识用户ID
     */
    public String getPassId() {
        if (TextUtils.isEmpty(passId)) {
            this.passId = this.getSharedPreferences(SP_TAG,
                Context.MODE_PRIVATE).getString(SP_TAG_PASS_ID, "");
        }
        return passId;
    }

    public void setPassId(String passId) {
        if (!TextUtils.isEmpty(passId)) {
            this.passId = passId;
            this.getSharedPreferences(SP_TAG, Context.MODE_PRIVATE).edit()
                .putString(SP_TAG_PASS_ID, passId).commit();
        }
    }

    /**
     *
     */
    public String getSessionId() {
        return CommonResource.getInstance().getSessionId();
    }

    public void setSessionId(String sessionid) {
        CommonResource.getInstance().setSessionId(sessionid);
    }

    /**
     * 是否是管理员
     */
    public boolean isManager() {

        return this.getSharedPreferences(SP_TAG,
            Context.MODE_PRIVATE).getBoolean(IS_MANAGER, false);
    }

    public void setManager(boolean isManager) {
        this.getSharedPreferences(SP_TAG, Context.MODE_PRIVATE).edit()
            .putBoolean(IS_MANAGER, isManager).commit();
    }

    /**
     * wifi是否已连接
     *
     * @return
     */
    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    /**
     * 获取ssid
     *
     * @return
     */
    public String getSsid() {
        WifiInfo wifiInfo = ((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Interface to accetp a configuration within JSON
     */
    private void initProb() {
        mLogger.i("UEProbActivity initProb");
        try {
            JSONObject config = new JSONObject();
            String serverApi = "http://" + BuildConfig.UE_PROB_SERVER + "/TRACEProbeService/accept";
            config.put("service_api", serverApi);
            //config.put("service_api", "http://218.205.115.245:18088/TRACEProbeService/accept");
            config.put("proxy_addr", null);
            config.put("proxy_port ", null);
            config.put("session_timer ", 3000);
            config.put("flag_get_location ", false);
            config.put("upload_policy", ReportPolicy.ANYTIME);
            config.put("batch_policy", ReportPolicy.BATCH_AT_LAUNCH);

            UEProbAgent.InitialConfig(config);
            String phone = PersonalInfo.getInstance().getPhoneNo();
            if (phone != null && phone.length() > 0) {
                UEProbAgent.onUserID(UniversalApp.getContext(), phone);
            }
            mLogger.d("config :" + config);

        } catch (Exception e) {
            mLogger.d("UEProbActivity initProb Exception");
            e.printStackTrace();
        }
    }

    public boolean isPictureIsUploading() {
        return pictureIsUploading;
    }

    public void setPictureIsUploading(boolean pictureIsUploading) {
        this.pictureIsUploading = pictureIsUploading;
    }

    /*
     * TRUE: if sign file MD5 value is the same as original one & user can user it normally;
     * FALSE: if sign file MD5 value be changed & APP will exit;
     */
    private boolean isAppUseCorrectSign() {

        boolean bResult = false;

        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(
                BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];

            int code = sign.hashCode();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sign.toByteArray());
            byte[] digest = md.digest();

            String res = CommonUtil.toHexString(digest);

            mLogger.v("apk md5:" + res);
            mLogger.v("hashCode:" + code);
            //对比MD5值
            //Build config MD5为空表示DEBUG环境，不进行判断，直接为TRUE;
            //否则判断release sign MD5和当前APP 是否相等，不相等则退出；
            if (TextUtils.isEmpty(BuildConfig.APK_RELEASE_SIGN_MD5) || BuildConfig.APK_RELEASE_SIGN_MD5.equals(res)) {
                bResult = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return bResult;
        }
    }


    public boolean getShouldSetFamilyMemberMaskShow() {
        return sp.getBoolean(SP_SHOULD_SET_FAMILY_MEMBER_MASK_SHOW, true);
    }

    public void setShouldSetFamilyMemberMaskShow(boolean value) {
        sp.edit().putBoolean(SP_SHOULD_SET_FAMILY_MEMBER_MASK_SHOW, value);
    }

    public String getCurrentReactNativeUrl() {
        return currentReactNativeUrl;
    }

    public void setCurrentReactNativeUrl(String currentReactNativeUrl) {
        this.currentReactNativeUrl = currentReactNativeUrl;
    }

    @Override
    public ReactNativeHost getReactNativeHost() {
        if (reactNativeHostMap == null) {
            reactNativeHostMap = new HashMap<>();
        }

        String currentUrl = getCurrentReactNativeUrl();

        if (!reactNativeHostMap.containsKey(currentUrl)) {

            try {
                Uri wholeUri = Uri.parse(currentUrl);

                String wholeUrl = wholeUri.getQueryParameter("rnUrl");

                Uri uri = Uri.parse(wholeUrl);

                final String jsBundleName = getFileNameNoEx(uri.getLastPathSegment());

                // publish = true && not in checkPublish
                final Boolean isPublish = Boolean.parseBoolean(wholeUri.getQueryParameter("publish")) && ReactNativeContainerActivity.checkPublish(jsBundleName);

                final ReactNativeHost reactNativeHost = new ReactNativeContainerHost(this) {

                    @Override
                    protected boolean getUseDeveloperSupport() {
                        if (BuildConfig.RN_DEBUG) {
                            return !isPublish;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    protected List<ReactPackage> getPackages() {
                        if (reactPackages == null) {
                            reactPackages = Arrays.<ReactPackage>asList(
                                new MainReactPackage(),
                                new ReactNativeCustomPackage(),
                                new RNFSPackage() // <---------- add package
                            );
                        }
                        return reactPackages;
                    }

                    @Override
                    protected String getJSMainModuleName() {
                        return jsBundleName + ".bundle";
                    }

                    @Nullable
                    @Override
                    protected String getJSBundleFile() {
                        // 在发布版本，或isPublish版本中
                        if (!BuildConfig.RN_DEBUG || isPublish) {
                            // 如果本地有Bundle文件，优先读取本地Bundle
                            File file = context.getFilesDir();
                            String path = file.getAbsolutePath();
                            path = path + "/hotfix/" + jsBundleName + ".jsbundle";
                            File bundleFile = new File(path);
                            if (bundleFile.exists()) {
                                return path;
                            }
                        }
                        return null;
                    }

                    @Nullable
                    @Override
                    protected String getBundleAssetName() {
                        // 在发布版本，或isPublish版本中
                        if (!BuildConfig.RN_DEBUG || isPublish) {
                            // 返回Asset里面的jsBundle
                            return jsBundleName + ".jsbundle";
                        } else {
                            return "";
                        }
                    }
                };

                reactNativeHostMap.put(currentUrl, reactNativeHost);
            } catch (Exception e) {
                return null;
            }
        }
        return reactNativeHostMap.get(currentUrl);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN || level >= TRIM_MEMORY_MODERATE) {
            mLogger.i("start trace appBG service");
            AppBgServiceManager.startService(this.getApplicationContext());
        }
    }

    public enum MODULE {UNSPECIFIED, GATEWAY, DEVICE, FAMILY, FIND, CUSTOM, UNKNOWN}

}
