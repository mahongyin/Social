package com.mhy.socialcommon.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author        Mahongyin
 * Description   用于 接受回调区分   整体区分 是 哪一个   qq/空间/微信 /朋友圈/微博/支付宝
 *
 * 自定义注解
 * 使用注解限定参数类型，这样可以避免传入限定以外的值
 */
//@Inherited：允许子类继承父类中的注解，可以通过反射获取到父类的注解
//@Documented: 用于标记在生成javadoc时是否将注解包含进去
//@Target：用于定义注解可以在什么地方使用，默认可以在任何地方使用，也可以指定使用的范围

/**Target
 * TYPE : 类、接口或enum声明
 * FIELD: 域(属性)声明
 * METHOD: 方法声明
 * PARAMETER: 参数声明
 * CONSTRUCTOR: 构造方法声明
 * LOCAL_VARIABLE:局部变量声明
 * ANNOTATION_TYPE:注释类型声明
 * PACKAGE: 包声明
 */
//当需要定义固定的几种状态值时，可以使用枚举或者注解，
// 使用注解@IntDef限定其值必须为显式声明常量之一
@IntDef(value = {Type.QQ_Share, Type.QQ_ZONE_Share, Type.WEIBO_Share, Type.WEIXIN_Share, Type.WEIXIN_PYQ_Share, Type.ALIPAY_Share,
        Type.QQ_Auth, Type.WEIBO_Auth, Type.WEIXIN_Auth, Type.ALIPAY_Auth, Type.WEIXIN_Pay, Type.ALIPAY_Pay})
@Retention(RetentionPolicy.SOURCE)//源码级别，注解只存在源码中
/**注解的声明周期  Retention
 * SOURCE：源码级别，注解只存在源码阶段保留，编译时会丢弃注解。一般用于和编译器交互，用于检测代码。如@Override, @SuppressWarings。
 * CLASS：字节码级别，注解存在于源码和字节码文件中，在运行时虚拟机不需要保留注解..主要用于编译时生成额外的文件，如XML，Java文件等，但运行时无法获得。 如mybatis生成实体和映射文件，这个级别需要添加JVM加载时候的代理（javaagent），使用代理来动态修改字节码文件。
 * RUNTIME：运行时级别，注解存在于源码、字节码、java虚拟机中，主要用于运行时，可以使用反射获取相关的信息。
 */
public @interface Type {

    /** QQ */
    int QQ_Share = 1;

    /** QQ空间 /说说*/
    int QQ_ZONE_Share = 2;

    /** 微博 */
    int WEIBO_Share = 3;

    /** 微信 */
    int WEIXIN_Share = 4;

    /** 微信朋友圈 */
    int WEIXIN_PYQ_Share = 5;

    /** 支付宝 */
    int ALIPAY_Share = 6;



    /** QQ */
    int QQ_Auth = 11;

    /** 微博 */
    int WEIBO_Auth = 13;

    /** 微信 */
    int WEIXIN_Auth = 12;

    /** 支付宝 */
    int ALIPAY_Auth = 14;



    /** 微信 */
    int WEIXIN_Pay = 21;

    /** 支付宝 */
    int ALIPAY_Pay = 22;
}
