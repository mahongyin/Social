package com.mhy.wxlibrary.bean;

import android.os.Bundle;

import androidx.annotation.DrawableRes;

import com.mhy.socialcommon.ShareEntity;
import com.mhy.socialcommon.SocialType;
import com.mhy.wxlibrary.share.WxType;


/**
 * Function：
 * Desc：
 */
public final class WxShareEntity extends ShareEntity {
    public static final String KEY_WX_TYPE = "key_wx_type";
    /**
     * 依次为：文本，图片，音乐，视频，网页
     */
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMG = 1;
    public static final int TYPE_MUSIC = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_WEB = 4;
    public static final int TYPE_MINIAPP = 5;//add

    public static final String KEY_WX_TITLE = "key_wx_title";
    public static final String KEY_WX_SUMMARY = "key_wx_summary";
    public static final String KEY_WX_TEXT = "key_wx_text";
    public static final String KEY_WX_IMG_LOCAL = "key_wx_local_img";
    public static final String KEY_WX_IMG_RES = "key_wx_img_res";
    public static final String KEY_WX_MUSIC_URL = "key_wx_music_url";
    public static final String KEY_WX_VIDEO_URL = "key_wx_video_url";
    public static final String KEY_WX_WEB_URL = "key_wx_web_url";
    //add
    public static final String KEY_WX_MINI_APPID = "key_wx_mini_appid";
    //add
    public static final String KEY_WX_MINI_PATH = "key_wx_mini_path";

    private WxShareEntity(SocialType type) {
        super(type);
    }

    /**
     * 分享文本
     *
     * @param type 是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param text 文本
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createText(@WxType int type, String text) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_TEXT);
        addParams(entity.params, KEY_WX_TEXT, text);
        return entity;
    }

    /**
     * 分享图片
     *
     * @param type        是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param imgFilePath 本地图片地址
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createImage(@WxType int type, String imgFilePath) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_IMG);
        addParams(entity.params, KEY_WX_IMG_LOCAL, imgFilePath);
        return entity;
    }

    /**
     * 分享图片
     *
     * @param type   是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param imgRes 应用内图片资源
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createImage(@WxType int type, @DrawableRes int imgRes) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_IMG);
        addParams(entity.params, KEY_WX_IMG_RES, imgRes);
        return entity;
    }

    /**
     * 分享音乐
     *
     * @param type     是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param musicUrl 音乐url，不支持本地音乐
     * @param imgUrl   本地图片地址，缩略图大小
     * @param title    音乐标题
     * @param summary  音乐摘要
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createMusic(@WxType int type, String musicUrl, String imgUrl, String title, String summary) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_MUSIC);
        addParams(entity.params, KEY_WX_MUSIC_URL, musicUrl);
        addTitleSummaryAndThumb(entity.params, title, summary, imgUrl);
        return entity;
    }

    /**
     * 分享音乐
     *
     * @param type     是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param musicUrl 音乐url，不支持本地音乐
     * @param imgRes   应用内图片资源，缩略图大小
     * @param title    音乐标题
     * @param summary  音乐摘要
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createMusic(@WxType int type, String musicUrl, @DrawableRes int imgRes, String title, String summary) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_MUSIC);
        addParams(entity.params, KEY_WX_MUSIC_URL, musicUrl);
        addTitleSummaryAndThumb(entity.params, title, summary, imgRes);
        return entity;
    }

    /**
     * 分享视频
     *
     * @param type     是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param videoUrl 视频url，不支持本地音乐
     * @param imgUrl   本地图片地址，缩略图大小
     * @param title    视频标题
     * @param summary  视频摘要
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createVideo(@WxType int type, String videoUrl, String imgUrl, String title, String summary) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_VIDEO);
        addParams(entity.params, KEY_WX_VIDEO_URL, videoUrl);
        addTitleSummaryAndThumb(entity.params, title, summary, imgUrl);
        return entity;
    }

    /**
     * 分享视频
     *
     * @param type     是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param videoUrl 音乐url，不支持本地音乐
     * @param imgRes   应用内图片资源，缩略图大小
     * @param title    视频标题
     * @param summary  视频摘要
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createVideo(@WxType int type, String videoUrl, @DrawableRes int imgRes, String title, String summary) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_VIDEO);
        addParams(entity.params, KEY_WX_VIDEO_URL, videoUrl);
        addTitleSummaryAndThumb(entity.params, title, summary, imgRes);
        return entity;
    }

    /**
     * 分享网页
     *
     * @param type    是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param webUrl  视频url，不支持本地音乐
     * @param imgUrl  本地图片地址，缩略图大小
     * @param title   网页标题
     * @param summary 网页摘要
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createWebPage(@WxType int type, String webUrl, String imgUrl, String title, String summary) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_WEB);
        addParams(entity.params, KEY_WX_WEB_URL, webUrl);
        addTitleSummaryAndThumb(entity.params, title, summary, imgUrl);
        return entity;
    }

    /**
     * 分享网页
     *
     * @param type    是否分享到朋友圈，好友列表,朋友圈,收藏
     * @param webUrl  音乐url，不支持本地音乐
     * @param imgRes  应用内图片资源，缩略图大小
     * @param title   网页标题
     * @param summary 网页摘要
     * @return ShareEntity 分享内容包装
     */
    public static ShareEntity createWebPage(@WxType int type, String webUrl, @DrawableRes int imgRes, String title, String summary) {
        SocialType valw = SocialType.WEIXIN_Share;
        if (type == WxType.TYPE_TIME_LINE) {
            valw = SocialType.WEIXIN_TimeLine_Share;
        } else if (type == WxType.TYPE_FAVORITE) {
            valw = SocialType.WEIXIN_Favorite;
        }
        ShareEntity entity = new ShareEntity(valw);
        addParams(entity.params, KEY_WX_TYPE, TYPE_WEB);
        addParams(entity.params, KEY_WX_WEB_URL, webUrl);
        addTitleSummaryAndThumb(entity.params, title, summary, imgRes);
        return entity;
    }

