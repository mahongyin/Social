package com.woaiwangpai.iwb.wxapi;

import android.util.Log;

import com.mhy.wxlibrary.wxapi.BaseWXActivity;

/**
 * 微信回调使用
 */
public class WXEntryActivity extends BaseWXActivity {

    @Override
    protected void onWXIntent(String extInfo) {
        Log.e("WXEntryActivity", "onWXIntent: " + extInfo);
    }
}
