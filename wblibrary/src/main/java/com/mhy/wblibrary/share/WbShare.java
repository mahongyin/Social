package com.mhy.wblibrary.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.mhy.socialcommon.ShareApi;
import com.mhy.socialcommon.ShareEntity;
import com.mhy.socialcommon.SocialType;
import com.mhy.wblibrary.WbSocial;
import com.mhy.wblibrary.bean.WbShareEntity;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MediaObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.StoryMessage;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 分享平台公共组件模块-微博分享
 */
public class WbShare extends ShareApi {
  private IWBAPI mWBAPI;

  /**
   * 执行登陆操作
   *
   * @param act activity
   * @param l   回调监听
   */
  public WbShare(Activity act, OnShareListener l) {
    super(act, l);
    mShareType = SocialType.WEIBO_Share;
    mActivity = act;
    setOnShareListener(l);
    if (cpuX86()) {
      return;
    }
    AuthInfo authInfo = new AuthInfo(act, WbSocial.getAppKy(), WbSocial.getRedirectUrl(), WbSocial.getScope());
    mWBAPI = WBAPIFactory.createWBAPI(act);
    mWBAPI.registerApp(act, authInfo);
    // mWBAPI.setLoggerEnable(true);

  }

  private boolean cpuX86() {
    String arch = System.getProperty("os.arch");
    String arc = null; // 大写
    if (arch != null) {
      arc = arch.toUpperCase();
      // 不让使用
      return arc.contains("X86");
    }
    return false;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    mWBAPI.doResultIntent(
            data,
            new WbShareCallback() {
              @Override
              public void onComplete() {
                callbackShareOk();
                //                Toast.makeText(ShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
              }

              @Override
              public void onError(UiError error) {
                callbackShareFail(error.errorMessage);
              }

              @Override
              public void onCancel() {
                callbackCancel();
              }
            });
  }

  /*基本信息验证*/
  private boolean baseVerify() {
    if (TextUtils.isEmpty(WbSocial.getAppKy()) || TextUtils.isEmpty(WbSocial.getRedirectUrl())) {
      callbackShareFail("请检查appid是否为空");
      return true;
    }
    return false;
  }

  /**
   * 分享 有客户端用客户端。没则网页
   *
   * @param shareEntity
   */
  @Override
  public void doShare(ShareEntity shareEntity) {
    if (cpuX86()) {
      return;
    }
    if (baseVerify()) {
      return;
    }

    WeiboMultiMessage weiboMessage = getShareMessage(shareEntity.getParams());
    if (weiboMessage == null) {
      return;
    }
    mWBAPI.shareMessage(weiboMessage, false);
  }

