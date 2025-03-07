package com.mhy.socialcommon;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * 分享平台公共组件模块
 */
public abstract class ShareApi {

    /**
     * 分享类型
     */
    protected static SocialType mShareType;
    private static OnShareListener mShareListener;
    protected WeakReference<Activity> mActivity;

    public ShareApi(Activity act, OnShareListener l) {
        mActivity = new WeakReference<>(act);
        setOnShareListener(l);
    }

    /**
     * 返回分享成功
     */
    public static void callbackShareOk() {
        if (mShareListener != null) {
            mShareListener.onShareOk(mShareType);
        }
    }

    /**
     * 返回分享失败
     *
     * @param msg 错误详情
     */
    public static void callbackShareFail(String msg) {
        if (mShareListener != null) {
            mShareListener.onShareFail(mShareType, msg);
        }
    }

    public static void callbackCancel() {
        if (mShareListener != null) {
            mShareListener.onCancel(mShareType);
        }
    }

    public static void cancelCallback() {
        mShareListener = null;
    }

    /**
     * 分享
     *
     * @param content 分享内容实体
     */
    public abstract void doShare(ShareEntity content);

    protected abstract String getAppId();

    /**
     * 分享回调 qq\微博 需要
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /**
     * 设置分享回调
     *
     * @param l l
     */
    private void setOnShareListener(OnShareListener l) {
        mShareListener = l;
    }

    /**
     * 分享回调
     */
    public interface OnShareListener {

        /**
         * 分享回调-成功分享
         */
        void onShareOk(SocialType type);

        /**
         * 分享回调-支付分享
         */
        void onShareFail(SocialType type, String msg);

        void onCancel(SocialType type);
    }
}
