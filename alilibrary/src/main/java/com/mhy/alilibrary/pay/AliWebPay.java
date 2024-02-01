package com.mhy.alilibrary.pay;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import java.lang.ref.WeakReference;

/**
 * Created By Mahongyin
 * Date    2024/1/22 9:53
 */
public class AliWebPay {
    private WeakReference<Activity> mActivity;
    public AliWebPay(Activity act) {
        mActivity = new WeakReference<>(act);
    }

    /**
     * 使用webview加载支付页面
     * WebViewClient实现 return AliPay.shouldOverrideUrlLoading(webView,url)
     * @return true 拦截消费
     */
    public boolean shouldOverrideUrlLoading(final WebView webView, String url) {
        if (!(url.startsWith("http") || url.startsWith("https"))) {
            return true;
        }
        WebSettings settings = webView.getSettings();
        if (!settings.getJavaScriptEnabled()) {
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
        }
        if (!CookieManager.allowFileSchemeCookies()){
            // 启用二方/三方 Cookie 存储和 DOM Storage
            // 注意：若要在实际 App 中使用，请先了解相关设置项细节。
            CookieManager.getInstance().setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            }
        }
        if (!settings.getDomStorageEnabled()){
            settings.setDomStorageEnabled(true);
        }
        /**
         * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
         */
        final PayTask task = new PayTask(mActivity.get());
        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
            @Override
            public void onPayResult(final H5PayResultModel result) {
                final String url = result.getReturnUrl();
                if (!TextUtils.isEmpty(url)) {
                    mActivity.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl(url);
                        }
                    });
                }
            }
        });

        /**
         * 判断是否成功拦截
         * 若成功拦截，则无需继续加载该URL；否则继续加载
         */
        if (!isIntercepted) {
            webView.loadUrl(url);
        }
        return true;
    }
    public void release() {
        mActivity=null;
    }
}