  /**
   * 分享 图片或视频故事 必须有客户端
   *
   * @param shareEntity
   */
  public void doShareStory(ShareEntity shareEntity) {
    if (cpuX86()) {
      return;
    }
    if (baseVerify()) {
      return;
    }
    StoryMessage message = getShareStoryMessage(shareEntity.getParams());
    if (message == null) {
      return;
    }
    mWBAPI.shareStory(message);
  }

//  /**
//   * @param textContent 文本内容
//   * @param description 网页描述
//   * @param bitmap 图片
//   * @param title 标题
//   * @param tagWebUrl 网页目标地址
//   * @param imageFiles 图集 ArrayList<Uri> list = new ArrayList<>(); //list.add(Uri.fromFile(newFile(getExternalFilesDir(null) + "/aaa.png")));
//   * @param videopath 视频文件路径
//   */
//  @Deprecated
//  private void doShare(
//      Context context,
//      String textContent,
//      String description,
//      Bitmap bitmap,
//      String title,
//      String tagWebUrl,
//      ArrayList<File> imageFiles,
//      String videopath) {
//    if (cpuX86()) {
//      return;
//    }
//    if (baseVerify(mShareListener)) {
//      return;
//    }
//    WeiboMultiMessage message = new WeiboMultiMessage();
//    TextObject textObject = new TextObject();
//    // 分享文字
//    if (!TextUtils.isEmpty(textContent)) {
//      textObject.text = textContent;
//      message.textObject = textObject;
//    }
//
//    // 分享图片
//    if (bitmap != null) {
//      ImageObject imageObject = new ImageObject();
//      // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//      // R.drawable.share_image);
//      imageObject.setImageData(bitmap);
//      message.imageObject = imageObject;
//    }
//
//    // 分享网页
//    if (!TextUtils.isEmpty(tagWebUrl)) {
//      WebpageObject webObject = new WebpageObject();
//      webObject.identify = UUID.randomUUID().toString();
//      webObject.title = title;
//      webObject.description = description;
//      if (null != bitmap) {
//        ByteArrayOutputStream os = null;
//        try {
//          os = new ByteArrayOutputStream();
//          bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os); // 指定可以将位图压缩为的已知格式
//          webObject.thumbData = os.toByteArray(); // 缩略图
//        } catch (Exception e) {
//          e.printStackTrace();
//        } finally {
//          try {
//            if (os != null) {
//              os.close();
//            }
//          } catch (IOException e) {
//            e.printStackTrace();
//          }
//        }
//      }
//      webObject.actionUrl = tagWebUrl;
//      webObject.defaultText = defautText;
//      message.mediaObject = webObject;
//    }
//
//    if (imageFiles != null) {
//      // 分享多图
//      MultiImageObject multiImageObject = new MultiImageObject();
//      ArrayList<Uri> list = new ArrayList<>();
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//        String authority = context.getPackageName() + ".fileprovider";
//        for (File imageFile : imageFiles) {
//          list.add(FileProvider.getUriForFile(context, authority, imageFile));
//        }
//      } else {
//        for (File imageFile : imageFiles) {
//          list.add(Uri.fromFile(imageFile));
//        }
//      }
//      multiImageObject.imageList = list;
//      message.multiImageObject = multiImageObject;
//      list.clear();
//      list = null;
//    }
//
//    if (!TextUtils.isEmpty(videopath)) {
//      // 分享视频
//      VideoSourceObject videoObject = new VideoSourceObject();
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//        File videoFile = new File(videopath);
//        if (!videoFile.getParentFile().exists()) {
//          videoFile.getParentFile().mkdir();
//        }
//        videoObject.videoPath =
//            FileProvider.getUriForFile(
//                context, context.getPackageName() + ".fileprovider", videoFile);
//      } else {
//        videoObject.videoPath = Uri.fromFile(new File(videopath));
//      }
//      //      videoObject.videoPath = Uri.fromFile(new File(videopath));
//      message.videoSourceObject = videoObject;
//    }
//
//    mWBAPI.shareMessage(message, false);
//  }

  private StoryMessage getShareStoryMessage(Bundle params) {
    StoryMessage message = new StoryMessage();
    int type = params.getInt(WbShareEntity.KEY_WB_TYPE);
    if (type == WbShareEntity.TYPE_IMG_STORY) {
      File picFile = new File(params.getString(WbShareEntity.KEY_WB_IMG_LOCAL));
      message.setImageUri(Uri.fromFile(picFile));
    } else if (type == WbShareEntity.TYPE_VIDEO_STORY) {
      File videoFile = new File(params.getString(WbShareEntity.KEY_WB_VIDEO_URL));
      message.setVideoUri(Uri.fromFile(videoFile));
    } else {
      return null;
    }
    return message;
  }

