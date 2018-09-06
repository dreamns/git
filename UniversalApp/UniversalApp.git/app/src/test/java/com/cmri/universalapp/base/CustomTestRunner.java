package com.cmri.universalapp.base;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;

/**
 * Created by Administrator on 2017/6/6.
 */

public class CustomTestRunner extends RobolectricTestRunner {
    private static final String BUILD_OUTPUT = "E:/Git/UniApp_Android/UniversalAppGit/app/build/intermediates/";
    public CustomTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String manifestPath = BUILD_OUTPUT + "manifests/full/cmccUniversalApp/ut_debug/AndroidManifest.xml";
        String resDir = BUILD_OUTPUT + "res/merged/debug";
        String assetsDir = BUILD_OUTPUT + "assets/debug";

//        AndroidManifest manifest = createAppManifest(Fs.fileFromPath(manifestPath),
//                Fs.fileFromPath(resDir),
//                Fs.fileFromPath(assetsDir),"com.uthing");
        AndroidManifest manifest = null;
        return manifest;
    }
}
