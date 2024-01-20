package com.mhy.wxlibrary.auth;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.IntDef;

import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.WxSocial;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.SubscribeMiniProgramMsg;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 微信登陆
 */
public class WxAuth extends AuthApi {
    private IWXAPI mWXApi;

    /*
     * 执行登陆操作
     * @param act activity
     * @param l 回调监听
     */
    public WxAuth(Activity act, OnAuthListener l) {
        super(act, l);
        mAuthType = SocialType.WEIXIN_Auth;
        if (mWXApi == null) {
            mWXApi = WXAPIFactory.createWXAPI(mActivity.get(), getAppId(), true);
            mWXApi.registerApp(getAppId());
        }
    }

    @Override
    protected String getAppId() {
        return WxSocial.getWeixinId();
    }

    /*基本信息验证*/
    private boolean baseVerify() {
        if (TextUtils.isEmpty(getAppId())) {
            setErrorCallBack("appid为空");
            return true;
        }
        return false;
    }

    /**
     * 登陆认证
     */
    public void doAuth() {
        if (baseVerify()) {
            return;
        }
        //在这init?
        if (!mWXApi.isWXAppInstalled()) {
            setErrorCallBack("微信未安装");
            return;
        }
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat";
        //String.valueOf(System.currentTimeMillis());
        mWXApi.sendReq(req);
    }

    /**
     * 打开微信小程序
     *
     * @param miniAppId 小程序原始ID "gh_d43f693ca31f"
     * @param path      类似http的url方法来传递参数 "page/index?key1=xxx&key2=yyy";
     * @param type      WXMiniProgramTypeRelease 正式版
     *                  WXMiniProgramTypeTest 开发版
     *                  WXMiniProgramTypePreview 体验版
     *                  {@link WXLaunchMiniProgram.Req}
     */
    public void doOpenMiniApp(String miniAppId, String path, @MiniProgramType int type) {
        IWXAPI api = WXAPIFactory.createWXAPI(mActivity.get(), getAppId(), true);
        api.registerApp(getAppId());

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        // 填小程序原始id
        req.userName = miniAppId;
        //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.path = path;
        // 可选打开 开发版，体验版和正式版 /*WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE*/
        req.miniprogramType = type;
        api.sendReq(req);
    }

    /**
     * 订阅小程序消息
     *
     * @param miniAppId 小程序 appid
     */
    public void subMiniAppMsg(String miniAppId) {
        if (mWXApi.getWXAppSupportAPI() >= Build.SUBSCRIBE_MINI_PROGRAM_MSG_SUPPORTED_SDK_INT) {
            SubscribeMiniProgramMsg.Req req = new SubscribeMiniProgramMsg.Req();
            req.miniProgramAppId = miniAppId;
            boolean ret = mWXApi.sendReq(req);
            String message = String.format("sendReq ret : %s", ret);
            setCompleteCallBack(message);

        } else {
            setErrorCallBack("不支持小程序");
        }

    }

    /**
     * 订阅消息
     *
     * @param scene      1000
     *                   options.scene是1036，这个场景id表示app分享。
     *                   options.scene是1069，这个场景id表示从app打开。
     * @param templateId Jo-ywGDy0K9zGY87D2Cs2D51ExMoA1xSor7UxfIiLG8
     * @param reserved   abcAbc
     */
    public void subAppMsg(int scene, String templateId, String reserved) {
        if (mWXApi.getWXAppSupportAPI() >= Build.SUBSCRIBE_MESSAGE_SUPPORTED_SDK_INT) {
            SubscribeMessage.Req req = new SubscribeMessage.Req();
            req.scene = scene;
            req.templateID = templateId;
            req.reserved = reserved;

            boolean ret = mWXApi.sendReq(req);
            setCompleteCallBack("sendReq result = " + ret);

        } else {
            setErrorCallBack("不支持小程序");
        }

    }

    /**
     * 启动微信
     */
    private boolean launch() {
        return mWXApi.openWXApp();
    }

    /**
     * 注册
     */
    private void register() {
        mWXApi.registerApp(getAppId());
    }

    /**
     * 反注册
     */
    private void unRegister() {
        mWXApi.unregisterApp();
    }

    //限定值 小程序发布类型
    @IntDef(value = {WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE,
            WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST,
            WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW})
    @Retention(RetentionPolicy.SOURCE)//源码级别，注解只存在源码中
    private @interface MiniProgramType {
    }

}
