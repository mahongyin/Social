package com.mhy.alilibrary.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.DrawableRes;

import com.mhy.socialcommon.ShareApi;
import com.mhy.socialcommon.ShareEntity;
import com.mhy.socialcommon.ShareUtil;
import com.mhy.socialcommon.SocialType;

import java.io.File;

/**
 * Created By Mahongyin
 * Date    2024/1/19 23:49
 */
public class AliShare extends ShareApi {
    @Override
    protected String getAppId() {
        return "";
    }
    public AliShare(Activity act, OnShareListener l) {
        super(act, l);
        mShareType = SocialType.ALIPAY_Share;

    }

    @Override
    public void doShare(ShareEntity content) {
        if (content == null) {
            return;
        }
        String type = content.params.getString(AliShareEntity.TYPE);
        String imageurl;
        String imagepath;
        String tageturl;
        int image;
        String title;
        if (type != null) {
            switch (type) {
                case AliShareEntity.IMG_RES:
                    image = content.params.getInt(AliShareEntity.IMG_RES);
                    sendByteImage(image);
                    break;
                case AliShareEntity.IMG_PATH:
                    imagepath = content.params.getString(AliShareEntity.IMG_PATH);
                    sendLocalImage(imagepath);
                    break;
                case AliShareEntity.IMG_URL:
                    imageurl = content.params.getString(AliShareEntity.IMG_URL);
                    sendOnlineImage(imageurl);
                    break;
                case AliShareEntity.TYPE_WEB:
                    tageturl = content.params.getString(AliShareEntity.WEB_URL);
                    title = content.params.getString(AliShareEntity.TITLE);
                    imageurl = content.params.getString(AliShareEntity.IMG_URL);
                    String description = content.params.getString(AliShareEntity.SUMMARY);
                    ShareUtil.getInstance(mActivity.get()).shareText(description+" "+tageturl, ShareUtil.package_ali);
                    break;
                case AliShareEntity.TYPE_TEXT:
                    String summary = content.params.getString(AliShareEntity.SUMMARY);
                    ShareUtil.getInstance(mActivity.get()).shareText(summary, ShareUtil.package_ali);
                    break;
                default:
                    break;
            }
        }

    }

    private void sendByteImage(@DrawableRes int id) {
        Bitmap bmp = BitmapFactory.decodeResource(mActivity.get().getResources(), id);
        ShareUtil.getInstance(mActivity.get()).shareBitMapImg(bmp, ShareUtil.package_ali);
        bmp.recycle();
    }

    private void sendOnlineImage(String imgurl) {
        ShareUtil.getInstance(mActivity.get()).shareText(imgurl, ShareUtil.package_ali);
    }

    private void sendLocalImage(String path) {
        File file = new File(path);
        if (!file.exists()) {
            callbackShareFail("选择的文件不存在");
            return;
        }
        ShareUtil.getInstance(mActivity.get()).shareImg(path, ShareUtil.package_ali);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
