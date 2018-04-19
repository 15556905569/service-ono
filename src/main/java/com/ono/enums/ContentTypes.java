package com.ono.enums;

/**
 * Created by amosli on 12/07/2017.
 */
public enum ContentTypes {

    JSON("application/json"),
    FORM("application/x-www-form-urlencoded");
    private String value;

    ContentTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
