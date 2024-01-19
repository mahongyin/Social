package com.mhy.qqlibrary.auth;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.mhy.qqlibrary.QqSocial;
import com.mhy.socialcommon.AuthApi;
import com.mhy.socialcommon.SocialType;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.connect.common.Constants;
import com.tencent.open.im.IM;
import com.tencent.open.miniapp.MiniApp;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author mahongyin 2020-05-29 19:07 @CopyRight mhy.work@qq.com
 * description .
 */
public class QqAuth extends AuthApi {
    private Tencent mTencent;

    public QqAuth(Activity act, OnAuthListener l) {
        super(act, l);
        Tencent.setIsPermissionGranted(true);
        mAuthType = SocialType.QQ_Auth;
        if (mTencent == null) {
            //authInfo"101807669"
            mTencent = Tencent.createInstance(getAppId(), mActivity.get());
        }
    }

    @Override
    protected String getAppId() {
        return QqSocial.getAppId();
    }


    public void doAuth() {
        if (baseVerify()) {
            return;
        }
//      mTencent.checkLogin(baseUiListener);//验证token是否有效
//         mTencent = Tencent.createInstance(QqSocial.getAppId()/*getInfo()*/, mActivity);//authInfo"101807669"
        if (mTencent != null && !mTencent.isSessionValid()) {
            mTencent.login(mActivity.get(), "all", baseUiListener, true);//true 没装手q就扫码?
        } else if (mTencent != null) {
//            UserInfo info = new UserInfo(mActivity, mTencent.getQQToken());
//            info.getUserInfo(baseUiListener);
            mTencent.logout(mActivity.get());
            mTencent.login(mActivity.get(), "all", baseUiListener, true);//true 没装手q就扫码?
        }
    }

    /**
     * @param isQR true 强制扫码  false 拒绝扫码
     */
    public void doAuth(boolean isQR) {
        if (baseVerify()) {
            return;
        }
        if (mTencent != null && !mTencent.isSessionValid()) {
            if (isQR) {
                //强制扫码/web登陆
                mActivity.get().getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);
                mTencent.login(mActivity.get(), "all", baseUiListener, true);
            }//true 没装手q就扫码
            else {
                mTencent.login(mActivity.get(), "all", baseUiListener);
            }
        } else if (mTencent != null) {
//            UserInfo info = new UserInfo(mActivity, mTencent.getQQToken());
//            info.getUserInfo(baseUiListener);
            mTencent.logout(mActivity.get());
            if (isQR) {
                mActivity.get().getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, true);//强制扫码/web登陆

                mTencent.login(mActivity.get(), "all", baseUiListener, true);//true 没装手q就扫码
            } else {
                mTencent.login(mActivity.get(), "all", baseUiListener);
            }
        }
    }

    /**
     * 打开小程序、小游戏  MiniAppId 和AppId 主体对应
     *
     * @param miniAppId      "1108108864"
     * @param miniAppPath    "pages/tabBar/index/index" //路径错误将默认打开首页
     * @param miniAppVersion (develop/trial/release)
     */
    public void doOpenMiniApp(String miniAppId, String miniAppPath, String miniAppVersion) {

        if (TextUtils.isEmpty(miniAppId)) {
            setErrorCallBack("miniapp id is empty");
            return;
        }
        // 校验版本类型
        if (!MiniApp.OPEN_CONNECT_DEMO_MINI_APP_VERSIONS.contains(miniAppVersion)) {
            //不是发布版本
            setErrorCallBack("miniapp version wrong");
            return;
        }
        launchMiniApp(miniAppId, miniAppPath, miniAppVersion);
    }

    /**
     * 拉起小程序/小游戏
     * appid能够直接拉取到该应用主体对应的apptype是小程序还是小游戏
     * 此处暂时无需声明MiniApp的类型
     */
    private void launchMiniApp(String miniAppId, String miniAppPath, String miniAppVersion) {
        int ret = MiniApp.MINIAPP_UNKNOWN_TYPE;
        ret = mTencent.startMiniApp(mActivity.get(), miniAppId, miniAppPath, miniAppVersion);
        if (ret != MiniApp.MINIAPP_SUCCESS) {
            // 互联demo针对纯输入出错的地方进行文字提示
            String errorStr = "";
            if (ret == MiniApp.MINIAPP_ID_EMPTY) {
                errorStr = "mini_app_id_empty";
            } else if (ret == MiniApp.MINIAPP_ID_NOT_DIGIT) {
                errorStr = "mini_app_id_not_digit";
            }
            StringBuilder builder = new StringBuilder();
            builder.append("start miniapp failed. error:")
                    .append(ret)
                    .append(" ")
                    .append(errorStr);

            setErrorCallBack(builder.toString());
        }
    }

    /**
     * 基本信息验证
     */
    private boolean baseVerify() {
        if (TextUtils.isEmpty(getAppId())) {
            setErrorCallBack("appid为空");
            return true;
        }
        return false;
    }

    /**
     * 是否安装qq
     *
     * @return true 是
     */
    public boolean isInstallQQ() {
        return mTencent.isQQInstalled(mActivity.get());
    }

    /**
     * 1.此处做了优化，点击聊天/语音/视频会去做token的校验
     * 2.根据情况进行自动登录并且回调
     * 3.回调后根据不同情况拉起不同业务
     * mChosenIMType = Constants.IM_AIO;
     * mChosenIMType = Constants.IM_AUDIO_CHAT;
     * mChosenIMType = Constants.IM_VIDEO_CHAT;
     *
     * @param qq 目标QQ号
     */
    private void doIm(int mChosenIMType, String qq) {
        // 调试的时候，使用mTencent.isSessionValid()，因为是当次的
        // 实际使用的时候，使用更加准确的mTencent.checkLogin()
        if (mTencent != null && mTencent.isSessionValid()) {
            // 拉起AIO
            // 如果还是失败,意味着token不为空且不过期，但是由于修改密码/被锁定等原因需重新授权，则需重新校验(暂时看看QQ这边是否会有这种情况)
            if (!TextUtils.isEmpty(qq)) {
                // 拉起会话
                jumpIMWithType(mChosenIMType, qq);
            } else {
                setErrorCallBack("QQ号为空，请重新设置");
            }
        } else {
            // 根据产品的更改，登录自己完成授权
            setErrorCallBack("请先登陆");
        }
    }

    /**
     * 根据传参拉起不同的IM业务
     *
     * @param type
     */
    private void jumpIMWithType(int type, String qq) {
        int ret = IM.IM_UNKNOWN_TYPE;
        if (type == Constants.IM_AIO) {
            ret = mTencent.startIMAio(mActivity.get(), qq, mActivity.get().getPackageName());
        } else if (type == Constants.IM_AUDIO_CHAT) {
            ret = mTencent.startIMAudio(mActivity.get(), qq, mActivity.get().getPackageName());
        } else if (type == Constants.IM_VIDEO_CHAT) {
            ret = mTencent.startIMVideo(mActivity.get(), qq, mActivity.get().getPackageName());
        }
        if (ret != IM.IM_SUCCESS) {
            setErrorCallBack("start IM conversation failed. error:" + ret);
        }
    }

    /**
     * 退出登陆
     */
    public void doLogOut() {
        if (mTencent != null && mTencent.isSessionValid()) {
            mTencent.logout(mActivity.get());//登录成功注销
        }
    }

    String accessToken;
    BaseUiListener baseUiListener = new BaseUiListener();

    public class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
