package com.mhy.alilibrary;

import android.app.Activity;

import com.mhy.alilibrary.auth.AliAuth;
import com.mhy.alilibrary.pay.AliPay;
import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.PayApi;

/**
 * @author mahongyin 2020-05-29 18:33 @CopyRight mhy.work@qq.com
 * description .
 */
public class AliSoial {
    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_AUTH_FLAG = 2;

    private static AliSoial singleton;

    public static AliSoial getInstance() {
        if (singleton == null) {
            synchronized (AliSoial.class) {
                if (singleton == null) {
                    singleton = new AliSoial();
                }
            }
        }
        return singleton;
    }

    public AliAuth getAuth(Activity activity, AuthApi.OnAuthListener listener) {
        return new AliAuth(activity, listener);
    }

    public AliPay getPay(Activity activity, PayApi.OnPayListener listener) {
        return new AliPay(activity, listener);
    }
}
