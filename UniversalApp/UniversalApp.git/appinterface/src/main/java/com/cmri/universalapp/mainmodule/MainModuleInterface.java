package com.cmri.universalapp.mainmodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


/**
 * Created by wanzhuqing on 2017/2/20.
 */

public abstract class  MainModuleInterface {

     private static MainModuleInterface instance;

     public static MainModuleInterface getInstance(){
         return instance;
     }


    /**
     *
     */
     public static void init(MainModuleInterface impl){
          instance = impl;
     }
    /**
     *
     */
     public abstract void executeFamilyTask(String taskId, String relatePassId);
    /**
     *
     */
     public abstract int getPortraitBgByPassId(String passId);
    /**
     *
     */
     public abstract  void actionSetting(Activity activity, String token, String nick) ;
    /**
     *
     */
     public abstract Context getApplicationContext();
    /**
     *
     */
     public abstract void setCurrentReactNativeUrl(String url);
    /**
     *
     */
     public abstract boolean isContainsMobaihe();

    /**
     * 获取小红点
     *
     * @return
     */
    public abstract boolean getNewMessageSp();

    /**
     * 设置有新消息小红点
     *
     * @param hasNewMsg
     */
    public abstract void setNewMessageSp(boolean hasNewMsg);

    /**
     * 获取smartmainactivity  intent
     * @param context
     * @return
     */
    public abstract Intent getSmartMainActivityIntent(Context context);

    /**
     * cleanLoginInfo
     */
    public abstract void cleanLoginInfo();

    /**
     * parseLink from outside
     * @param intent
     */
    public abstract void parseLink(Intent intent);

}
