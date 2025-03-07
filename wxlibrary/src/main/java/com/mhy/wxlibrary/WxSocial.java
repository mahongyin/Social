package com.mhy.wxlibrary;

import android.content.Context;

import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory;
import com.tencent.mm.opensdk.diffdev.IDiffDevOAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author mahongyin 2020-05-28 11:21 @CopyRight mhy.work@qq.com
 * WxSocial.setWeixinId("xxxxxxxx");
 */
public class WxSocial {
    private IWXAPI mWXApi;
    // 微信扫码授权
    private IDiffDevOAuth wxOAuth;
    private String wxAppId;
    private static volatile WxSocial singleton = null;
    public static WxSocial getInstance() {
        if (singleton == null) {
            synchronized(WxSocial.class) {
                if (singleton == null) {
                    singleton = new WxSocial();
                }
            }
        }
        return singleton;
    }
    public void init(Context context, String appId) {
        boolean verifySignature = true; //验证签名
        init(context, appId, verifySignature);
    }
    public void init(Context context, String appId, boolean verifySignature) {
        this.wxAppId = appId;
        mWXApi = WXAPIFactory.createWXAPI(context.getApplicationContext(), this.wxAppId, verifySignature);
        mWXApi.registerApp(this.wxAppId);
    }
    public IWXAPI getWXApi() {
        return mWXApi;
    }
    public IDiffDevOAuth getWxOAuth() {
        if (wxOAuth == null) {
            wxOAuth = DiffDevOAuthFactory.getDiffDevOAuth();
        }
        return wxOAuth;
    }
    public String getWxAppId() {
        return wxAppId;
    }
}
