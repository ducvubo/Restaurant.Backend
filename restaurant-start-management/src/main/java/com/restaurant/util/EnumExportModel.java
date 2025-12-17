package com.restaurant.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Model để lưu trữ thông tin về một enum cần export
 */
public class EnumExportModel {
    private String name;
    private String capitalizedName;
    private List<EnumMemberExportModel> members = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapitalizedName() {
        return capitalizedName;
    }

    public void setCapitalizedName(String capitalizedName) {
        this.capitalizedName = capitalizedName;
    }

    public List<EnumMemberExportModel> getMembers() {
        return members;
    }

    public void setMembers(List<EnumMemberExportModel> members) {
        this.members = members;
    }
}

