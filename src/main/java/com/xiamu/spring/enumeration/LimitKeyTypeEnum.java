package com.xiamu.spring.enumeration;

/**
 * @Author xianghui.luo
 * @Date 2025/2/7 14:15
 * @Description 访问限制类型枚举
 */
public enum LimitKeyTypeEnum {
    ALL("ALL","针对接口的总请求数"),
    IPADDR("IPADDR","根据Ip地址来限制"),
    IPADDRACCOUNT("IPADDRACCOUNT","根据Ip地址+账户来限制");

    private String key;
    private String desc;

    LimitKeyTypeEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
