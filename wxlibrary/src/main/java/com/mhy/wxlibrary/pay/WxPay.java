package com.mhy.wxlibrary.pay;

import android.app.Activity;
import android.text.TextUtils;

import com.mhy.socialcommon.PayApi;
import com.mhy.socialcommon.PayContent;
import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.WxSocial;
import com.mhy.wxlibrary.bean.WxPayContent;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.JumpToOfflinePay;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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
        msgApi = WxSocial.getInstance().getWXApi();
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
        new AsyncTaskEx<Void, Void, WxPayContent>() {

            @Override
            protected WxPayContent doInBackground(Void... params) {
                return content;
            }

            @Override
            protected void onPostExecute(WxPayContent result) {
                if (TextUtils.isEmpty(getAppId())) {
                    callbackPayFail("appid为空");
                    return;
                }
                PayReq req = new PayReq();
                req.appId = result.getAppid();
                req.partnerId = result.getPartnerid();
                req.prepayId = result.getPrepayid();
                req.packageValue = result.getPackageValue();
                req.nonceStr = result.getNoncestr();
                req.timeStamp = result.getTimestamp();
                req.sign = result.getSign();
                msgApi.sendReq(req);
            }
        }.execute();
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
