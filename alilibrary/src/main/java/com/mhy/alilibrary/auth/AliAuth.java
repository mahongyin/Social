package com.mhy.alilibrary.auth;

import static com.mhy.alilibrary.AliSoial.SDK_AUTH_FLAG;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;
import com.mhy.alilibrary.bean.AuthResult;
import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.SocialType;

import java.util.Map;

/**
 * @author mahongyin 2020-05-29 18:29 @CopyRight mhy.work@qq.com
 * description .
 */
public class AliAuth extends AuthApi {

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == SDK_AUTH_FLAG) {
                @SuppressWarnings("unchecked")
                AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                String resultStatus = authResult.getResultStatus();

                // 判断resultStatus 为“9000”且result_code
                // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                    // 获取alipay_open_id，调支付时作为参数extern_token 的value
                    // 传入，则支付账户为该授权账户
//                        showAlert(PayDemoActivity.this, getString(R.string.auth_success) + authResult);
                    setCompleteCallBack(authResult);
                } else {
                    // 其他状态值则为授权失败
//                        showAlert(PayDemoActivity.this, getString(R.string.auth_failed) + authResult);
                    setErrorCallBack(resultStatus);
                }
            }
        }
    };

    public AliAuth(Activity act, OnAuthListener l) {
        super(act, l);
        mAuthType = SocialType.ALIPAY_Auth;
    }

    @Override
    protected String getAppId() {
        return "";
    }

    public void doAuth(String orderInfo) {
        if (TextUtils.isEmpty(orderInfo)) {
            setErrorCallBack("orderInfo是空");
            return;
        }
        authV2(orderInfo);
    }

    /**
     * 支付宝账户授权业务示例
     */
    public void authV2(final String orderInfo) {

        /*
         * authInfo 的获取必须来自服务端；
         */

        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(mActivity.get());
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }
    public void release(){
        mHandler.removeCallbacksAndMessages(null);
    }
}
