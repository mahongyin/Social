package com.mhy.qqlibrary;

/**
 * @author mahongyin 2020-05-29 19:01 @CopyRight mhy.work@qq.com
 * description .
 */

public class QqSocial {

    private String appId;

    public String getAppId() {
        return appId;
    }

    private static QqSocial singleton;

    public static QqSocial getInstance() {
        if (singleton == null) {
            synchronized (QqSocial.class) {
                if (singleton == null) {
                    singleton = new QqSocial();
                }
            }
        }
        return singleton;
    }

    public void init(String Id) {
        appId = Id;
    }
}
