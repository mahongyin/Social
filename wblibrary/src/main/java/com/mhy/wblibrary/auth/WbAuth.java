package com.mhy.wblibrary.auth;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.SocialType;
import com.mhy.wblibrary.WbSocial;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

/**
 * 微博登陆授权
 *
 * @author mahongyin
 */
public class WbAuth extends AuthApi {
    private IWBAPI mWBApi;

    /**
     * @param act activity
     * @param listener   回调监听
     */
    public WbAuth(Activity act, OnAuthListener listener) {
        super(act, listener);
        mAuthType = SocialType.WEIBO_Auth;

        AuthInfo authInfo =
                new AuthInfo(act, getAppId(), WbSocial.getInstance().getRedirectUrl(), WbSocial.getInstance().getScope());
        mWBApi = WBAPIFactory.createWBAPI(act);
        mWBApi.registerApp(act, authInfo);
    }

    @Override
    protected String getAppId() {
        return WbSocial.getInstance().getAppKy();
    }

    private boolean cpuX86() {
        String arch = System.getProperty("os.arch");
        // 大写
        String arc = null;
        if (arch != null) {
            arc = arch.toUpperCase();
            return arc.contains("X86");
        }
        return false;
    }

    /**
     * 执行登陆操作 客户端或web
     */
    public void doAuth() {
        // auth 客户端和web
        mWBApi.authorize(mActivity.get(),
                new WbAuthListener() {
                    @Override
                    public void onComplete(Oauth2AccessToken token) {
                        //"微博授权成功"
                        setCompleteCallBack(token);
                    }

                    @Override
                    public void onError(UiError error) {
                        //"微博授权出错"
                        setErrorCallBack(error.errorMessage);
                    }

                    @Override
                    public void onCancel() {
                        //"微博授权取消"
                        setCancelCallBack();
                    }
                });
    }

    /**
     * 仅客户端
     */
    public void doClientAuth() {
        mWBApi.authorizeClient(mActivity.get(), new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                setCompleteCallBack(token);
            }

            @Override
            public void onError(UiError error) {
                setErrorCallBack(error.errorMessage);
            }

            @Override
            public void onCancel() {
                setCancelCallBack();
            }
        });
    }

    /**
     * 仅web
     */
    public void dotWebAuth() {
        mWBApi.authorizeWeb(mActivity.get(), new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                setCompleteCallBack(token);
            }

            @Override
            public void onError(UiError error) {
                setErrorCallBack(error.errorMessage);
            }

            @Override
            public void onCancel() {
                setCancelCallBack();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mWBApi.authorizeCallback(mActivity.get(), requestCode, resultCode, data);
    }
}
