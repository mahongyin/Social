package com.mhy.socialcommon;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.service.quicksettings.TileService;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mahongyin 2020-05-30 12:29 @CopyRight mhy.work@qq.com
 * description .
 */
public class ShareUtil {
    private Context mActivity;
    private static ShareUtil shareUtil;

    public static ShareUtil getInstance(Context context) {
        if (shareUtil == null) {
            shareUtil = new ShareUtil(context);
        }
        return shareUtil;
    }

    private ShareUtil(Context act) {
        this.mActivity = act;
    }

    /**
     * 分享前必须执行本代码，主要用于兼容SDK18以上的系统
     */
    private static void checkFileUriExposure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    public void shareText(String msg, @PackageName String... packageName) {
        checkFileUriExposure();
        Intent qqIntent = new Intent(Intent.ACTION_SEND);
        if (packageName.length == 1 && !TextUtils.isEmpty(packageName[0])) {
            qqIntent.setPackage(packageName[0]);
        }
        qqIntent.setType("text/plain");
//        qqIntent.putExtra(Intent.EXTRA_SUBJECT, "选则要分享的应用");
        qqIntent.putExtra(Intent.EXTRA_TEXT, msg);
        qqIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(qqIntent);
        mActivity.startActivity(Intent.createChooser(qqIntent, "分享"));
    }

    /**
     * @param resImg 本地图片
     */
    public void shareImg(@DrawableRes int resImg, String packageName, String classname) {
        Bitmap bmp = BitmapFactory.decodeResource(mActivity.getResources(), resImg);
        shareBitMapImg(bmp, packageName);
        bmp.recycle();
//        checkFileUriExposure();
//        String path = getResourcesUri(resImg);
//        Intent imageIntent = new Intent(Intent.ACTION_SEND);
//        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(classname)) {
//            ComponentName comp = new ComponentName(packageName, classname);
//            imageIntent.setComponent(comp);
//            imageIntent.setPackage(packageName);
//        }
//        imageIntent.setType("image/*");
//        imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
//        imageIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//        mActivity.startActivity(Intent.createChooser(imageIntent, "分享"));
    }
    public void shareImg(@DrawableRes int resImg, String packageName) {
        Bitmap bmp = BitmapFactory.decodeResource(mActivity.getResources(), resImg);
        shareBitMapImg(bmp, packageName);
        bmp.recycle();
//        checkFileUriExposure();
//        String path = getResourcesUri(resImg);
//        Intent imageIntent = new Intent(Intent.ACTION_SEND);
//        if (!TextUtils.isEmpty(packageName)) {
//            imageIntent.setPackage(packageName);
//        }
//        imageIntent.setType("image/*");
//        imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
//        imageIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//        mActivity.startActivity(Intent.createChooser(imageIntent, "分享"));
    }

