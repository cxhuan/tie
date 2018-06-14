package com.chelaile.auth.model.entity;

public class SysMenuAuth {
    /** id */
    private Integer id;

    /** 菜单ID */
    private Integer menuId;

    /** 权限ID */
    private Integer authId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getAuthId() {
        return authId;
    }

    public void setAuthId(Integer authId) {
        this.authId = authId;
    }
}