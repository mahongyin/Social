package com.mhy.wblibrary.bean;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mhy.socialcommon.ShareEntity;
import com.mhy.socialcommon.SocialType;

import java.util.ArrayList;

/**
 *
 */
public class WbShareEntity extends ShareEntity {

    public static final String KEY_WB_TYPE = "key_wb_type";
    /**
     * 依次为：文本，图片，多图，音乐，视频，网页
     */
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMG = 10;
    public static final int TYPE_IMG_TEXT = 1;
    public static final int TYPE_MULTI_IMAGES = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_WEB = 4;
    public static final int TYPE_IMG_STORY = 5;
    public static final int TYPE_VIDEO_STORY = 6;
    /**
     * 超话
     */
    public static final int TYPE_SUPER_TALK = 7;

    public static final String KEY_WB_TITLE = "key_wb_title";
    public static final String KEY_WB_SUMMARY = "key_wb_summary";
    public static final String KEY_WB_TEXT = "key_wb_text";
    public static final String KEY_WB_IMG_LOCAL = "key_wb_local_img";
    public static final String KEY_WB_IMG_RES = "key_wb_img_res";
    public static final String KEY_WB_MULTI_IMG = "key_wb_multi_img";
    public static final String KEY_WB_VIDEO_URL = "key_wb_video_url";
    public static final String KEY_WB_WEB_URL = "key_wb_web_url";

    public static final String KEY_WB_SUPER_SG = "key_wb_super_sg";
    public static final String KEY_WB_SUPER_SEC = "key_wb_super_sec";
    public static final String KEY_WB_SUPER_SG_EXT = "key_wb_super_sp_ext";
    ;

    private WbShareEntity(SocialType type) {
        super(type);
    }

    /**
     * @param text 分享文本内容
     */
    public static ShareEntity createText(String text) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_TEXT);
        addParams(entity.params, KEY_WB_TEXT, text);
        return entity;
    }

    /**
     * 分享图文
     *
     * @param img  本地图片
     * @param text 文本内容
     */
    public static ShareEntity createImageText(String img, String text) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_IMG_TEXT);
        addParams(entity.params, KEY_WB_TEXT, text);
        addTitleSummaryAndThumb(entity.params, "", "", img);
        return entity;
    }

    /**
     * 分享图文
     *
     * @param img  应用内图片资源
     * @param text 文本内容
     */
    public static ShareEntity createImageText(@DrawableRes int img, String text) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_IMG_TEXT);
        addParams(entity.params, KEY_WB_TEXT, text);
        addTitleSummaryAndThumb(entity.params, "", "", img);
        return entity;
    }

    public static ShareEntity createImage(String img) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_IMG);
        addTitleSummaryAndThumb(entity.params, "", "", img);
        return entity;
    }

    public static ShareEntity createImage(@DrawableRes int img) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_IMG);
        addTitleSummaryAndThumb(entity.params, "", "", img);
        return entity;
    }

    /**
     * 分享图story
     *
     * @param imgPath 本地图片绝对路径
     */
    public static ShareEntity createImageStory(String imgPath) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_IMG_STORY);
        addParams(entity.params, KEY_WB_IMG_LOCAL, imgPath);
        return entity;
    }

    /**
     * 分享视频story
     *
     * @param videoPath 本地视频绝对路径
     */
    public static ShareEntity createVideoStory(String videoPath) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_VIDEO_STORY);
        addParams(entity.params, KEY_WB_VIDEO_URL, videoPath);
        return entity;
    }

    /**
     * 分享多图
     *
     * @param imagePaths 图片路径List，最多9张
     * @param text       文本内容
     */
    public static ShareEntity createMultiImage(ArrayList<String> imagePaths, String text) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_MULTI_IMAGES);
        addParams(entity.params, KEY_WB_MULTI_IMG, imagePaths);
        addParams(entity.params, KEY_WB_TEXT, text);
        return entity;
    }

    /**
     * 分享视频
     *
     * @param videoPath  视频路径，本地视频
     * @param coverImage 视频封面
     * @param text       文本内容
     */
    public static ShareEntity createVideo(String text, String videoPath, @Nullable String coverImage) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_VIDEO);
        addParams(entity.params, KEY_WB_VIDEO_URL, videoPath);
        addParams(entity.params, KEY_WB_TEXT, text);
        if (TextUtils.isEmpty(coverImage)) {
            return entity;
        }
        addTitleSummaryAndThumb(entity.params, "", "", coverImage);
        return entity;
    }

    /**
     * 分享网页
     *
     * @param webUrl  网页链接
     * @param title   网页标题
     * @param summary 网页摘要
     * @param imgPath 网页左边图标，本地路径
     * @param text    文本内容
     */
    public static ShareEntity createWeb(String webUrl, String title, String summary, String imgPath, String text) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_WEB);
        addParams(entity.params, KEY_WB_WEB_URL, webUrl);
        addParams(entity.params, KEY_WB_TEXT, text);
        addTitleSummaryAndThumb(entity.params, title, summary, imgPath);
        return entity;
    }

    /**
     * 分享网页
     *
     * @param webUrl  网页链接
     * @param title   网页标题
     * @param summary 网页摘要
     * @param imgRes  网页左边图标，应用内图片资源
     * @param text    文本内容
     */
    public static ShareEntity createWeb(String webUrl, String title, String summary, @DrawableRes int imgRes, String text) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_WEB);
        addParams(entity.params, KEY_WB_WEB_URL, webUrl);
        addParams(entity.params, KEY_WB_TEXT, text);
        addTitleSummaryAndThumb(entity.params, title, summary, imgRes);
        return entity;
    }

    /**
     * 创建超话
     *
     * @param title
     * @param sgName
     * @param sgExtParam
     * @param secName
     * @return
     */
    public static ShareEntity createSuperTalk(String title, @NonNull String sgName, @NonNull String secName, @NonNull String sgExtParam) {
        ShareEntity entity = new ShareEntity(SocialType.WEIBO_Share);
        addParams(entity.params, KEY_WB_TYPE, TYPE_WEB);
        addParams(entity.params, KEY_WB_SUPER_SG, sgName.trim());
        addParams(entity.params, KEY_WB_SUPER_SEC, secName.trim());
        addParams(entity.params, KEY_WB_SUPER_SG_EXT, sgExtParam.trim());
        addParams(entity.params, KEY_WB_TITLE, title);
        return entity;
    }

    /**
     * @param title   标题
     * @param summary 摘要
     * @param imgPath 本地图片地址
     */
    private static void addTitleSummaryAndThumb(Bundle params, String title, String summary, String imgPath) {
        addParams(params, KEY_WB_TITLE, title);
        addParams(params, KEY_WB_SUMMARY, summary);
        addParams(params, KEY_WB_IMG_LOCAL, imgPath);
    }

    /**
     * @param title   标题
     * @param summary 摘要
     * @param imgRes  res应用内图片资源
     */
    private static void addTitleSummaryAndThumb(Bundle params, String title, String summary, @DrawableRes int imgRes) {
        addParams(params, KEY_WB_TITLE, title);
        addParams(params, KEY_WB_SUMMARY, summary);
        addParams(params, KEY_WB_IMG_RES, imgRes);
    }
}
