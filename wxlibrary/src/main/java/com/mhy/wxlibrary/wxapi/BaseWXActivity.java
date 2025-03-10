package com.mhy.wxlibrary.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.WxSocial;
import com.mhy.wxlibrary.auth.WxAuth;
import com.mhy.wxlibrary.bean.WeiXin;
import com.mhy.wxlibrary.share.WxShare;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * 微信回调  类名为WXEntryActivity
 */
public abstract class BaseWXActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXBaseActivity";
    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //api = WXAPIFactory.createWXAPI(this, getAppId(), false);
        //api.registerApp(getAppId());//add
        api = WxSocial.getInstance().getWXApi();
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // 微信主动发的意图
        if (baseReq != null) {
            switch (baseReq.getType()) {
                case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                    Log.i("BaseWXActivity", "来自微信消息");
                    break;
                case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                    Log.i("BaseWXActivity", "发消息给微信");
                    break;
                case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                    if (baseReq instanceof ShowMessageFromWX.Req) {
                        Log.i("BaseWXActivity", "来自微信消息展示");
                        getWxMsg((ShowMessageFromWX.Req) baseReq);
                    }
                    break;
                default:
                    Log.i("BaseWXActivity", "微信的意图 " + baseReq.getType());
                    break;
            }
        }
        finish();
    }

    //1登陆，2分享
    @Override
    public void onResp(BaseResp resp) {
        if (resp != null) {
            Log.i("BaseWXActivity", String.format("微信返回：%s, %s", resp.getType(), resp.errCode == BaseResp.ErrCode.ERR_OK));
            if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//分享
                Log.i(TAG, "微信分享操作.....");
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        WxShare.callbackShareOk();
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        WxShare.callbackCancel();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        WxShare.callbackShareFail(resp.errStr);
                        break;
                    default:
                        WxShare.callbackShareFail(resp.errStr);
                        break;
                }
//            WeiXin weiXin = new WeiXin(2, resp.errCode, "");
//            EventBus.getDefault().post(weiXin);
            } else if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//微信授权登陆
                Log.i(TAG, "微信登录操作.....");
//            SendAuth.Resp authResp = (SendAuth.Resp) resp;//因为只有OK才有authResp.code
//            WeiXin weiXin = new WeiXin(1, resp.errCode, authResp.code);
//            EventBus.getDefault().post(weiXin);
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        String code = ((SendAuth.Resp) resp).code;
                        //建议将Appsecret、用户数据（如access_token）放在App云端服务器，由云端中转接口调用请求。
                        //把code交给后台去和wx后台通信,
                        // 交给后台去做：通过code获取access_token  access_token的有效期是2小时
                        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=${code}&grant_type=authorization_code
                        //刷新 access_token 有效期  都是后台做  refresh_token
                        //https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN
                        //获取用户信息
                        //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
                        //同一用户（openid）在一分钟内不能调用以上接口合计180次，超过会被当成恶意攻击返回 45011
                        WxAuth.setCompleteCallBack(new WeiXin(SocialType.WEIXIN_Auth, resp.errCode, code));
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        WxAuth.setCancelCallBack();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        WxAuth.setErrorCallBack("用户拒绝"+resp.errStr);
                        break;
                    default:
                        WxAuth.setErrorCallBack("未知错误"+resp.errStr);
                        break;
                }
            } else if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
                //微信小程序
                WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
//              https://www.jianshu.com/p/c08b54299e8a
                // 对应JsApi navigateBackApplication中的extraData字段数据
//    <view class='suspension'>
//      <button class="server_button" open-type="launchApp" app-parameter="wechat" binderror="launchAppError">打开APP</button>
//    </view>//小程序给APP传值
                String extraData = launchMiniProResp.extMsg;
                WxAuth.setCompleteCallBack(extraData); //TODO 回调区分
            }
        }
        finish();
    }

    /**
     * 处理微信意图
     */
    private void getWxMsg(ShowMessageFromWX.Req showReq) {
        if (showReq != null) {
            WXMediaMessage wxMsg = showReq.message;
            WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
            String extInfo = obj.extInfo;
            Uri titleLink = Uri.parse(extInfo);
            Log.i("微信Uri", titleLink.toString());
            //val linkUri = titleLink.getQueryParameter("link") ?: "";
            //Intent/EventBus
            onWXIntent(extInfo);
        }
    }

    /**
     * 处理微信意图
     *
     * @param extInfo 例如a.href的url
     */
    protected abstract void onWXIntent(String extInfo);

    @Override
    public void finish() {
        super.finish();
        WxShare.cancelCallback();
        WxAuth.cancelCallback();
    }
}