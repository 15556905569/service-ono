package com.ono.enums;


/**
 * <p>
 * 10001  缺少必传参数
 * 10002  参数错误
 * <p>
 * 20001  服务器错误
 * <p>
 * 30001  数据库错误
 * <p>
 * 99999  未知错误
 */
public enum StatusCode {

    PARAMS_EMPTY("10001", "缺少必传参数"),
    PARAMS_UNKNOWN("10002", "参数错误"),
    WEBSITE_ERROR("10003", "暂不支持此网站"),

    SERVER_ERROR("20001", "服务器错误"),
    DB_ERROR("30001", "数据库错误"),
    UNKNOWN("99999", "未知错误");

    private String code;
    private String desc;

    StatusCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
