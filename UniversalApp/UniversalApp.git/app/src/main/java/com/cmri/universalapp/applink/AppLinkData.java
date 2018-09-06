package com.cmri.universalapp.applink;

import java.util.Map;

/**
 * Created by 15766_000 on 2017/5/11.
 */

public class AppLinkData {
    private String url;
    private Map<String, String> param;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }
}