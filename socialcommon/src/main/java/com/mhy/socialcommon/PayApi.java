package com.mhy.socialcommon;

import android.app.Activity;

import java.lang.ref.WeakReference;


/**
 * 支付基类
 */
public abstract class PayApi {

    //只允许一个实例化回调
    protected OnPayListener mPayResultListener;
    protected SocialType mPayType;
    protected WeakReference<Activity> mActivity;


    public PayApi(Activity act, OnPayListener listener) {
        mActivity = new WeakReference<>(act);
        mPayResultListener = listener;
    }
//    public PayApi(Activity act){
//        mAct = act;
//        setOnPayListener(null);
//    }

    /**
     * 返回支付成功
     */
    public void callbackPayOk() {
        if (mPayResultListener != null) {
            mPayResultListener.onPayOk(mPayType);
        }
    }

    /**
     * 返回支付失败
     */
    public void callbackPayFail(String msg) {
        if (mPayResultListener != null) {
            mPayResultListener.onPayFail(mPayType, msg);
        }
    }

    public void cancelCallback() {
        mPayResultListener = null;
    }

    /**
     * 调用支付sdk
     *
     * @param payInfo 支付sdk
     */
    public abstract void doPay(PayContent payInfo);

    /**
     * 支付回调
     */
    public interface OnPayListener {

        /**
         * 支付回调-成功支付
         */
        void onPayOk(SocialType type);

        /**
         * 支付回调-支付失败
         */
        void onPayFail(SocialType type, String msg);

    }


}
