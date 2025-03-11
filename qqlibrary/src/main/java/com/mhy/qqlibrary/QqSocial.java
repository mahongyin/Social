package com.mhy.qqlibrary;

import android.app.Activity;

import com.mhy.qqlibrary.auth.QqAuth;
import com.mhy.qqlibrary.share.QqShare;
import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.ShareApi;

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

    public QqAuth getAuth(Activity activity, AuthApi.OnAuthListener listener) {
        return new QqAuth(activity, listener);
    }

    public QqShare getShare(Activity activity, ShareApi.OnShareListener listener) {
        return new QqShare(activity, listener);
    }
}
