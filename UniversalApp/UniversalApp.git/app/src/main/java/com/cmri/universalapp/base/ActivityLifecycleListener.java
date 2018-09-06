package com.cmri.universalapp.base;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import com.cmri.universalapp.family.member.MemberModuleInterface;
import com.cmri.universalapp.login.AppUpdateService;
import com.cmri.universalapp.login.activity.LoginActivity;
import com.cmri.universalapp.login.activity.SplashActivity;
import com.cmri.universalapp.login.activity.WelcomeActivity;
import com.cmri.universalapp.login.model.PersonalInfo;
import com.cmri.universalapp.setting.CloseActivity;
import com.cmri.universalapp.util.MyLogger;
import com.cmri.universalapp.util.TraceUtil;
import com.umeng.analytics.MobclickAgent;

import cmcc.ueprob.agent.UEProbAgent;

/**
 * Created by chf on 2016/7/7.
 */
public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private static MyLogger sMyLogger = MyLogger.getLogger(ActivityLifecycleListener.class.getSimpleName());
    /**
     * 这些界面跳过session检查。
     * 对与session check 来说，这些界面是在后台状态的。而其他界面是前台状态。
     */
    private static final String[] SKIP_VALIDATE = new String[]{
            SplashActivity.class.getSimpleName(),
            CloseActivity.class.getSimpleName(),
            WelcomeActivity.class.getSimpleName(),
            LoginActivity.class.getSimpleName(),
    };

    private static volatile int activityCount;
    private static boolean isInBackground = false;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
        if (activityCount == 1) {
//            checkVersion();
            sMyLogger.d("from back to foreground");
            refreshMemberList();
            applicationDidEnterForeground();
        }
//        if (showNotifyActive(activity)) {
//            ActivationManager.getInstance().active();
//        } else { // SKIP_VALIDATE 界面认为应用在后台。主要作用是与前台状态相对
//            ActivationManager.getInstance().background();
//        }
    }

    private void refreshMemberList() {
        if (PersonalInfo.getInstance().isLogin()) {

            PersonalInfo personalInfo = PersonalInfo.getInstance();
            String familyId = personalInfo.getFamilyId();
            String passId = personalInfo.getPassId();

            if (!TextUtils.isEmpty(passId) && !TextUtils.isEmpty(familyId)) {
                sMyLogger.d("reload family member.");
                MemberModuleInterface.getInstance().refreshFamilyData();
            }

        }
    }

    /**
     * 标识是否应该认为是激活状态
     *
     * @param activity false
     * @return true, 处于前台。false处于后台。
     */
    private boolean showNotifyActive(Activity activity) {
        String name = activity.getClass().getSimpleName();
        for (String skip : SKIP_VALIDATE) {
            if (skip.equals(name)) {
                return false;
            }
        }

        return true;
    }

    private void checkVersion() {
        Intent intent = new Intent(UniversalApp.getContext(), AppUpdateService.class);
        UniversalApp.getContext().startService(intent);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isInBackground) {
            sMyLogger.d("app went to foreground");
            isInBackground = false;
        }
        UEProbAgent.onResume(activity);
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        UEProbAgent.onPause(activity);
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            // 没有应用在前台
//            ActivationManager.getInstance().background();
            applicationDidEnterBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            sMyLogger.d("app went to background");
            isInBackground = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }

    private void applicationDidEnterForeground(){
        TraceUtil.traceLocation();
        TraceUtil.traceApps();
    }

    private void applicationDidEnterBackground(){

    }

}
