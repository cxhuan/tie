package com.chelaile.base;


import com.alibaba.fastjson.JSON;
import com.chelaile.base.exception.ErrCodeConstant;


public class JResponse<T> {
    /**
     * 消息状态
     */
    private String code;

    private T data;
    // 错误消息
    private String errMsg;

    public static <T> JResponse<T> success(T data) {
        return newJsonMessage("0000", data, null);
    }

    public static <T> JResponse<T> fail(ErrCodeConstant errCodeConstant) {
        return newJsonMessage(errCodeConstant.code(), null, errCodeConstant.msg());
    }

    public static <T> JResponse<T> newJsonMessage(String code, T data, String errMsg) {
        JResponse<T> t = new JResponse<>();
        t.setCode(code);
        t.setData(data);
        t.setErrMsg(errMsg);
        return t;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public static void main(String[] args) {
        JResponse<Long> x = success(20L);
        System.out.println(JSON.toJSONString(x));
    }
}
