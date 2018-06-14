package com.chelaile.auth.base;

import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.model.entity.SysUserExample;
import com.chelaile.auth.service.SysUserService;
import com.chelaile.auth.util.SecuritySessionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 身份校验核心类
 *
 * @author: cxhuan
 * @create: 2018/6/13 14:20
 */
@Component
@Transactional
public class MyShiroRealm extends AuthorizingRealm {
    public Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    private SysUserService sysUserService;

    /**
     * Description: 此方法调用  hasRole,hasPermission的时候才会进行回调.
     *
     * 权限信息.(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，
     * 调用clearCached方法；
     * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     *
     * @param pc
     * @return:
     *
     * @auther: cxhuan
     * @date: 2018/6/13 14:21
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            doClearCache(pc);
            SecurityUtils.getSubject().logout();
            return null;
        }

        if (pc == null) {
            throw new AuthorizationException("parameters principals is null");
        }
        // 获取已认证的用户名（登录名）
        // String userId = (String) super.getAvailablePrincipal(pc);
        // if (StringUtils.isEmpty(userId)) {
        // return null;
        // }
        // Set<String> roleCodes=roleService.getRoleCodeSet(userId);
        // 默认用户拥有所有权限
        // Set<String> functionCodes=functionService.getAllFunctionCode();
       // SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // authorizationInfo.setRoles(roleCodes);
        // authorizationInfo.setStringPermissions(functionCodes);
        return new SimpleAuthorizationInfo();

    }

    /**
     * Description: 认证信息.(身份验证)
     *
     * @param token
     * @return: 
     *       
     * @auther: cxhuan
     * @date: 2018/6/13 15:15
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal(); // 得到用户名
        String password = new String((char[]) token.getCredentials()); // 得到密码
        if (null != username) {
            String passwd = new SimpleHash("SHA-1", "", password).toString(); // 密码加密
            SysUserExample sysUserExample = new SysUserExample();
            sysUserExample.createCriteria().andUserAccountEqualTo(username)
                    .andUserPasswordEqualTo(passwd);
            List<SysUser> listUser = sysUserService.listByExample(sysUserExample);
            if (CollectionUtils.isNotEmpty(listUser)) {
                SysUser sysUser = listUser.get(0);
                SecuritySessionUtil.init().setAttribute(AuthConst.SESSION_USER, sysUser);
                return new SimpleAuthenticationInfo(username, password, getName());
            }
        }
        return null;
    }
}
