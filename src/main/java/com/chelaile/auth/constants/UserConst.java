package com.chelaile.auth.constants;/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/11 19:29
 */

/**
 * @program tie
 * @description: 用户状态
 * @author: cxhuan
 * @create: 2018-06-11 19:29  
 */
public interface UserConst {
    /*
     * 用户状态
     */
    int INVALID = 0;
    int VALID = 1;

    /*
     * 用户类型
     */
    int SUPER_MANAGER = 0;//超级管理员
    int MANAGER = 1; //系统管理员
    int COMMON = 2; //普通用户
}
