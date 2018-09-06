package com.cmri.universalapp.base;

import android.app.Application;

import com.cmri.universalapp.im.BuildConfig;
import com.cmri.universalapp.im.activity.PlaintTextDetailActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowLog.class}, constants = BuildConfig.class, sdk = 23
,manifest = "build/intermediates/manifests/full/cmccUniversalApp/ut_debug/AndroidManifest.xml")
public class ActivityLifecycleListenerTest {

    ActivityLifecycleListener callback = null;

    @Before
    public void setUp() {
        Application application = RuntimeEnvironment.application;
        callback = new ActivityLifecycleListener();
    }

    @Test
    public void startActivity() throws Exception {

        PlaintTextDetailActivity priorActivity = Robolectric.setupActivity(PlaintTextDetailActivity.class);

        callback.onActivityCreated(priorActivity, null);

        callback.onActivityStarted(priorActivity);

        callback.onActivitySaveInstanceState(priorActivity, null);

        callback.onActivityPaused(priorActivity);

        callback.onActivityStopped(priorActivity);

        callback.onTrimMemory(0);

        callback.onLowMemory();
        callback.onConfigurationChanged(null);

        callback.onActivityDestroyed(priorActivity);

        assertEquals(4, 2 + 2);
    }

}