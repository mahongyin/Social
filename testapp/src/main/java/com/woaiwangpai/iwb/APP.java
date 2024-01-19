package com.woaiwangpai.iwb;

import android.app.Application;

import com.mhy.qqlibrary.QqSocial;
import com.mhy.wblibrary.WbSocial;
import com.mhy.wxlibrary.WxSocial;

/**
 * @author mahongyin 2020-05-28 14:09 @CopyRight mhy.work@qq.com
 * description .
 */
public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initSocial();
    }

    private void initSocial() {
        //AliShare.setAppId("2018111362152255");///2015060900117932//2018111362152255
        WxSocial.setWeixinId("wxbddb62b534debb5f");
        QqSocial.setAppId("101807669");
        WbSocial.setWbApp("4092786483",//"2045436852", /*包名是"com.sina.weibo.sdk.demo"*/
                "http://www.sina.com");
//        public static String QQ_APP_ID = "101844360";//QQ开放平台的APP_ID 1106556586//mybase101807669//rele1107492512
//        public static String QQ_APP_KEY = "6fff04a5ad90bdff081cc78544057d2d";//QQ开放平台的APP_ID
//        public static String WEIBO_APP_KEY = "4092786483";//微博开放平台的APP_key//0c7daf8aecca98536708479e42286b81
//        public static String WEIBO_RESULT_URL = "https://api.weibo.com/oauth2/default.html";//微博返回URL

    }
}
