package com.mhy.socialcommon;

/**
 * @author mahongyin 2020-05-29 17:41 @CopyRight mhy.work@qq.com
 * description payinfo 基类
 */
public abstract class PayContent {
    protected SocialType payType;

    /**
     * @return SocialType
     * @see SocialType
     */
    public SocialType getPayType() {
        return payType;
    }

    public void setPayType(SocialType payType) {
        this.payType = payType;
    }
}
