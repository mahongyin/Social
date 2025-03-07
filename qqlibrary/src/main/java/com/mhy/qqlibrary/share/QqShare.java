package com.mhy.qqlibrary.share;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.mhy.qqlibrary.QqSocial;
import com.mhy.qqlibrary.bean.QQShareEntity;
import com.mhy.socialcommon.ShareApi;
import com.mhy.socialcommon.ShareEntity;
import com.mhy.socialcommon.SocialType;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * @author mahongyin 2020-05-29 19:32 @CopyRight mhy.work@qq.com
 * description .
 */
public class QqShare extends ShareApi {
    Tencent mTencent;
    BaseUiListener mQQCallbackListener = new BaseUiListener();

    public QqShare(Activity act, OnShareListener l) {
        super(act, l);
        Tencent.setIsPermissionGranted(true);
        if (mTencent == null) {
            //authInfo"101807669"
            mTencent = Tencent.createInstance(getAppId(), mActivity.get(), mActivity.get().getPackageName() + ".social.fileprovider");
        }
    }

    @Override
    public void doShare(ShareEntity shareInfo) {
        if (shareInfo == null) {
            return;
        }
        mShareType = shareInfo.getType();
        if (baseVerify()) {
            return;
        }

        if (mTencent != null) {
            if (shareInfo.getType() == SocialType.QQ_Share) {
                if (Tencent.isSupportShareToQQ(mActivity.get())) {
                    mTencent.shareToQQ(mActivity.get(), shareInfo.getParams(), mQQCallbackListener);
                }
            } else if (shareInfo.getType() == SocialType.QQ_PUBLISH_Share) {
                if (Tencent.isSupportPushToQZone(mActivity.get())) {
                    mTencent.publishToQzone(mActivity.get(), shareInfo.getParams(), mQQCallbackListener);
                }
            } else {
                if (Tencent.isSupportPushToQZone(mActivity.get())) {
                    mTencent.shareToQzone(mActivity.get(), shareInfo.getParams(), mQQCallbackListener);
                }
            }
        }
    }

    @Override
    protected String getAppId() {
        return QqSocial.getAppId();
    }

    /*基本信息验证*/
    private boolean baseVerify() {
        if (TextUtils.isEmpty(getAppId())) {
            callbackShareFail("appid为空");
            return true;
        }
        return false;
    }

    public IUiListener getQQCallbackListener() {
        return mQQCallbackListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Constants.REQUEST_QQ_SHARE||requestCode == Constants.REQUEST_QZONE_SHARE) {
        Tencent.onActivityResultData(requestCode, resultCode, data, getQQCallbackListener());
//        }
    }

    public class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
//            Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
            callbackShareOk();
            mTencent.logout(mActivity.get());//登录成功注销
        }

        @Override
        public void onError(UiError uiError) {
//            Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
            callbackShareFail("登录失败" + uiError.errorMessage);
        }

        @Override
        public void onCancel() {
//            Toast.makeText(context, "取消登录", Toast.LENGTH_SHORT).show();
            callbackCancel();
        }

        @Override
        public void onWarning(int i) {

        }
    }
}