//           "登录成功"
            setCompleteCallBack(o);
            try {
                JSONObject data = (JSONObject) o;
                String openID = data.getString("openid");
                accessToken = data.getString("access_token");
                String expires = data.getString("expires_in");

                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                //获取用户信息
//                UserInfo userInfo = new UserInfo(mActivity, mTencent.getQQToken());
//                userInfo.getUserInfo(iUiListener);

//                mTencent.logout(mActivity);//登录成功注销
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(UiError uiError) {
//           "登录失败"
            setErrorCallBack(uiError.errorMessage);
        }

        @Override
        public void onCancel() {
//            Toast.makeText(context, "取消登录", Toast.LENGTH_SHORT).show();
            setCancelCallBack();
        }

        @Override
        public void onWarning(int i) {

        }
    }

//    private IUiListener iUiListener = new IUiListener() {
//        @Override
//        public void onComplete(Object o) {
//            if (o != null) {
//                setCompleteCallBack(o);
//                Log.e("UserInfo", o.toString());
//                try {
//                    Log.e("用户名", ((JSONObject) o).getString("nickname"));
//                    Log.e("用户性别", ((JSONObject) o).getString("gender"));01
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
////                Toast.makeText(context, "QQ信息为空", Toast.LENGTH_LONG).show();
//                setErrorCallBack("QQ信息为空");
//            }
//        }
//
//        @Override
//        public void onError(UiError uiError) {
////            Toast.makeText(context, "授权QQ失败", Toast.LENGTH_LONG).show();
//            setErrorCallBack("授权QQ失败" + uiError.errorMessage);
//        }
//
//        @Override
//        public void onCancel() {
////            Toast.makeText(context, "取消登录", Toast.LENGTH_SHORT).show();
//            setCancelCallBack();
//        }
//    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
//        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
        Tencent.onActivityResultData(requestCode, resultCode, data, baseUiListener);
//        }
//        if(requestCode == Constants.REQUEST_API) {
//            if(resultCode == Constants.REQUEST_LOGIN) {
//                Tencent.handleResultData(data, baseUiListener);
//            }
//        }
//        if (resultCode == Constants.ACTIVITY_OK) {
//            if (requestCode == Constants.REQUEST_LOGIN) {
////                if (baseUiListener != null){ Tencent.onActivityResultData(requestCode, resultCode, data, baseUiListener);}
//        Tencent.onActivityResultData(requestCode, resultCode, data,baseUiListener);
//            }
////            else if (requestCode == Constants.REQUEST_OLD_SHARE) {
////                if (shareListener != null)
////                    Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
////            }
//
//        }
    }
}