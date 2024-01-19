package com.mhy.wxlibrary.bean;


import com.mhy.socialcommon.SocialType;

public class WeiXin {
    private SocialType type;//1:登录 2.分享 3:支付
    private int errCode;//微信返回的错误码
    private String code;//登录成功才会有的code

    public WeiXin(SocialType type, int errCode, String code) {
        this.type = type;
        this.errCode=errCode;
        this.code = code;
    }

    public SocialType getType() {
        return type;
    }

    public void setType(SocialType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}
