package com.mhy.wxlibrary;

import android.app.Activity;
import android.content.Context;

import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.PayApi;
import com.mhy.socialcommon.ShareApi;
import com.mhy.wxlibrary.auth.WxAuth;
import com.mhy.wxlibrary.pay.WxPay;
import com.mhy.wxlibrary.share.WxShare;
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
    private IDiffDevOAuth diffDevOAuth;
    private String wxAppId;
    private static volatile WxSocial singleton = null;

    public static WxSocial getInstance() {
        if (singleton == null) {
            synchronized (WxSocial.class) {
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

    /**
     * 启动微信
     */
    public boolean launch() {
        return mWXApi.openWXApp();
    }

    /**
     * 反注册
     */
    public void unRegister() {
        mWXApi.unregisterApp();
    }

    /**
     * 扫码登录的
     */
    public IDiffDevOAuth getDiffDevOAuth() {
        if (diffDevOAuth == null) {
            diffDevOAuth = DiffDevOAuthFactory.getDiffDevOAuth();
        }
        return diffDevOAuth;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public WxAuth getAuth(Activity activity, AuthApi.OnAuthListener l) {
        return new WxAuth(activity, l);
    }

    public WxShare getShare(Activity activity, ShareApi.OnShareListener l) {
        return new WxShare(activity, l);
    }

    public WxPay getPay(Activity activity, PayApi.OnPayListener l) {
        return new WxPay(activity, l);
    }

}
