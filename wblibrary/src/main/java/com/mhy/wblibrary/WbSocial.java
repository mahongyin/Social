package com.mhy.wblibrary;


import android.app.Activity;

import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.ShareApi;
import com.mhy.wblibrary.auth.WbAuth;
import com.mhy.wblibrary.share.WbShare;

/**
 * @author mahongyin 2020-05-29 13:28
 */
public class WbSocial {

    /**
     * 在微博开发平台为应用申请的App Key
     */
    private String APP_KY;
    /**
     * 在微博开放平台设置的授权回调页
     */
    private String REDIRECT_URL;
    /**
     * 在微博开放平台为应用申请的高级权限
     */
    private String SCOPE;

    public String getAppKy() {
        return APP_KY;
    }

    public String getRedirectUrl() {
        return REDIRECT_URL;
    }

    public String getScope() {
        return SCOPE;
    }

    private static final class SingletonHolder {
        static final WbSocial singleton = new WbSocial();
    }

    public static WbSocial getInstance() {
        return SingletonHolder.singleton;
    }

    // 微博AuthInfo(yspcar, "1446396360", "https://api.weibo.com/oauth2/default.html", "all")
    public void init(String appKey) {
        APP_KY = appKey;
        REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";//"http://www.sina.com";
        SCOPE = "all";
//        SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";
    }

    /**
     * @param redirectUrl 重定向 URL 授权回调页
     */
    public void init(String appKey, String redirectUrl) {
        APP_KY = appKey;
        REDIRECT_URL = redirectUrl;
        SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";
    }

    /**
     * @param redirectUrl 重定向URL 授权回调页
     * @param scope       在微博开放平台为应用申请的高级权限范围
     */
    public void init(String appKey, String redirectUrl, String scope) {
        APP_KY = appKey;
        REDIRECT_URL = redirectUrl;
        SCOPE = scope;
    }

    public WbAuth getAuth(Activity activity, AuthApi.OnAuthListener listener) {
        return new WbAuth(activity, listener);
    }

    public WbShare getShare(Activity activity, ShareApi.OnShareListener listener) {
        return new WbShare(activity, listener);
    }
}