    public void shareImg(String path, String packageName, String classname) {
        checkFileUriExposure();
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(classname)) {
            ComponentName comp = new ComponentName(packageName, classname);
            imageIntent.setComponent(comp);
            imageIntent.setPackage(packageName);
        }
        imageIntent.setType("image/*");
        imageIntent.putExtra(Intent.EXTRA_STREAM, getFileUri(packageName, path));
        imageIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(imageIntent, "分享"));
    }

    public void shareImg(String path, String packageName) {
        checkFileUriExposure();
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(packageName)) {
            imageIntent.setPackage(packageName);
        }
        imageIntent.setType("image/*");
        imageIntent.putExtra(Intent.EXTRA_STREAM, getFileUri(packageName, path));
        imageIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(imageIntent, "分享"));
    }
    public void shareBitMapImg(Bitmap bitmap, String packageName) {
        checkFileUriExposure();
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap, null,null));
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(packageName)) {
            imageIntent.setPackage(packageName);
        }
        imageIntent.setType("image/*");
        imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        imageIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(imageIntent, "分享"));
    }

    public void shareMultPath(ArrayList<String> imagePaths, String packageName, String classname) {
        checkFileUriExposure();
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (String imagePath : imagePaths) {
            Uri uri = Uri.parse(imagePath);
            imageUris.add(uri);
        }

        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(classname)) {
            ComponentName comp = new ComponentName(packageName, classname);
            mulIntent.setComponent(comp);
            mulIntent.setPackage(packageName);
        }
        mulIntent.setType("image/*");
        mulIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(mulIntent, "分享"));
    }

    public void shareMultImg(ArrayList<Integer> imageRes, String packageName, String classname) {
        checkFileUriExposure();
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (Integer imageRe : imageRes) {
            Uri uri1 = Uri.parse(getResourcesUri(imageRe));
            imageUris.add(uri1);
        }
        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(classname)) {
            ComponentName comp = new ComponentName(packageName, classname);
            mulIntent.setComponent(comp);
            mulIntent.setPackage(packageName);
        }
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        mulIntent.setType("image/*");
        mulIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(mulIntent, "分享"));
    }

    // 调用系统方法分享文件
    public void shareFile(String filePath, @PackageName String... packageName) {
        checkFileUriExposure();
        File file = new File(filePath);
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri = null;
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // "项目包名.fileprovider"即是在清单文件中配置的authorities
                uri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".social.fileprovider", file);
                // 给目标应用一个临时授权
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            if (packageName.length == 1 && !TextUtils.isEmpty(packageName[0])) {
                share.setPackage(packageName[0]);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mActivity.startActivity(Intent.createChooser(share, "分享文件"));
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    private String getResourcesUri(@DrawableRes int id) {
        Resources resources = mActivity.getResources();
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
    }

    public void sendEmail(String title, String content, String emails) {
        // 必须明确使用mailto前缀来修饰邮件地址
        Uri uri = Uri.parse("mailto:" + emails);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);//不带附件
//        intent.putExtra(Intent.EXTRA_EMAIL, emails);//接收者
//        intent.putExtra(Intent.EXTRA_CC, emails); // 抄送人
//        intent.putExtra(Intent.EXTRA_BCC, emails);//密送
        intent.putExtra(Intent.EXTRA_SUBJECT, title); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, content); // 正文
        mActivity.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }

    /**
     * 带附件
     *
     * @param title
     * @param content
     * @param emails
     * @param filePath
     */
    public void sendEmailAccessory(String title, String content, String[] emails, String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
//        intent.putExtra(Intent.EXTRA_CC, emails);
//        intent.putExtra(Intent.EXTRA_BCC, emails);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);

        intent.putExtra(Intent.EXTRA_STREAM, getFileUri(null, filePath/*"file:///mnt/sdcard/a.jpg"*/));
        intent.setType("*/*");//intent.setType("text/html");.html类型
        intent.setType("message/rfc882");
        Intent.createChooser(intent, "请选择邮件类应用");
        mActivity.startActivity(intent);
        // 必须明确使用mailto前缀来修饰邮件地址,
//        Uri uri = Uri.parse("mailto:" + emails);
//        Intent intent = new Intent(Intent.ACTION_SEND, uri);//带附件
//        intent.putExtra(Intent.EXTRA_EMAIL, emails);
//        intent.putExtra(Intent.EXTRA_CC, emails); // 抄送人
//        intent.putExtra(Intent.EXTRA_SUBJECT, title); // 主题
//        intent.putExtra(Intent.EXTRA_TEXT, content); // 正文
//        mActivity.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }

    /**
     * 多附件
     *
     * @param title
     * @param content
     * @param emails
     * @param filePaths
     */
    public void sendEmailMultipleAccessory(String title, String content, String emails, List<String> filePaths) {
        // 必须明确使用mailto前缀来修饰邮件地址,如果使用
//        Uri uri = Uri.parse("mailto:" + emails);
//        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE, uri);//带多附件
//        intent.putExtra(Intent.EXTRA_EMAIL, emails);
//        intent.putExtra(Intent.EXTRA_CC, emails); // 抄送人
//        intent.putExtra(Intent.EXTRA_SUBJECT, title); // 主题
//        intent.putExtra(Intent.EXTRA_TEXT, content); // 正文
//        mActivity.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
//        intent.putExtra(Intent.EXTRA_CC, emails);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);

        ArrayList<Uri> imageUris = new ArrayList<>();
        for (String filePath : filePaths) {
            imageUris.add(getFileUri(null, filePath/*"file:///mnt/sdcard/a.jpg"*/));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.setType("*/*");
        intent.setType("message/rfc882");
        Intent.createChooser(intent, "请选择邮件类应用");
        mActivity.startActivity(intent);
    }

    public void openMap(String j, String w) {
        Uri mapUri = Uri.parse("geo:" + j + "，" + w);
        mActivity.startActivity(new Intent(Intent.ACTION_VIEW, mapUri));
    }

    public void toCall(String phone) {
        Uri telUri = Uri.parse("tel:" + phone);
        mActivity.startActivity(new Intent(Intent.ACTION_DIAL, telUri));
    }

    public void doCall(String phone) {
        Uri callUri = Uri.parse("tel:" + phone);
        mActivity.startActivity(new Intent(Intent.ACTION_CALL, callUri));
    }

    public void unInstanll(String packname) {
        Uri uninstallUri = Uri.fromParts("package", packname, null);
        mActivity.startActivity(new Intent(Intent.ACTION_DELETE, uninstallUri));
    }

    public void instanll(String packname) {
        Uri installUri = Uri.fromParts("package", packname, null);
        mActivity.startActivity(new Intent(Intent.ACTION_PACKAGE_ADDED, installUri));
    }

    public void playMedia(String path) {
        Uri playUri = getFileUri(null, path);
        mActivity.startActivity(new Intent(Intent.ACTION_VIEW, playUri));
    }

    public void toSendSMS(String phone, String msg) {
        Uri smsUri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
        mActivity.startActivity(intent);
        intent.putExtra("sms_body", "yyyy");
        intent.setType("vnd.android-dir/mms-sms");
    }

    public void doSendSMS(String phone, String msg) {
        Uri smsToUri = Uri.parse("smsto://" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", msg);
        mActivity.startActivity(intent);
    }

    public void toSendColorSMS(String mediaPath, String phone, String msg) {
        Uri mmsUri = Uri.parse(mediaPath/*"content://media/external/images/media/23"*/);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("sms_body", msg);
        intent.putExtra(Intent.EXTRA_STREAM, mmsUri);
        intent.setType("*/*");
        mActivity.startActivity(intent);
    }

    /*
    系统返回的一些常见 Uri 样式：
    content://com.android.providers.media.documents..
    content://com.android.providers.downloads...
    content://media/external/images/media/...
    content://com.android.externalstorage.documents..*/
    @StringDef({ShareContentType.TEXT, ShareContentType.IMAGE, ShareContentType.AUDIO, ShareContentType.VIDEO, ShareContentType.File})
    @Retention(RetentionPolicy.SOURCE)
    @interface ShareContentType {
        /**
         * Share Text
         */
        final String TEXT = "text/plain";

        /**
         * Share Image
         */
        final String IMAGE = "image/*";

        /**
         * Share Audio
         */
        final String AUDIO = "audio/*";

        /**
         * Share Video
         */
        final String VIDEO = "video/*";

        /**
         * Share File
         */
        final String File = "*/*";
    }

    /**
     * 注解限定String类型为指定
     */
    @StringDef(value = {package_ali, package_qq, package_wb, package_wx})
    @Retention(RetentionPolicy.SOURCE)
    @interface PackageName {
    }

    public static final String package_qq = "com.tencent.mobileqq";
    public static final String package_ali = "com.eg.android.AlipayGphone";
    public static final String package_wx = "com.tencent.mm";
    public static final String package_wb = "com.sina.weibo";

    public static final String AliPay_Barcode = "alipayqr://platformapi/startapp?saId=20000056";//付款码
    public static final String AliPay_Paycode = "alipayqr://platformapi/startapp?saId=20000123";//收款码
    public static final String AliPay_Hongbao = "alipay://platformapi/startapp?saId=88886666";//红包
    public static final String AliPay_Scan = "alipayqr://platformapi/startapp?saId=10000007";//扫码
    private static final String AliPay_Qr = "&qrcode=https%3a%2f%2fqr.alipay.com%2f";//扫码字段
    private static final String AliPay_Qr_Url = "https://qr.alipay.com/";
    private static final String AliPay_Qr_Me = "&qrcode=https%3a%2f%2fqr.alipay.com%2ffkx19000ssxku6zeqdfnc1f";//个体商户
    public static final String WX_Scan = "weixin://scanqrcode";//微信扫码
    public static final String WX = "weixin://";//打开微信
    public static final String AliPay = "alipays://platformapi/startApp";//打开支付包

    /*利用URL Scheme

      比如在自带浏览器里面输入
      "javascript:window.location.href=’alipays://platformapi/startapp?appId=20000056’;"
      即可直接打开支付宝的二维码付款页面。自己手动添加浏览器书签然后丢到桌面就行了。
      支付宝扫码
      alipayqr://platformapi/startapp?saId=10000007
      如果希望扫一扫和二维码集成，立即就跳出支付页面，则使用如下：fkx19000ssxku6zeqdfnc1f
      alipayqr://platformapi/startapp?saId=10000007&qrcode=https%3a%2f%2fqr.alipay.com%2ffkx19000ssxku6zeqdfnc1f
      支付宝付款码（跳转支付宝转账向商家付款界面）
      alipays://platformapi/startapp?appId=20000056
      支付宝红包入口
      alipay://platformapi/startapp?saId=88886666
      支付宝收款码
      alipayqr://platformapi/startapp?saId=20000123
   支付宝记账
   alipay://platformapi/startapp?appId=20000168
   （跳转支付宝记账界面）

   支付宝滴滴
   alipay://platformapi/startapp?appId=20000778

   支付宝蚂蚁森林
   alipay://platformapi/startapp?appId=60000002

   支付宝转账
   alipayqr://platformapi/startapp?saId=20000116
   （跳转支付宝转账界面）

   支付宝手机充值
   alipayqr://platformapi/startapp?saId=10000003
   （跳转支付宝手机充值页面）

   支付宝卡包
   alipayqr://platformapi/startapp?saId=20000021
   （跳转支付宝卡包页面）

   支付宝吱口令
   alipayqr://platformapi/startapp?saId=20000085
   （跳转支付宝吱口令页面）

   支付宝芝麻信用
   alipayqr://platformapi/startapp?saId=20000118
   （跳转支付宝芝麻信用页面）

   支付宝红包
   alipayqr://platformapi/startapp?saId=88886666
   （跳转支付宝红包页面）

   支付宝爱心
   alipayqr://platformapi/startapp?saId=1000009
   （跳转支付宝献爱心页面）

   支付宝升级页面
   alipayqr://platformapi/startapp?saId=2000066
   （跳转支付宝升级页面）

   支付宝滴滴打的
   alipayqr://platformapi/startapp?saId=2000130
   （跳转支付宝滴滴打的页面）

   支付宝客服
   alipayqr://platformapi/startapp?saId=2000691
   （跳转支付宝客服页面）

   支付宝生活
   alipayqr://platformapi/startapp?saId=2000193
   （跳转支付宝生活页面）

   支付宝生活号
   alipayqr://platformapi/startapp?saId=2000101
   （跳转支付宝生活号页面）

      下面是微信的
      启动微信
      weixin://
      微信扫一扫
      weixin://scanqrcode

      ”weixin://dl/groupchat“发起群聊
      ”weixin://dl/add“添加朋友
      ”weixin://dl/log“上报日志
      ”weixin://dl/recommendation“新的朋友
      ”weixin://dl/groups“群聊
      ”weixin://dl/tags“标签
      ”weixin://dl/officialaccounts“公众号
      ”weixin://dl/moments“朋友圈
      ”weixin://dl/scan“扫一扫
      ”weixin://dl/shopping“购物
      ”weixin://dl/games“游戏
      ”weixin://dl/profile“个人信息
      ”weixin://dl/setname“名字
      ”weixin://dl/myQRcode“我的二维码
      ”weixin://dl/myaddress“我的地址
      ”weixin://dl/posts“相册
      ”weixin://dl/favorites“收藏
      ”weixin://dl/card“优惠券
      ”weixin://dl/stickers“表情
      ”weixin://dl/settings“设置
      ”weixin://dl/bindqq“QQ 号
      ”weixin://dl/bindmobile“手机号
      ”weixin://dl/bindemail“邮箱地址
      ”weixin://dl/protection“帐号保护
      ”weixin://dl/notifications“新消息通知
      ”weixin://dl/blacklist“通讯录黑名单
      ”weixin://dl/hidemoments“不让他（她）看我的朋友圈
      ”weixin://dl/blockmoments“不看他（她）的朋友圈
      ”weixin://dl/general“通用
      ”weixin://dl/languages“多语言
      ”weixin://dl/textsize“字体大小
      ”weixin://dl/stickersetting“我的表情
      ”weixin://dl/sight“朋友圈小视频
      ”weixin://dl/features“功能
      ”weixin://dl/securityassistant“通讯录同步助手
      ”weixin://dl/broadcastmessage“群发助手
      ”weixin://dl/chathistory“聊天记录迁移
      ”weixin://dl/clear“清理微信存储空间
      ”weixin://dl/help“意见反馈
      ”weixin://dl/about“关于微信
   */
    public boolean openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 启动外部app主页
     */
    public void openOutMain(String packname, String className, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName cmp = new ComponentName(packname, className);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setComponent(cmp);
        startIntent(intent);
    }

    /**
     * Intent.ACTION_VIEW 非主页 外部app
     */
    public void openOutActivity(String packname, String className, Bundle bundle, int flag) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        ComponentName cmp = new ComponentName(packname, className);
        if (flag != 0) {
            intent.addFlags(flag);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setComponent(cmp);
        startIntent(intent);
    }

    private void startIntent(Intent intent) {
        if (isActivityAvailable(intent)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (mActivity instanceof TileService) {
                    ((TileService) mActivity).startActivityAndCollapse(intent);
                } else {
                    mActivity.startActivity(intent);
                }
            } else {
                mActivity.startActivity(intent);
            }
        }
    }


    /**
     * 打开weixin扫一扫界面 如需收款，请自行操作保存收款码到相册步骤
     */
    public void openWxScan() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("LauncherUI.From.Scaner.Shortcut", true);
        openOutActivity("com.tencent.mm", "com.tencent.mm.ui.LauncherUI", bundle, FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
    }

    public void openWX() {
        openOutMain("com.tencent.mm", "com.tencent.mm.ui.LauncherUI", null);
//   openUrl(WX);
    }

    /**
     * 支付宝个人/商家 捐赠 扫码支付
     *
     * @param urlCode 收款码 /末尾
     *                <p>
     *                scheme=alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Fc1x05309e4ttz2v7xrwrzcd%3F_s%3Dweb-other
     *                alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fwww.baidu.com%2F //使用支付宝打开指定网址
     */

    public void alipayMe(String urlCode) {
//        urlCode="fkx150444qjqymmownj8acb";//00c060630igcenu4bfbud2e
//        openUrl(AliPay_Scan + "&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F" + urlCode + "%3F_s%3Dweb-other");
        openUrl(AliPay_Scan + AliPay_Qr + urlCode);
    }
    //通过浏览器打开
    public void alipayMeUrl(String urlCode) {
        openUrl(AliPay_Qr_Url+urlCode);
    }

    /**
     * 我的收款页
     */
    public void alipayMe() {
        openUrl(AliPay_Scan + AliPay_Qr_Me);
    }
    public void alipayMeUrl() {
        openUrl(AliPay_Qr_Url+"fkx19000ssxku6zeqdfnc1f");
    }
    /**
     * 是否安装某APP
     *
     * @param pack 包名  "com.eg.android.AlipayGphone"
     * @return true 已安装
     */
    protected boolean hasInstall(String pack) {
        PackageManager pm = mActivity.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(pack, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * intent是否可达
     *
     * @param intent intent.putE等
     * @return true ok
     */
    protected boolean isActivityAvailable(Intent intent) {
        PackageManager pm = mActivity.getPackageManager();
        if (pm == null) {
            return false;
        }
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    //判断是否安装支付宝app
    public boolean checkAliPayInstalled() {
        Uri uri = Uri.parse(ShareUtil.AliPay);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(mActivity.getPackageManager());
        return componentName != null;
    }

    public boolean checkWxInstalled() {
        Uri uri = Uri.parse(ShareUtil.WX);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(mActivity.getPackageManager());
        return componentName != null;
    }

    //返回 contentUri.toString() 即是以"content://"开头的用于共享的路径
    public Uri getFileUri(@Nullable String packageName, String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(mActivity,
                    mActivity.getPackageName() + ".social.fileprovider",  /* 要与`AndroidManifest.xml`里配置的`authorities`一致*/
                    file);
            if (packageName == null) {
                packageName = mActivity.getPackageName();
            }
            // 授权给微博访问路径
            mActivity.grantUriPermission(packageName,  /* 这里填包名*/
                    contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return contentUri;
        } else {
            return Uri.fromFile(file);
        }
    }
}
