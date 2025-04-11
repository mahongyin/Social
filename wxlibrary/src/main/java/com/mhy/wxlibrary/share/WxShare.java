package com.mhy.wxlibrary.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.mhy.socialcommon.ShareApi;
import com.mhy.socialcommon.ShareEntity;
import com.mhy.socialcommon.ShareUtil;
import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.WxSocial;
import com.mhy.wxlibrary.bean.WxShareEntity;
import com.mhy.wxlibrary.wxapi.BaseWXActivity;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;


/**
 * 分享平台公共组件模块-微信分享
 */
public class WxShare extends ShareApi {
    private IWXAPI mWXApi;

    /**
     * 执行登陆操作
     *
     * @param act      activity
     * @param listener 回调监听
     */
    public WxShare(Activity act, OnShareListener listener) {
        super(act, listener);
        if (mWXApi == null) {
//            mWXApi = WXAPIFactory.createWXAPI(mActivity.get(), getAppId(), true);
//            mWXApi.registerApp(getAppId());
            mWXApi = WxSocial.getInstance().getWXApi();
        }
        BaseWXActivity.wxShare = this;
    }

    @Override
    protected String getAppId() {
        return WxSocial.getInstance().getWxAppId();
    }

    /**
     * 基本信息验证
     *
     * @return true 拦截
     */
    private boolean baseVerify() {
        if (TextUtils.isEmpty(getAppId())) {
            callbackShareFail("appid为空");
            return true;
        }
        return false;
    }

    /**
     * 分享
     *
     * @param mShareContent 分享内容包装
     */
    @Override
    public void doShare(ShareEntity mShareContent) {
        if (null == mShareContent) {
            return;
        }
        mShareType = mShareContent.getType();
        if (baseVerify()) {
            return;
        }
        //在这init?
        if (!mWXApi.isWXAppInstalled()) {
            callbackShareFail("微信未安装");
            return;
        }

        //是否分享到朋友圈，微信4.2以下不支持朋友圈
        boolean isTimeLine = mShareType == SocialType.WEIXIN_TimeLine_Share;
        if (isTimeLine && mWXApi.getWXAppSupportAPI() < Build.TIMELINE_SUPPORTED_SDK_INT /*0x21020001*/) {
            callbackShareFail("微信版本过低，不支持分享朋友圈");
            return;
        }
        boolean isCollection = mShareType == SocialType.WEIXIN_Favorite;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = createMessage(req, mShareContent.getParams());
        if (req.message == null) {
            return;
        }
        req.scene = isTimeLine ? SendMessageToWX.Req.WXSceneTimeline : isCollection ? SendMessageToWX.Req.WXSceneFavorite : SendMessageToWX.Req.WXSceneSession;
        mWXApi.sendReq(req);
    }

    //返回 contentUri.toString() 即是以"content://"开头的用于共享的路径
    private String getFileUri(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        Uri contentUri = FileProvider.getUriForFile(mActivity.get(),
                mActivity.get().getPackageName() + ".social.fileprovider",  /* 要与`AndroidManifest.xml`里配置的`authorities`一致*/
                file);
        // 授权给微信访问路径
        mActivity.get().grantUriPermission(ShareUtil.package_wx,  /* 这里填微信包名*/
                contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return contentUri.toString();
    }

    // 判断微信版本是否为7.0.13及以上
    private boolean checkVersionValid(IWXAPI api) {
        //微信版本>0x27000D00 才支持FileProvder
        return api.getWXAppSupportAPI() >= 0x27000D00;
    }

    private String buildTransaction(final String type) {
        return type == null ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * bitmap.recycle()
     */
    private byte[] bmpToByteArray(final Bitmap bmp, boolean needThumb) {
        Bitmap newBmp;
        if (needThumb) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            if (width > height) {
                height = height * 150 / width;
                width = 150;
            } else {
                width = width * 150 / height;
                height = 150;
            }
            newBmp = Bitmap.createScaledBitmap(bmp, width, height, true);
        } else {
            newBmp = bmp;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        newBmp.compress(Bitmap.CompressFormat.JPEG, 100, output);

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!bmp.isRecycled()) {
                bmp.recycle();
            }
            if (!newBmp.isRecycled()) {
                newBmp.recycle();
            }
        }
        return result;
    }

