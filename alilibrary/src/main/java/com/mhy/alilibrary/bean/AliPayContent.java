package com.mhy.alilibrary.bean;

import com.mhy.socialcommon.PayContent;
import com.mhy.socialcommon.SocialType;

/**
 * @author mahongyin 2020-05-29 18:56 @CopyRight mhy.work@qq.com
 * description alipay 实例化orderInfo
 */
public class AliPayContent extends PayContent {
    /**
     * orderInfo 由后端返回 或自己拼接后传入
     */
    private String orderInfo;

    /**
     * orderInfo 由后端返回 或自己拼接后传入
     * @return orderInfo
     */
    public String getOrderInfo() {
        return orderInfo == null ? "" : orderInfo;
    }


    /**
     * 实例化 orderInfo
     * @param orderInfo 由后端返回 或自己拼接后传入
     */
    public AliPayContent(String orderInfo) {
        this.orderInfo = orderInfo;
        this.payType= SocialType.ALIPAY_Pay;
    }



}
