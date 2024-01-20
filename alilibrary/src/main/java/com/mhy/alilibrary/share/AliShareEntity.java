package com.mhy.alilibrary.share;

import androidx.annotation.DrawableRes;

import com.mhy.socialcommon.ShareEntity;
import com.mhy.socialcommon.SocialType;
import com.mhy.socialcommon.annotation.ParamsRequired;

/**
 * Function：
 * Desc：
 */
public final class AliShareEntity extends ShareEntity {

    public static final String TYPE = "ali_share";
    public static final String TYPE_WEB = "web";
    public static final String TYPE_TEXT = "text";
    public static final String IMG_URL = "image_url";
    public static final String IMG_PATH = "image_path";
    public static final String IMG_RES = "image_res";
    public static final String WEB_URL = "web_url";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    private AliShareEntity(SocialType type) {
        super(type);
    }

    /**
     * 创建分享图文类型到qq
     *
     * @param title     标题，长度限制30个字符
     * @param targetUrl 跳转地址
     * @param imgUrl    图片地址，本地路径或者url
     * @param summary   摘要，长度限制40个字
     */
    public static ShareEntity createWeb(@ParamsRequired String title, String summary, String imgUrl,
                                        @ParamsRequired String targetUrl) {
        ShareEntity entity = createText(summary);
        addParams(entity.params, TYPE, TYPE_WEB);
        addParams(entity.params, TITLE, title);
        addParams(entity.params, WEB_URL, targetUrl);
        addParams(entity.params, IMG_URL, imgUrl);
        addParams(entity.params, SUMMARY, summary);

        return entity;
    }

    /**
     * 创建分享纯图片到qq 纯图分享只支持本地图片
     *
     * @param localImgPath 本地图片地址
     */
    public static ShareEntity createImagePath(@ParamsRequired String localImgPath) {
        ShareEntity entity = new ShareEntity(SocialType.ALIPAY_Share);
        addParams(entity.params, TYPE, IMG_PATH);
        addParams(entity.params, IMG_PATH, localImgPath);
        return entity;
    }

    public static ShareEntity createImageUrl(@ParamsRequired String ImgUrl) {
        ShareEntity entity = new ShareEntity(SocialType.ALIPAY_Share);
        addParams(entity.params, TYPE, IMG_URL);
        addParams(entity.params, IMG_URL, ImgUrl);
        return entity;
    }

    public static ShareEntity createImageRes(@ParamsRequired @DrawableRes int resImg) {
        ShareEntity entity = new ShareEntity(SocialType.ALIPAY_Share);
        addParams(entity.params, TYPE, IMG_RES);
        addParams(entity.params, IMG_RES, resImg);
        return entity;
    }

    /**
     * 文本信息分享
     */
    public static ShareEntity createText(@ParamsRequired String content) {
        ShareEntity entity = new ShareEntity(SocialType.ALIPAY_Share);
        addParams(entity.params, TYPE, TYPE_TEXT);
        addParams(entity.params, SUMMARY, content);
        return entity;
    }
}
