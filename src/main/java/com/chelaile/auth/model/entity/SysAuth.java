package com.chelaile.auth.model.entity;

public class SysAuth {
    /** ID */
    private Integer id;

    /** 权限名称 */
    private String authName;

    /** 权限url */
    private String authUrl;

    /** 是否全局黑名单(1：是；空：不是) */
    private Integer globalBlacklist;

    /** 备注 */
    private String memo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public Integer getGlobalBlacklist() {
        return globalBlacklist;
    }

    public void setGlobalBlacklist(Integer globalBlacklist) {
        this.globalBlacklist = globalBlacklist;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}