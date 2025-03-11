package com.mhy.wxlibrary.auth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;

import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.ShareUtil;
import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.WxSocial;
import com.mhy.wxlibrary.bean.WeiXin;
import com.mhy.wxlibrary.wxapi.BaseWXActivity;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.SubscribeMiniProgramMsg;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;


/**
 * 微信登陆
 *
 * @author mahongyin
 * OAuthListener 扫码登录的回调
 */
public class WxAuth extends AuthApi implements OAuthListener {
    private IWXAPI mWXApi;
    /**
     * 执行登陆操作
     *
     * @param act activity
     * @param l   回调监听
     */
    public WxAuth(Activity act, OnAuthListener l) {
        super(act, l);
        mAuthType = SocialType.WEIXIN_Auth;
        //mWXApi = WXAPIFactory.createWXAPI(mActivity.get(), getAppId(), true);
//        mWXApi.registerApp(getAppId());
        mWXApi = WxSocial.getInstance().getWXApi();
        BaseWXActivity.wxAuth = this;
    }

    @Override
    protected String getAppId() {
        return WxSocial.getInstance().getWxAppId();
    }

    /**
     * 基本信息验证
     */
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
        req.state = "wechat"; //用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止 csrf 攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加 session 进行校验。在state传递的过程中会将该参数作为url的一部分进行处理，因此建议对该参数进行url encode操作，防止其中含有影响url解析的特殊字符（如'#'、'&'等）导致该参数无法正确回传。
        //String.valueOf(System.currentTimeMillis());
        mAuthType = SocialType.WEIXIN_Auth;
        mWXApi.sendReq(req);
    }

    /**
     * 微信扫码授权登录
     * 先请求后台去获取微信扫码需要的ticket
     * @param ticket 后台返回的获取微信扫码需要的ticket
     */
    public void doAuthQRCode(String ticket) {
        if (TextUtils.isEmpty(ticket)) {
            setErrorCallBack("ticket为空");
            return;
        }
        if (baseVerify()) {
            return;
        }
        //在这init?
        if (!mWXApi.isWXAppInstalled()) {
            setErrorCallBack("微信未安装");
            return;
        }
        // send oauth request
        StringBuilder str = new StringBuilder(); // 定义变长字符串
        Random random = new Random();
        // 随机生成数字，并添加到字符串
        // 随机生成数字，并添加到字符串
        for (int i = 0; i <= 7; i++) {
            str.append(random.nextInt(10));
        }
        String noncestr = str.toString();
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        String string1 = String.format("appid=%s&noncestr=%s&sdk_ticket=%s&timestamp=%s",
                getAppId(), noncestr, ticket, timeStamp);
        String sha = ShareUtil.getSHA(string1);
        String scope = "snsapi_userinfo";
        mAuthType = SocialType.WEIXIN_Qr_Auth;
        WxSocial.getInstance().getDiffDevOAuth()
                .auth(getAppId(), scope, noncestr, timeStamp, sha, this);
    }

    @Override
    public void onAuthGotQrcode(String p0, byte[] qrCode) {
        Log.d("onAuthGotQrcode", ""+p0);
        Bitmap bmp = BitmapFactory.decodeByteArray(qrCode, 0, qrCode.length);
        //回调二维码给前台显示
        setCompleteCallBack(bmp);
    }

    @Override
    public void onQrcodeScanned() {
        //扫码了
        Log.d("onQrcodeScanned", "微信授权扫码");
    }

    @Override
    public void onAuthFinish(OAuthErrCode errCode, String authCode) {
        //扫码授权结果
        if (errCode == OAuthErrCode.WechatAuth_Err_OK) {
            //拿着authCode去登录
            mAuthType = SocialType.WEIXIN_Auth;
            setCompleteCallBack(new WeiXin(mAuthType, errCode.getCode(), authCode));
        } else {
            //失败
            setErrorCallBack(errCode.toString());
        }
        WxSocial.getInstance().getDiffDevOAuth().stopAuth();
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
//            String message = String.format("sendReq ret : %s", ret);
//            setCompleteCallBack(message);

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

//            boolean ret = mWXApi.sendReq(req);
//            setCompleteCallBack("sendReq result = " + ret);

        } else {
            setErrorCallBack("不支持小程序");
        }

    }

    /**
     * 限定值 小程序发布类型 开发版,体验版,正式版
     */
    @IntDef(value = {WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE,
            WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST,
            WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW})
    @Retention(RetentionPolicy.SOURCE)
    private @interface MiniProgramType {
    }
}
