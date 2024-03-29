package com.mhy.wblibrary;


/**
 * @author mahongyin 2020-05-29 13:28
 */
public class WbSocial {


    /**
     * 在微博开发平台为应用申请的App Key
     */
    private static String APP_KY;
    /**
     * 在微博开放平台设置的授权回调页
     */
    private static String REDIRECT_URL;
    /**
     * 在微博开放平台为应用申请的高级权限
     */
    private static String SCOPE;

    public static String getAppKy() {
        return APP_KY;
    }

    public static String getRedirectUrl() {
        return REDIRECT_URL;
    }

    public static String getScope() {
        return SCOPE;
    }

    /**
     * @param redirectUrl 重定向URL 授权回调页
     * @param scope       在微博开放平台为应用申请的高级权限范围
     */
    public static void setWbApp(String appKey, String redirectUrl, String scope) {
        APP_KY = appKey;
        REDIRECT_URL = redirectUrl;
        SCOPE = scope;
    }

    public static void setWbApp(String appKey) {
        APP_KY = appKey;
        REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
        SCOPE = "all";
    }

    /**
     * @param redirectUrl 重定向 URL 授权回调页
     */
    public static void setWbApp(String appKey, String redirectUrl) {
        APP_KY = appKey;
        REDIRECT_URL = redirectUrl;
        SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";

    }

}
