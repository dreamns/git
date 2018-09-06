package com.cmri.universalapp.applink;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cmri.universalapp.SmartMainActivity;
import com.cmri.universalapp.login.activity.SplashActivity;
import com.cmri.universalapp.mainmodule.MainModuleInterface;
import com.cmri.universalapp.resourcestore.CommonResource;
import com.cmri.universalapp.util.Base64;
import com.cmri.universalapp.util.MyLogger;

import java.util.List;

/**
 * Created by 15766_000 on 2017/5/10.
 */

public class WebEntranceActivity extends Activity {
    private static MyLogger sMyLogger = MyLogger.getLogger(WebEntranceActivity.class.getSimpleName());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLinkManager.getInstance().parseLink(getIntent());
        toSplash();
        finish();
    }

    private void toSplash() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void parseLink(){
        try {
            Uri data = getIntent().getData();
            String scheme = data.getScheme();
            String host = data.getHost();
            //data.getQueryParameter("param");
            //Pattern pattern = Pattern.compile("param.(.*?)");
            //pattern.matcher(test);
            String urlString = data.toString();
            String queryParameter64 = urlString.replace(AppLinkManager.APP_LINK_URL, "");
            sMyLogger.e(queryParameter64);
            String queryParameter = "";
            if (queryParameter64 != null && queryParameter64.length() > 0) {
                queryParameter = byteArrayToStr(Base64.decode(queryParameter64.getBytes(), Base64.DEFAULT));
            }
            sMyLogger.e(queryParameter);
            AppLinkData applinkData = JSON.parseObject(queryParameter, AppLinkData.class);
            if (applinkData != null) {
                handleLinks(applinkData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sMyLogger.e(e.getMessage());
        } finally {
            finish();
        }
    }

    private void handleLinks(AppLinkData data) {
        if (data == null) {
            return;
        }
        AppLinkManager.getInstance().setLinkInfo(data);
        if (SmartMainActivity.isAlive()) {
            sMyLogger.d("app link: in background");
            toSmartMain(data);
        } else {
            sMyLogger.d("app link: not in background");
            Intent i = new Intent(this, SplashActivity.class);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
    }


    /**
     * byteArrayToStr
     * @param byteArray
     * @return
     */
    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    private void toSmartMain(AppLinkData data) {
        Intent intent = MainModuleInterface.getInstance().getSmartMainActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AppLinkManager.HAS_APP_LINK, true);
        startActivity(intent);
        overridePendingTransition(universalapp.cmri.com.login.R.anim.enter_right_to_left, universalapp.cmri.com.login.R.anim.exit_right_to_left_less);
    }



    private static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCallable(Intent intent) {
        List<ResolveInfo> list = CommonResource.getInstance().getAppContext()
                .getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);



    }
}