    /**
     * @param title   标题
     * @param summary 摘要
     * @param imgUrl  本地图片地址
     */
    private static void addTitleSummaryAndThumb(Bundle params, String title, String summary, String imgUrl) {
        addParams(params, KEY_WX_TITLE, title);
        addParams(params, KEY_WX_SUMMARY, summary);
        addParams(params, KEY_WX_IMG_LOCAL, imgUrl);
    }

    /**
     * @param title   标题
     * @param summary 摘要
     * @param imgRes  应用内图片资源
     */
    private static void addTitleSummaryAndThumb(Bundle params, String title, String summary, @DrawableRes int imgRes) {
        addParams(params, KEY_WX_TITLE, title);
        addParams(params, KEY_WX_SUMMARY, summary);
        addParams(params, KEY_WX_IMG_RES, imgRes);
    }

    public static void createMiniApp(Bundle params, String miniAppid, String miniPath, String webpageUrl, String title, String summary, @DrawableRes int imgRes) {
        addParams(params, KEY_WX_TITLE, title);
        addParams(params, KEY_WX_SUMMARY, summary);
        addParams(params, KEY_WX_MINI_APPID, miniAppid);
        addParams(params, KEY_WX_MINI_PATH, miniPath);
        addParams(params, KEY_WX_WEB_URL, webpageUrl);
        addParams(params, KEY_WX_IMG_RES, imgRes);
    }

    /**
     * @param miniAppid  小程序 原始gh_id
     * @param miniPath   小程序路径 默认入口 ？&传参;
     * @param webpageUrl web
     * @param title      标题
     * @param summary    详情
     * @param imgUrl     图片
     */
    public static void createMiniApp(Bundle params, String miniAppid, String miniPath, String webpageUrl, String title, String summary, String imgUrl) {
        addParams(params, KEY_WX_TITLE, title);
        addParams(params, KEY_WX_SUMMARY, summary);
        addParams(params, KEY_WX_MINI_APPID, miniAppid);
        addParams(params, KEY_WX_MINI_PATH, miniPath);
        addParams(params, KEY_WX_WEB_URL, webpageUrl);
        addParams(params, KEY_WX_IMG_LOCAL, imgUrl);
    }
}