    /**
     * 构建消息体
     *
     * @param req
     * @param params
     * @return
     */
    private WXMediaMessage createMessage(SendMessageToWX.Req req, Bundle params) {
        WXMediaMessage msg = new WXMediaMessage();
        int type = params.getInt(WxShareEntity.KEY_WX_TYPE);
        boolean success = false;
        switch (type) {
            case WxShareEntity.TYPE_TEXT:
                success = addText(req, msg, params);
                break;
            case WxShareEntity.TYPE_IMG:
                success = addImage(req, msg, params);
                break;
            case WxShareEntity.TYPE_MUSIC:
                success = addMusic(req, msg, params);
                break;
            case WxShareEntity.TYPE_VIDEO:
                success = addVideo(req, msg, params);
                break;
            case WxShareEntity.TYPE_WEB:
                success = addWeb(req, msg, params);
                break;
            case WxShareEntity.TYPE_MINI_APP:
                success = addMiniApp(req, msg, params);
                break;
            case WxShareEntity.TYPE_MUSIC_VIDEO:
                success = addMusicVideo(req, msg, params);
                break;
            default:
                break;
        }
        if (!success) {
            return null;
        }
        return msg;
    }

    // 支持会话、朋友圈、收藏
    private boolean addMusicVideo(SendMessageToWX.Req req, WXMediaMessage msg, Bundle params) {
        if (mWXApi.getWXAppSupportAPI() < Build.SUPPORT_SEND_MUSIC_VIDEO_MESSAGE) {
            callbackShareFail("微信版本过低，不支持音乐视频分享");
            return false;
        }
        WXMusicVideoObject musicVideo = new WXMusicVideoObject();
        String musicVideoUrl = params.getString(WxShareEntity.KEY_WX_MUSIC_URL,"");
        String webUrl = params.getString(WxShareEntity.KEY_WX_WEB_URL,"");
        musicVideo.musicUrl = webUrl;	 //必填 音乐url
        musicVideo.musicDataUrl = musicVideoUrl; //必填 音乐音频url
        musicVideo.songLyric = params.getString(WxShareEntity.KEY_WX_MUSIC_LRC,"");// 歌词信息 LRC格式 注意：限制长度不超过32K
        musicVideo.hdAlbumThumbFilePath = params.getString(WxShareEntity.KEY_WX_IMG_LOCAL,"");	 // 专辑图本地文件路径
        musicVideo.singerName = params.getString(WxShareEntity.KEY_WX_SUMMARY); //必填 歌手名称
        musicVideo.albumName = params.getString(WxShareEntity.KEY_WX_TEXT);//专辑名称
        //musicVideo.musicGenre = "流行歌曲";//流派
        //musicVideo.issueDate = 1610713585;//发行日期
        musicVideo.identification = String.valueOf(musicVideoUrl.hashCode());//分享歌曲的识别符 注意：限制长度不超过1K，从微信跳回应用时会带
        musicVideo.duration = params.getInt(WxShareEntity.KEY_WX_DURATION,0);//必填 单位为毫秒

        msg.mediaObject = musicVideo;
        msg.title = params.getString(WxShareEntity.KEY_WX_TITLE);	// 歌曲名称 必填，不能为空
        msg.description = params.getString(WxShareEntity.KEY_WX_SUMMARY);  // 选填，建议与歌手名字段 singerName 保持一致
        msg.messageExt = webUrl;   // 微信跳回应用时会带上 wxapi onResp()

        Bitmap bitmap = null;
        if (params.containsKey(WxShareEntity.KEY_WX_IMG_LOCAL)) {
            //分为本地文件
            String imgUrl = params.getString(WxShareEntity.KEY_WX_IMG_LOCAL);
//            if (TextUtils.isEmpty(imgUrl = notFoundFile(imgUrl))) {
//                return false;
//            }
            bitmap = BitmapFactory.decodeFile(imgUrl);
        } else {
            //应用内资源图片
            bitmap = BitmapFactory.decodeResource(mActivity.get().getResources(), params.getInt(WxShareEntity.KEY_WX_IMG_RES));
        }

        if (bitmap != null) {// 兼容旧版本节点的图片，小于32k，新版本优先
            //msg.setThumbImage(bitmap);
            msg.thumbData = bmpToByteArray(bitmap, true); // 音乐卡片缩略图，不超过64KB
            bitmap.recycle();
        }

        req.transaction = buildTransaction("musicVideo");

        return true;
    }

