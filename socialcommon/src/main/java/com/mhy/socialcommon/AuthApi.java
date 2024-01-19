package com.mhy.socialcommon;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * 社会化组件基类实现
 */
public abstract class AuthApi {
    // 防止在支付宝 等App 被强行退出等情况下，OpenAuthTask.Callback 一定时间内无法释放导致Activity 泄漏。
    protected WeakReference<Activity> mActivity;

    protected static SocialType mAuthType;

    protected static OnAuthListener mOnAuthListener;

    public AuthApi(Activity act, OnAuthListener l) {
        mActivity = new WeakReference<>(act);
        setAuthListener(l);
    }

    protected abstract String getAppId();
//    public abstract void doAuth(String mInfo);

    /**
     * qq weibo 需要
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (getAuthType()) {
////            case AuthType.QQ:
////                if (requestCode == Constants.REQUEST_LOGIN) {
////                    Tencent.handleResultData(data, ((QQAuth)this).getUIListener());
////                }
////                break;
////            case AuthType.WEIBO:
////                SsoHandler ssoHandler = ((WeiboAuth)this).getSsoHandler();
////                if(ssoHandler != null) {
////                    ((WeiboAuth) this).getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
////                }
////                break;
//            case SocialType.WEIXIN_Auth:
//
//                break;
//        }
    }

    private void setAuthListener(OnAuthListener l) {
        mOnAuthListener = l;
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

    /**
     * 登陆取消回调
     */
    public static void setCancelCallBack() {
        if (mOnAuthListener != null) {
            mOnAuthListener.onCancel(mAuthType);
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        mOnAuthListener = null;
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
