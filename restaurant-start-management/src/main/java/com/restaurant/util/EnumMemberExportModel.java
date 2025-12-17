package com.restaurant.util;

/**
 * Model để lưu trữ thông tin về một thành viên (member) của enum
 */
public class EnumMemberExportModel {
    private String name;
    private int value;
    private String text;
    private String dataName;
    private String className;
    private String guidId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGuidId() {
        return guidId;
    }

    public void setGuidId(String guidId) {
        this.guidId = guidId;
    }
}

