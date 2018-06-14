package com.chelaile.base.exception;

/**
 * Description: 错误异常类
 *
 * @author: cxhuan
 * @create: 2018/6/8 16:07
 */
public class BusinessException extends Exception {
    private String errorCode;
    private String errorMsg;

    public BusinessException(String errorCode, String errorMsg) {
        super(String.format("%s:%s", errorCode, errorMsg));
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException(ErrCodeConstant errCodeConstant) {
        this(errCodeConstant.code(), errCodeConstant.msg());
    }

    public String getErrorCode() {
        return errorCode;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

}
