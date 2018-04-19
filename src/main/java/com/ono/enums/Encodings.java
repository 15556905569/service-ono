package com.ono.enums;

/**
 * Created by amosli on 17/07/2017.
 */
public enum Encodings {
    UTF8("UTF-8"), GBK("GBK"), ISO88591("ISO-8859-1");
    private String value;

    Encodings(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
