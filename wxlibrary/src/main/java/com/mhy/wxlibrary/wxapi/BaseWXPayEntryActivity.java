package com.mhy.wxlibrary.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mhy.wxlibrary.WxSocial;
import com.mhy.wxlibrary.pay.WxPay;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信支付  继承此类  并类名为WXPayEntryActivity
 */
public abstract class BaseWXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayBaseEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, getAppId());
//		api.registerApp(Social.getWeixinId());//add
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    protected String getAppId() {
        return WxSocial.getWeixinId();
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //微信支付
            //支付成功 通知Wxpay回调
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                WxPay.callbackPayOk();
            } else {
                WxPay.callbackPayFail(resp.errStr);
                Log.i("payfailcode:", String.valueOf(resp.errCode));
            }
        }
        finish();
    }
}