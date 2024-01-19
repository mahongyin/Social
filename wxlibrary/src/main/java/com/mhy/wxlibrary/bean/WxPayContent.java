package com.mhy.wxlibrary.bean;

import com.mhy.socialcommon.PayContent;
import com.mhy.socialcommon.SocialType;

/**
 * 微信支付
 */
public class WxPayContent extends PayContent {
    //appid
    private String appid;

    private String timestamp;

    private String noncestr;
    //商户号
    private String partnerid;
    //key
    private String prepayid;

    private String packageValue;

    private String sign;

    public String code_url;

    public String return_url;  //用于前端数据展现


    public String getAppid() {
        return appid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public String getSign() {
        return sign;
    }

    public WxPayContent(String appid, String partnerid, String prepayid, String packagestr, String noncestr, String timestamp, String sign) {
        this.appid = appid;
        this.timestamp = timestamp;
        this.noncestr = noncestr;
        this.partnerid = partnerid;
        this.prepayid = prepayid;
        this.packageValue = packagestr;
        this.sign = sign;
        this.payType= SocialType.WEIXIN_Pay;
    }
    public WxPayContent( String partnerid, String prepayid, String packagestr, String noncestr, String timestamp, String sign) {
        this.timestamp = timestamp;
        this.noncestr = noncestr;
        this.partnerid = partnerid;
        this.prepayid = prepayid;
        this.packageValue = packagestr;
        this.sign = sign;
        this.payType= SocialType.WEIXIN_Pay;
    }
}
