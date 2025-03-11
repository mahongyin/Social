package com.mhy.wxlibrary.share;

import androidx.annotation.IntDef;

/**
 * Created By Mahongyin
 * Date    2024/1/20 1:42
 */
@IntDef(value = {WxType.TYPE_SESSION, WxType.TYPE_TIME_LINE, WxType.TYPE_FAVORITE})
public @interface WxType {
    /**
     * 分享好友会话
     */
    int TYPE_SESSION = 1;
    /**
     * 发送微信朋友圈
     */
    int TYPE_TIME_LINE = 2;
    /**
     * 发送微信收藏
     */
    int TYPE_FAVORITE = 3;
}