    /**
     * 分享小程序 卡片 目前不支持朋友圈
     *
     * @param req
     * @param message
     * @param params
     * @return
     */
    private boolean addMiniApp(SendMessageToWX.Req req, WXMediaMessage message, Bundle params) {
        if (mWXApi.getWXAppSupportAPI() < Build.MINIPROGRAM_SUPPORTED_SDK_INT) {
            callbackShareFail("微信版本过低，不支持小程序分享");
            return false;
        }
        // 第二种：App 主动分享小程序卡片：⚠️ 小程序测试版不能分享
        WXMiniProgramObject wxObject = new WXMiniProgramObject();
        //miniprogramType	int	小程序的类型，默认正式版
        //正式版: WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
        //测试版: WXMiniProgramObject.MINIPROGRAM_TYPE_TEST;
        //预览版: WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW
        wxObject.miniprogramType = params.getInt(WxShareEntity.KEY_WX_MINI_TYPE);// 正式版:0，测试版:1，体验版:2
        wxObject.webpageUrl = params.getString(WxShareEntity.KEY_WX_WEB_URL); //兼容低版本的网络链接 限制长度不超过 10KB
        wxObject.userName = params.getString(WxShareEntity.KEY_WX_MINI_APPID);//小程序的原始ID
        wxObject.path = params.getString(WxShareEntity.KEY_WX_MINI_PATH);// 指定打开小程序的某一个页面的URL路径 path	小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
        // wxminiObiect.hdImageData =  hdImageData; //小程序节点高清大图，小于128K
        wxObject.withShareTicket = true;//boolean是否使用带 shareTicket 的分享	通常开发者希望分享出去的小程序被二次打开时可以获取到更多信息，例如群的标识。可以设置 withShareTicket 为 true，当分享卡片在群聊中被其他用户打开时，可以获取到 shareTicket，用于获取更多分享信息。详见小程序获取更多分享信息 ，最低客户端版本要求：6.5.13
        message.mediaObject = wxObject;
        message.title = params.getString(WxShareEntity.KEY_WX_TITLE);// 小程序消息title
        message.description = params.getString(WxShareEntity.KEY_WX_SUMMARY);// 小程序消息desc

        Bitmap bitmap = null;
        if (params.containsKey(WxShareEntity.KEY_WX_IMG_LOCAL)) {
            //分为本地文件
            String imgUrl = params.getString(WxShareEntity.KEY_WX_IMG_LOCAL);
//            if (TextUtils.isEmpty(imgUrl = notFoundFile(imgUrl))) {
//                return false;
//            }
            bitmap = BitmapFactory.decodeFile(imgUrl);
        } else {
            //应用内资源图片
            bitmap = BitmapFactory.decodeResource(mActivity.get().getResources(), params.getInt(WxShareEntity.KEY_WX_IMG_RES));
        }

        if (bitmap != null) {// 兼容旧版本节点的图片，小于32k，新版本优先
            //msg.setThumbImage(bitmap);
            message.thumbData = bmpToByteArray(bitmap, true); // 小程序消息封面图片，小于128k
            bitmap.recycle();
        }

        req.transaction = buildTransaction("miniProgram");

        return true;
    }

    private boolean addText(SendMessageToWX.Req req, WXMediaMessage msg, Bundle params) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = params.getString(WxShareEntity.KEY_WX_TEXT);

        msg.mediaObject = textObj;
        msg.description = textObj.text;

