package com.mhy.wxlibrary.wxapi;

import android.app.Activity;
import android.content.Intent;
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
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信回调  类名为WXEntryActivity
 *
 */
public abstract class BaseWXActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXBaseActivity";
    private IWXAPI api;

    protected String getAppId() {
        return WxSocial.getWeixinId();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, getAppId(), false);
//        api.registerApp(Social.getWeixinId());//add
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq arg0) {

    }
    //1登陆，2分享
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//分享
            Log.i(TAG, "微信分享操作.....");
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    WxShare.callbackShareOk();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    WxShare.callbackCancel();;
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
        } else if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//登陆
            Log.i(TAG, "微信登录操作.....");
//            SendAuth.Resp authResp = (SendAuth.Resp) resp;//因为只有OK才有authResp.code
//            WeiXin weiXin = new WeiXin(1, resp.errCode, authResp.code);
//            EventBus.getDefault().post(weiXin);
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code=((SendAuth.Resp)resp).code;
                    //把code交给后台去和wx后台通信
                    WxAuth.setCompleteCallBack(new WeiXin(SocialType.WEIXIN_Auth, resp.errCode, code));
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    WxAuth.setCancelCallBack();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    WxAuth.setErrorCallBack(resp.errStr);
                    break;
                default:
                    WxAuth.setErrorCallBack(resp.errStr);
                    break;
            }
        }else if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            //微信小程序
            WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
//           https://www.jianshu.com/p/c08b54299e8a
            // 对应JsApi navigateBackApplication中的extraData字段数据
//    <view class='suspension'>
//      <button class="server_button" open-type="launchApp" app-parameter="wechat" binderror="launchAppError">打开APP</button>
//    </view>//小程序给APP传值
            String extraData =launchMiniProResp.extMsg;
            WxAuth.setCompleteCallBack(extraData); //TODO 回调区分
        }

        //登陆返回
//        if(resp instanceof SendAuth.Resp){
//            switch (resp.errCode) {
//                case BaseResp.ErrCode.ERR_OK:
//                    String code=((SendAuth.Resp)resp).code;
//                    //把code交给后台去和wx后台通信
//                    WxAuth.setCompleteCallBack(new WeiXin(1, resp.errCode, code));
//                    break;
//                case BaseResp.ErrCode.ERR_USER_CANCEL:
//                    WxAuth.setCancelCallBack();
//                    break;
//                case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                    WxAuth.setErrorCallBack(resp.errStr);
//                    break;
//                default:
//                    WxAuth.setErrorCallBack(resp.errStr);
//                    break;
//            }
//        }else {//分享返回
//            switch (resp.errCode) {
//                case BaseResp.ErrCode.ERR_OK:
//                    WxShare.callbackShareOk();
//                    break;
//                case BaseResp.ErrCode.ERR_USER_CANCEL:
//                    WxShare.callbackShareFail("分享取消");
//                    break;
//                case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                    WxShare.callbackShareFail(resp.errStr);
//                    break;
//                default:
//                    WxShare.callbackShareFail(resp.errStr);
//                    break;
//            }
//        }

        finish();
    }

}