  private WeiboMultiMessage getShareMessage(Bundle params) {
    WeiboMultiMessage msg = new WeiboMultiMessage();
    int type = params.getInt(WbShareEntity.KEY_WB_TYPE);
    MediaObject mediaObject = null;
    switch (type) {
      case WbShareEntity.TYPE_TEXT:
        msg.textObject = getTextObj(params);
        mediaObject = msg.textObject;
        break;
      case WbShareEntity.TYPE_IMG_TEXT:
        msg.imageObject = getImageObj(params);
        msg.textObject = getTextObj(params);
        mediaObject = msg.imageObject;
        break;
      case WbShareEntity.TYPE_MULTI_IMAGES:
        msg.multiImageObject = getMultiImgObj(params);
        msg.textObject = getTextObj(params);
        mediaObject = msg.multiImageObject;
        break;
      case WbShareEntity.TYPE_VIDEO:
        msg.videoSourceObject = getVideoObj(params);
        msg.textObject = getTextObj(params);
        mediaObject = msg.videoSourceObject;
        break;
      case WbShareEntity.TYPE_WEB:
        msg.mediaObject = getWebPageObj(params);
        msg.textObject = getTextObj(params);
        mediaObject = msg.mediaObject;
        break;
    }
    if (mediaObject == null) {
      return null;
    }
    return msg;
  }

  private TextObject getTextObj(Bundle params) {
    TextObject textObj = new TextObject();
    textObj.text = params.getString(WbShareEntity.KEY_WB_TEXT);
    return textObj;
  }

  /**
   * 单图
   *
   * @param params bitmap 大小限制 压缩尺寸
   * @return
   */
  private ImageObject getImageObj(Bundle params) {
    ImageObject imgObj = new ImageObject();
    if (params.containsKey(WbShareEntity.KEY_WB_IMG_LOCAL)) { // 分为本地文件和应用内资源图片
      String imgUrl = params.getString(WbShareEntity.KEY_WB_IMG_LOCAL);
      if (notFoundFile(imgUrl)) {
        return null;
      }
      imgObj.imagePath = imgUrl;
    } else {
      try {
        Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), params.getInt(WbShareEntity.KEY_WB_IMG_RES));
        imgObj.setImageData(bitmap);
        bitmap.recycle();
      } catch (Exception e) {
        e.printStackTrace();
        Bitmap bitmap = readBitMap(mActivity, params.getInt(WbShareEntity.KEY_WB_IMG_RES));
        imgObj.setImageData(bitmap);
        bitmap.recycle();
      }
    }
    return imgObj;
  }

  /**
   * 以最省内存的方式读取本地资源的图片
   *
   * @param context
   * @param resId
   * @return 最后记得 bitmap.recycle();
   */
  public static Bitmap readBitMap(Context context, int resId) {
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inPreferredConfig = Bitmap.Config.RGB_565; // Bitmap.Config.ARGB_8888
    opt.inPurgeable = true; // 允许可清除
    opt.inInputShareable = true; // 以上options的两个属性必须联合使用才会有效果
    // 获取资源图片
    InputStream is = context.getResources().openRawResource(resId);
    return BitmapFactory.decodeStream(is, null, opt);
  }

  /**
   * 多图
   *
   * @param params
   * @return
   */
  private MultiImageObject getMultiImgObj(Bundle params) {
    MultiImageObject multiImageObject = new MultiImageObject();
    ArrayList<String> images = params.getStringArrayList(WbShareEntity.KEY_WB_MULTI_IMG);
    ArrayList<Uri> uris = new ArrayList<>();
    if (images != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        String authority = mActivity.getPackageName() + ".com.fileprovider";
        for (String image : images) {
          uris.add(FileProvider.getUriForFile(mActivity, authority, new File(image)));
        }
      } else {
        for (String image : images) {
          uris.add(Uri.fromFile(new File(image)));
        }
      }
    }
    multiImageObject.imageList = uris;
