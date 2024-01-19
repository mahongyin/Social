package com.mhy.wxlibrary.share;

import androidx.annotation.IntDef;

/**
 * Created By Mahongyin
 * Date    2024/1/20 1:42
 */
@IntDef(value = {WxType.TYPE_SESSION, WxType.TYPE_TIME_LINE, WxType.TYPE_FAVORITE})
public @interface WxType {
    /**
     * 分析好友
     */
    int TYPE_SESSION = 1;
    /**
     * 分析朋友圈
     */
    int TYPE_TIME_LINE = 2;
    /**
     * 分析收藏
     */
    int TYPE_FAVORITE = 3;
}