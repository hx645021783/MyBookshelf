package com.jaredhuang.bookdb.throwable;


public class VipThrowable extends Throwable {

    private final static String tag = "VIP_THROWABLE";

    public  VipThrowable() {
        super("此书源使用了高级功能，复制支付宝红包搜索码领取红包或关注微信公众号[开源阅读软件]开启。");
    }
}