        req.transaction = buildTransaction("text");
        return true;
    }

    private boolean addImage(SendMessageToWX.Req req, WXMediaMessage msg, Bundle params) {
        WXImageObject imgObj;
        Bitmap bitmap;
        if (params.containsKey(WxShareEntity.KEY_WX_IMG_LOCAL)) {
            //分为本地文件
            String imgUrl = params.getString(WxShareEntity.KEY_WX_IMG_LOCAL);
            if (TextUtils.isEmpty(imgUrl = notFoundFile(imgUrl))) {
                return false;
            }
            imgObj = new WXImageObject();
            imgObj.imagePath = imgUrl;
            bitmap = BitmapFactory.decodeFile(imgUrl);
        } else {
            //应用内资源图片
            bitmap = BitmapFactory.decodeResource(mActivity.get().getResources(), params.getInt(WxShareEntity.KEY_WX_IMG_RES));
            imgObj = new WXImageObject(bitmap);
        }
        msg.mediaObject = imgObj;
        if (bitmap != null) {
            //msg.setThumbImage(bitmap);
            msg.thumbData = bmpToByteArray(bitmap, true);
            bitmap.recycle();
        }
        req.transaction = buildTransaction("img");
        return true;
    }

    private boolean addMusic(SendMessageToWX.Req req, WXMediaMessage msg, Bundle params) {
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicUrl = params.getString(WxShareEntity.KEY_WX_MUSIC_URL);

        msg.mediaObject = musicObject;
        if (addTitleSummaryAndThumb(msg, params)) {
            return false;
        }

        req.transaction = buildTransaction("music");
        //req.userOpenId = getOpenId();
        return true;
    }

    private boolean addVideo(SendMessageToWX.Req req, WXMediaMessage msg, Bundle params) {
        WXVideoObject musicObject = new WXVideoObject();
        musicObject.videoUrl = params.getString(WxShareEntity.KEY_WX_VIDEO_URL);
        //videoUrl 和 videoLowBandUrl 不能同时为空，同时存在时 videoUrl 优先级高于 videoLowBandUrl:供低带宽的环境下使用的视频链接
        msg.mediaObject = musicObject;
        if (addTitleSummaryAndThumb(msg, params)) {
            return false;
        }
        req.transaction = buildTransaction("video");
        //req.userOpenId = getOpenId();
        return true;
    }

    private boolean addWeb(SendMessageToWX.Req req, WXMediaMessage msg, Bundle params) {
        WXWebpageObject musicObject = new WXWebpageObject();
        musicObject.webpageUrl = params.getString(WxShareEntity.KEY_WX_WEB_URL);

        msg.mediaObject = musicObject;
        if (addTitleSummaryAndThumb(msg, params)) {
            return false;
        }

        req.transaction = buildTransaction("webpage");
        //req.userOpenId = getOpenId();
        return true;
    }

    /**
     * 添加标题、描述、缩略图
     */
    private boolean addTitleSummaryAndThumb(WXMediaMessage msg, Bundle params) {
        if (params.containsKey(WxShareEntity.KEY_WX_TITLE)) {
            msg.title = params.getString(WxShareEntity.KEY_WX_TITLE);
        }

        if (params.containsKey(WxShareEntity.KEY_WX_SUMMARY)) {
            msg.description = params.getString(WxShareEntity.KEY_WX_SUMMARY);
        }

        if (params.containsKey(WxShareEntity.KEY_WX_IMG_LOCAL) || params.containsKey(WxShareEntity.KEY_WX_IMG_RES)) {
            Bitmap bitmap;
            if (params.containsKey(WxShareEntity.KEY_WX_IMG_LOCAL)) {
                //分为本地文件和应用内资源图片
                String imgUrl = params.getString(WxShareEntity.KEY_WX_IMG_LOCAL);
//                if (TextUtils.isEmpty(imgUrl = notFoundFile(imgUrl))) {
//                    return true;
//                }
                bitmap = BitmapFactory.decodeFile(imgUrl);
            } else {
                bitmap = BitmapFactory.decodeResource(mActivity.get().getResources(), params.getInt(WxShareEntity.KEY_WX_IMG_RES));
            }
            if (bitmap != null) {
                //msg.setThumbImage(bitmap);
                msg.thumbData = bmpToByteArray(bitmap, true);
                bitmap.recycle();
            }
        }
        return false;
    }

    private String notFoundFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
                return filePath;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                callbackShareFail("文件未找到");
                return "";
            }
            // 判断Android版本是否7.0及以上 才支持fpovider
            if (checkVersionValid(mWXApi) && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                String contentPath = getFileUri(file);
                // 使用contentPath作为文件路径进行分享
                return contentPath;
            } else {
                // 使用原有方式传递文件路径进行分享
                return filePath;
            }
        } else {
            callbackShareFail("文件未找到");
            return "";
        }
    }
}
