package com.cmri.universalapp.mainmodule.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cmcc.hemu.HeMu;
import com.cmri.universalapp.SmartMainActivity;
import com.cmri.universalapp.base.AppConstant;
import com.cmri.universalapp.base.UniversalApp;
import com.cmri.universalapp.family.home.NewMsgManager;
import com.cmri.universalapp.family.member.MemberModuleInterface;
import com.cmri.universalapp.family.member.domain.IFamilyAdminUseCase;
import com.cmri.universalapp.familyalbum.home.domain.FamilyAlbumUploadManager;
import com.cmri.universalapp.im.IMuserManager;
import com.cmri.universalapp.im.util.MessageUtil;
import com.cmri.universalapp.index.domain.IndexManager;
import com.cmri.universalapp.index.domain.IndexUseCase;
import com.cmri.universalapp.login.activation.ActivationManager;
import com.cmri.universalapp.login.model.PersonalInfo;
import com.cmri.universalapp.mainmodule.MainModuleInterface;
import com.cmri.universalapp.setting.SettingActivity;
import com.cmri.universalapp.smarthome.devicelist.domain.SmartHomeDeviceManager;
import com.cmri.universalapp.smarthome.hemu.video.CameraInfoManager;
import com.cmri.universalapp.voipinterface.IVoipManager;
import com.umeng.analytics.MobclickAgent;

import net.sunniwell.stbclient.HYUpnpManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wanzhuqing on 2017/2/20.
 */

public class MainModuleImpl extends MainModuleInterface {


/**
 *
 */
    public void executeFamilyTask(String taskId, String relatePassId) {
//        FamilyTaskManager taskManager = FamilyTaskManager.getInstance(EventBus.getDefault());
//        taskManager.execute(taskId, relatePassId);
    }

    @Override
    public int getPortraitBgByPassId(String passId) {
        return MessageUtil.getPortraitBgByPassId(passId);
    }

    @Override
    public void actionSetting(Activity activity, String token, String nick) {
        Intent intent = new Intent(activity, SettingActivity.class);
        intent.putExtra("TOKEN", token);
        intent.putExtra("NICK", nick);
        activity.startActivity(intent);
    }

    @Override
    public Context getApplicationContext() {
        return UniversalApp.getContext();
    }

    @Override
    public void setCurrentReactNativeUrl(String url) {
        UniversalApp.getInstance().setCurrentReactNativeUrl(url);
    }

    @Override
    public boolean isContainsMobaihe() {
        return HYUpnpManager.isContainsMobaihe(getApplicationContext());
    }

    @Override
    public boolean getNewMessageSp() {
        return NewMsgManager.getInstance().hasNewMessage();
    }

    @Override
    public void setNewMessageSp(boolean hasNewMsg) {
        NewMsgManager.getInstance().changeUnreadState(hasNewMsg);
    }

    @Override
    public Intent getSmartMainActivityIntent(Context context) {
        Intent mainIntent = new Intent(context, SmartMainActivity.class);
        return mainIntent;
    }

    @Override
    public void cleanLoginInfo() {
        try {
            FamilyAlbumUploadManager.getInstance().clearUploadData(false);

            PersonalInfo.getInstance().setLogin(false);
            PersonalInfo.getInstance().setPassId(null);
            PersonalInfo.getInstance().setToken(null);
            PersonalInfo.getInstance().setPhoneNo(null);
            PersonalInfo.getInstance().setGetPrivacy(false);
            PersonalInfo.getInstance().setFamilyUrl(null);
            PersonalInfo.getInstance().setFamilyScore(0);
            UniversalApp.getInstance().sp.edit().putString(UniversalApp.SP_TAG_PERSONAL_INFOR, null).commit();
            UniversalApp.getInstance().sp.edit().putString(AppConstant.SP_PHONE, null).commit();
            UniversalApp.getInstance().setSessionId("");
//        IFamilyTaskUseCase taskUseCase = FamilyTaskManager.getInstance(EventBus.getDefault());
//        taskUseCase.refreshIntimacy();
//        IFamilyAdminUseCase adminUseCase = FamilyAdminManager.getInstance(EventBus.getDefault(), FamilyMemberRemoteDataSource.getInstance(EventBus.getDefault()));
//        adminUseCase.refresh();
            MemberModuleInterface moduleInterface =MemberModuleInterface.getInstance();
            moduleInterface.setFamilyDataDirty();
            IndexUseCase indexUseCase = IndexManager.getInstance(EventBus.getDefault(), PersonalInfo.getInstance());
            indexUseCase.clear();
            moduleInterface.getPushHandler().clear();
            ActivationManager.getInstance().clear();

            IFamilyAdminUseCase familyAdminUseCase = MemberModuleInterface.getInstance().getAdminUseCase();
            familyAdminUseCase.clear();

            IMuserManager.getInstance().imDoLogOut();

            IVoipManager.getInstance().logoutVoip(UniversalApp.getInstance());
            IVoipManager.getInstance().accountRelease();

            //设备相关缓存数据清除
            SmartHomeDeviceManager.getInstance().clearCache();
            CameraInfoManager.getInstance().clear();
            if (CameraInfoManager.getInstance().isLoginFinished()) {
                HeMu.logout();
            }
            MobclickAgent.onProfileSignOff();
        } catch (Exception e) {
            //LOGGER.e(e.getMessage());
        }
    }

    @Override
    public void parseLink(Intent intent) {
        //AppLinkManager.getInstance().parseLink(intent);
    }


}