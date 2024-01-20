package com.mhy.alilibrary.pay;

import static com.mhy.alilibrary.AliSoial.SDK_PAY_FLAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.mhy.alilibrary.bean.AliPayContent;
import com.mhy.alilibrary.bean.PayResult;
import com.mhy.socialcommon.PayApi;
import com.mhy.socialcommon.PayContent;
import com.mhy.socialcommon.SocialType;

import java.util.Map;

/**
 * @author mahongyin 2020-05-29 18:27 @CopyRight mhy.work@qq.com
 * description 支付宝支付
 */
public class AliPay extends PayApi {

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        showAlert(PayDemoActivity.this, getString(R.string.pay_success) + payResult);
                        callbackPayOk();
                        Log.e("resultInfo", resultInfo);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showAlert(PayDemoActivity.this, getString(R.string.pay_failed) + payResult);
                        callbackPayFail(resultInfo);
                        Log.e("resultInfo", resultStatus);
                    }
                    break;
                }

                default:
                    break;
            }
        }

        ;
    };

    public AliPay(Activity act, OnPayListener l) {
        super(act, l);
        mPayType = SocialType.ALIPAY_Pay;
    }

    /**
     * 支付宝支付
     *
     * @param payInfo 支付的OrderInfo 统一由后端返回 或者自己拼接后传入
     */
    @Override
    public void doPay(PayContent payInfo) {
        if (payInfo == null) {
            callbackPayFail("orderInfo为空");
            return;
        }
//        if (payInfo instanceof AliPayContent){
        if (payInfo.getPayType() == SocialType.ALIPAY_Pay) {
            if (TextUtils.isEmpty(((AliPayContent) payInfo).getOrderInfo())) {
                callbackPayFail("orderInfo为空");
            } else {
                payV2(((AliPayContent) payInfo).getOrderInfo());
            }
        } else {
            callbackPayFail("类型参数错误");
        }
    }

    /**
     * 支付宝支付业务示例
     * orderInfo 的获取必须来自服务端；
     */
    private void payV2(final String orderInfo) {

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity.get());
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * 获取支付宝 SDK 版本号。
     */
    public String showSdkVersion() {
        PayTask payTask = new PayTask(mActivity.get());
        return payTask.getVersion();

    }
}
