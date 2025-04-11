package com.woaiwangpai.iwb.wxapi;

import android.util.Log;

import com.mhy.wxlibrary.wxapi.BaseWXActivity;

/**
 * 微信回调使用
 */
public class WXEntryActivity extends BaseWXActivity {

    @Override
    protected void onWXIntent(int objType, String json) {
        // 来自微信跳转带的数据，比如微信分享后跳转app，目前微信不回调 原因未知
        Log.i("WXEntryActivity", "onWXIntent: " + json);
        //Uri titleLink = Uri.parse(extInfo);
        //Log.i("微信Uri", titleLink.toString());
        //val linkUri = titleLink.getQueryParameter("link") ?: "";
        //Intent/EventBus
    }
}
