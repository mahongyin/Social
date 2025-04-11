package com.mhy.wxlibrary.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.ShareApi;
import com.mhy.socialcommon.ShareUtil;
import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.WxSocial;
import com.mhy.wxlibrary.bean.WeiXin;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicVideoObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * 微信回调  类名为WXEntryActivity
 */
public abstract class BaseWXActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXBaseActivity";
    private IWXAPI api;

    public static ShareApi wxShare;
    public static AuthApi wxAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.getData() != null) {
            Log.d(TAG,"intent启动"+ intent.getData().toString());
        }
        if (intent.getExtras()!=null){
            Log.d(TAG,"intent启动"+  ShareUtil.bundleToJson(intent.getExtras()));
        }
        try {
            //api = WXAPIFactory.createWXAPI(this, getAppId(), false);
            //api.registerApp(getAppId());//add
            api = WxSocial.getInstance().getWXApi();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            Log.e(TAG, "微信回调异常：", e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData() != null) {
            Log.d(TAG,"intent启动"+ intent.getData().toString());
        }
        if (intent.getExtras()!=null){
            Log.d(TAG,"intent启动"+  ShareUtil.bundleToJson(intent.getExtras()));
        }
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    /**
     * 处理从微信返回到第三方应用
     */
    @Override
    public void onReq(BaseReq baseReq) {
        // 微信主动发的意图
        if (baseReq != null) {
            switch (baseReq.getType()) {
                case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                    Log.i(TAG, "来自微信消息");
                    if (baseReq instanceof GetMessageFromWX.Req) {
                        getWxMsg((GetMessageFromWX.Req) baseReq);
                    }
                    break;
                case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                    Log.i(TAG, "发消息给微信");
                    if (baseReq instanceof SendMessageToWX.Req) {
                        sendWxMsg((SendMessageToWX.Req) baseReq);
                    }
                    break;
                case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                    Log.i(TAG, "来自微信消息展示");
                    if (baseReq instanceof ShowMessageFromWX.Req) {
                        showWxMsg((ShowMessageFromWX.Req) baseReq);
                    }
                    break;
                default:
                    Log.i(TAG, "微信的意图 " + baseReq.getType());
                    break;
            }
        }
        finish();
    }

    //1登陆，2分享
    @Override
    public void onResp(BaseResp resp) {
        if (resp != null) {
            Log.i(TAG, String.format("微信返回：%s, %s", resp.getType(), resp.errCode == BaseResp.ErrCode.ERR_OK));
            if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//分享
                Log.i(TAG, "微信分享操作.....");
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        if (wxShare != null) {
                            wxShare.callbackShareOk();
                        }
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        if (wxShare != null) {
                            wxShare.callbackCancel();
                        }
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        if (wxShare != null) {
                            wxShare.callbackShareFail("用户拒绝" + resp.errStr);
                        }
                        break;
                    default:
                        if (wxShare != null) {
                            wxShare.callbackShareFail("未知错误" + resp.errStr);
                        }
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
                        if (wxAuth != null) {
                            wxAuth.setCompleteCallBack(new WeiXin(SocialType.WEIXIN_Auth, resp.errCode, code));
                        }
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        if (wxAuth != null) {
                            wxAuth.setCancelCallBack();
                        }
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        if (wxAuth != null) {
                            wxAuth.setErrorCallBack("用户拒绝" + resp.errStr);
                        }
                        break;
                    default:
                        if (wxAuth != null) {
                            wxAuth.setErrorCallBack("未知错误" + resp.errStr);
                        }
                        break;
                }
            } else if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
                //微信小程序
                WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
// https://www.jianshu.com/p/c08b54299e8a
// 对应JsApi navigateBackApplication中的extraData字段数据
// <view class='suspension'>
//   <button class="server_button" open-type="launchApp" app-parameter="wechat" binderror="launchAppError">打开APP</button>
// </view>
                // 小程序给APP传值
                String extraData = launchMiniProResp.extMsg;
                if (wxAuth != null) {
                    wxAuth.setCompleteCallBack(extraData); //TODO 回调区分
                }

            }
        }
        finish();
    }

    private void getWxMsg(GetMessageFromWX.Req baseReq) {
//        Intent intent = getIntent();
//        //bundle为微信传递过来的intent所带的内容，通过getExtras方法获取
//        Bundle bundle1 = intent.getExtras();
//        final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle1);//就是baseReq
//        String transaction = req.transaction;

        //String transaction1 = baseReq.transaction;

        Bundle bundleReq = new Bundle();
        baseReq.toBundle(bundleReq);
        Log.i(TAG, "get微信消息：" + ShareUtil.bundleToJson(bundleReq));
    }

    private void sendWxMsg(SendMessageToWX.Req baseReq) {
//        Intent intent = getIntent();
//        //bundle为微信传递过来的intent所带的内容，通过getExtras方法获取
//        Bundle bundle1 = intent.getExtras();//就是baseReq的Bundle

        //String openId = baseReq.userOpenId;

        Bundle bundleReq = new Bundle();
        baseReq.toBundle(bundleReq);
        Log.i(TAG, "send微信消息：" + ShareUtil.bundleToJson(bundleReq));

//        WXMediaMessage wxMsg = baseReq.message;
//        WXMediaMessage.IMediaObject mediaObject = wxMsg.mediaObject;
//        if (mediaObject == null) {
//            return;
//        }
//        Bundle bundleObj = new Bundle();
//        mediaObject.serialize(bundleObj);
    }

    /**
     * 处理微信意图
     */
    private void showWxMsg(ShowMessageFromWX.Req showReq) {
//        Intent intent = getIntent();
//        //bundle为微信传递过来的intent所带的内容，通过getExtras方法获取
//        Bundle bundle1 = intent.getExtras();//就是showReq的Bundle

        Bundle bundleReq = new Bundle();
        showReq.toBundle(bundleReq);
        Log.i(TAG, "show微信消息：" + ShareUtil.bundleToJson(bundleReq));

        WXMediaMessage wxMsg = showReq.message;

//        byte[] thumb = wxMsg.thumbData;
//        String title = wxMsg.title;
//        String description = wxMsg.description;
        String messageExt = wxMsg.messageExt;//分享到微信时传的额外信息字段

        if (wxMsg.mediaObject instanceof WXAppExtendObject) {
            WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//            String extInfo = obj.extInfo;
//            String filePath = obj.filePath;
//            byte[] fileData = obj.fileData;

            Bundle bundleObj = new Bundle();
            obj.serialize(bundleObj);
            onWXIntent(obj.type(), ShareUtil.bundleToJson(bundleObj));
        } else if (wxMsg.mediaObject instanceof WXMusicVideoObject) {
            WXMusicVideoObject musicVideoObject = (WXMusicVideoObject) wxMsg.mediaObject;
//            String identification = musicVideoObject.identification;    // 分享到微信时的音乐标识符字段
            // 应用根据identification与messageExt自行处理

            Bundle bundleObj = new Bundle();
            musicVideoObject.serialize(bundleObj);
            onWXIntent(musicVideoObject.type(), ShareUtil.bundleToJson(bundleObj));
        }
    }

    /**
     * 处理微信意图
     */
    protected abstract void onWXIntent(int objType, String json);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wxAuth != null) {
            wxAuth.cancelCallback();
            wxAuth = null;
        }
        if (wxShare != null) {
            wxShare.cancelCallback();
            wxShare = null;
        }
    }
}