//    uris.clear();
    if (addTitleSummaryAndThumb(multiImageObject, params)) {
      return null;
    }
    return multiImageObject;
  }

  private VideoSourceObject getVideoObj(Bundle params) {
    VideoSourceObject videoSourceObject = new VideoSourceObject();
    String videoUrl = params.getString(WbShareEntity.KEY_WB_VIDEO_URL);
    if (!TextUtils.isEmpty(videoUrl)) {
//      videoSourceObject.videoPath = Uri.fromFile(new File(videoUrl));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        File videoFile = new File(videoUrl);
        if (!videoFile.getParentFile().exists()) {
          videoFile.getParentFile().mkdir();
        }
        videoSourceObject.videoPath = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".com.fileprovider", videoFile);
      } else {
        videoSourceObject.videoPath = Uri.fromFile(new File(videoUrl));
      }
    }

    if (params.containsKey(WbShareEntity.KEY_WB_IMG_LOCAL)) {
      String coverPath = params.getString(WbShareEntity.KEY_WB_IMG_LOCAL);
      if (!TextUtils.isEmpty(coverPath)) {
        videoSourceObject.coverPath = Uri.fromFile(new File(coverPath));
      }
    }
    return videoSourceObject;
  }

  private String defautText = "分享网页";

  private WebpageObject getWebPageObj(Bundle params) {
    WebpageObject webpageObject = new WebpageObject();
    webpageObject.identify = UUID.randomUUID().toString();
    webpageObject.actionUrl = params.getString(WbShareEntity.KEY_WB_WEB_URL);
    webpageObject.defaultText = defautText; // add"分享网页"
    //标题//缩略图//描述 在这里
    if (addTitleSummaryAndThumb(webpageObject, params)) {
      return null;
    }
    return webpageObject;
  }

  /**
   * 当有设置缩略图但是找不到的时候阻止分享
   */
  private boolean addTitleSummaryAndThumb(MediaObject msg, Bundle params) {
    if (params.containsKey(WbShareEntity.KEY_WB_TITLE)) {
      msg.title = params.getString(WbShareEntity.KEY_WB_TITLE);
    }

    if (params.containsKey(WbShareEntity.KEY_WB_SUMMARY)) {
      msg.description = params.getString(WbShareEntity.KEY_WB_SUMMARY);
    }

    if (params.containsKey(WbShareEntity.KEY_WB_IMG_LOCAL) || params.containsKey(WbShareEntity.KEY_WB_IMG_RES)) {
      Bitmap bitmap;
      if (params.containsKey(WbShareEntity.KEY_WB_IMG_LOCAL)) { // 分为本地文件和应用内资源图片
        String imgUrl = params.getString(WbShareEntity.KEY_WB_IMG_LOCAL);
        if (notFoundFile(imgUrl)) {
          return true;
        }
        bitmap = BitmapFactory.decodeFile(imgUrl);
      } else {
        try {
          bitmap = BitmapFactory.decodeResource(mActivity.getResources(), params.getInt(WbShareEntity.KEY_WB_IMG_RES));
        } catch (Exception e) {
          e.printStackTrace();
          bitmap = readBitMap(mActivity, params.getInt(WbShareEntity.KEY_WB_IMG_RES));
        }
      }
      msg.thumbData = bmpToByteArray(bitmap, true);
    }
    return false;
  }

  //搞缩略图
  private byte[] bmpToByteArray(final Bitmap bmp, boolean needThumb) {
//      if (null != bitmap) {
//        ByteArrayOutputStream os = null;
//        try {
//          os = new ByteArrayOutputStream();
//          bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os); // 指定可以将位图压缩为的已知格式
//          webObject.thumbData = os.toByteArray(); // 缩略图
//        } catch (Exception e) {
//          e.printStackTrace();
//        } finally {
//          try {
//            if (os != null) {
//              os.close();
//            }
//          } catch (IOException e) {
//            e.printStackTrace();
//          }
//        }
//      }

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

  private boolean notFoundFile(String filePath) {
    if (!TextUtils.isEmpty(filePath)) {
      File file = new File(filePath);
      if (!file.exists()) {
        if (mShareListener != null) {
          callbackShareFail("文件没找到");
        }
        return true;
      }
    } else {
      if (mShareListener != null) {
        callbackShareFail("文件没找到");
      }
      return true;
    }
    return false;
  }
}
