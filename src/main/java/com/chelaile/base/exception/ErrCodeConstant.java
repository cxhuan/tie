package com.chelaile.base.exception;

/**
 * Description: 错误码管理类
 *
 * @author: cxhuan
 * @create: 2018/6/8 16:07
 */
public enum ErrCodeConstant {
    /*
     * 01xx 系统权限相关错误
     */
    //AUTH_USER_NOT_EXIST("0100", "用户不存在"),
    AUTH_ORG_NOT_EXIST("0101", "机构不存在"),
    AUTH_REDIRECT("0102", "重定向"),
    AUTH_VERIFY_CODE_INCORRECT("0103", "验证码有误"),
    AUTH_USER_NOT_EXIST("0104", "用户不存在"),
    AUTH_LOGIN_FAIL("0105", "用户名或密码错误"),
    AUTH_USER_INVALID("0106", "用户已失效"),
    AUTH_USER_EXPIRE("0107", "用户已过期"),
    AUTH_OLD_PASSWORD_ERROR("", "初始密码错误"),
    AUTH_CHANGE_PASSWORD_FAIL("", "修改密码失败"),
    AUTH_RESET_PASSWORD_FAIL("", "重置密码失败"),
    AUTH_USER_OP_UNPERMISSION("", "用户无操作权限"),
    USER_DUPLICATE("", "用户重复"),
    USER_ACCOUNT_CANNOT_MODIFY("", "不允许修改用户账号"),
    UPDATE_USER_FAIL("", "修改用户信息失败"),
    PARAM_IS_NULL("", "参数为空"),
    LACK_NECESSARY_PARAM("", "缺少必要参数"),
    VERIFY_CODE_GENERATE_FAIL("", "验证码生成失败"),
    PARENT_ORG_ID_ERROR("", "父机构ID错误"),
    ORG_DUPLICATE("", "机构重复"),
    PARAM_IS_INVALID("", "参数无效"),





    /*
     * 02xx
     */


    /*
     * 9999 系统错误
     */
    SYSTEM_INTERNAL_EXCEPTION("9999", "系统内部错误"),
    ;
    private String code;
    private String msg;

    ErrCodeConstant(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }
}
