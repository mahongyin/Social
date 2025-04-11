package com.mhy.wxlibrary.pay;

import android.app.Activity;
import android.text.TextUtils;

import com.mhy.socialcommon.PayApi;
import com.mhy.socialcommon.PayContent;
import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.WxSocial;
import com.mhy.wxlibrary.bean.WxPayContent;
import com.mhy.wxlibrary.wxapi.BaseWXPayEntryActivity;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.JumpToOfflinePay;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 微信支付
 */
public class WxPay extends PayApi {

    private IWXAPI msgApi;

    /**
     * 支付
     *
     * @param act act
     * @param l   回调
     */
    public WxPay(Activity act, OnPayListener l) {
        super(act, l);
        mPayType = SocialType.WEIXIN_Pay;
        if (msgApi == null) {
            msgApi = WxSocial.getInstance().getWXApi();
        }
        BaseWXPayEntryActivity.wxPay = this;
    }

    /**
     * 之前先和后台交互好订单
     * 这一套后台已经帮你处理好金额之类的了
     *
     * @param payInfo 支付payInfo
     */

    @Override
    public void doPay(PayContent payInfo) {
        if (payInfo == null) {
            callbackPayFail("payInfo为空");
            return;
        }
        final WxPayContent content;
        if (payInfo.getPayType() != SocialType.WEIXIN_Pay) {
            callbackPayFail("类型参数错误");
            return;
        } else {
            content = (WxPayContent) payInfo;
        }

        if (TextUtils.isEmpty(content.getAppid()) && TextUtils.isEmpty(getAppId())) {
            callbackPayFail("appid为空");
            return;
        }

//       appid partnerid商户号 prepayid密钥
        //msgApi = WXAPIFactory.createWXAPI(mActivity.get(), TextUtils.isEmpty(content.getAppid()) ? getAppId() : content.getAppid());

        if (!msgApi.isWXAppInstalled()) {
            callbackPayFail("微信未安装");
            return;
        }

        if (TextUtils.isEmpty(getAppId())) {
            callbackPayFail("appid为空");
            return;
        }

        PayReq req = new PayReq();
        req.appId = content.getAppid();
        req.partnerId = content.getPartnerid();
        req.prepayId = content.getPrepayid();
        req.packageValue = content.getPackageValue();
        req.nonceStr = content.getNoncestr();
        req.timeStamp = content.getTimestamp();
        req.sign = content.getSign();
        msgApi.sendReq(req);

//        FutureTask<WxPayContent> futureTask = new FutureTask<WxPayContent>(new Callable<WxPayContent>() {
//            @Override
//            public WxPayContent call() {
//                return content;
//            }
//        });
//        Executors.newSingleThreadExecutor().submit(futureTask);
//        try {
//            content = futureTask.get();
//        } catch (Exception e) {
//
//        }
    }

    private String getAppId() {
        return WxSocial.getInstance().getWxAppId();
    }

    /**
     * 离线支付
     */
    public void offLinePay() {
        int wxSdkVersion = msgApi.getWXAppSupportAPI();
        if (wxSdkVersion >= Build.OFFLINE_PAY_SDK_INT) {
            msgApi.sendReq(new JumpToOfflinePay.Req());
        } else {
            callbackPayFail("不支持离线支付");
        }
    }
}
