package com.chelaile.auth.model.entity;

public class SysUserAuth {
    /** id */
    private Integer id;

    /** 用户id */
    private Integer userId;

    /** 权限ID */
    private Integer authId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAuthId() {
        return authId;
    }

    public void setAuthId(Integer authId) {
        this.authId = authId;
    }
}