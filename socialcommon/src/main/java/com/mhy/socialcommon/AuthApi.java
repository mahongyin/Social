package com.mhy.socialcommon;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * 社会化组件基类实现
 * @author mahongyin
 */
public abstract class AuthApi {
    protected static SocialType mAuthType;
    private static OnAuthListener mOnAuthListener;
    /** 防止在支付宝 等App 被强行退出等情况下，OpenAuthTask.Callback 一定时间内无法释放导致Activity 泄漏。*/
    protected WeakReference<Activity> mActivity;

    public AuthApi(Activity act, OnAuthListener l) {
        mActivity = new WeakReference<>(act);
        setAuthListener(l);
    }

    /**
     * 登陆成功回调
     */
    public static void setCompleteCallBack(Object user) {
        if (mOnAuthListener != null) {
            mOnAuthListener.onComplete(mAuthType, user);
        }
    }

    /**
     * 登陆错误回调
     */
    public static void setErrorCallBack(String error) {
        if (mOnAuthListener != null) {
            mOnAuthListener.onError(mAuthType, error);
        }
    }
    public static void cancelCallback() {
        mOnAuthListener = null;
    }
    /**
     * 登陆取消回调
     */
    public static void setCancelCallBack() {
        if (mOnAuthListener != null) {
            mOnAuthListener.onCancel(mAuthType);
        }
    }

    protected abstract String getAppId();

    /** 登陆授权回调
     * qq/微博 需要
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {}

    private void setAuthListener(OnAuthListener l) {
        mOnAuthListener = l;
    }

    public interface OnAuthListener {

        /**
         * 成功
         *
         * @param type 登陆类型
         * @param user 返回实体
         */
        void onComplete(SocialType type, Object user);

        /**
         * 失败
         *
         * @param type  登陆类型
         * @param error 失败原因
         */
        void onError(SocialType type, String error);

        /**
         * 用户取消
         *
         * @param type 登陆类型
         */
        void onCancel(SocialType type);
    }
}
