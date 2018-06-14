package com.chelaile.auth.dao;

import com.chelaile.auth.model.entity.SysAuth;
import com.chelaile.auth.model.entity.SysMenu;
import com.chelaile.auth.model.entity.SysOrg;
import com.chelaile.auth.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;
@Mapper
public interface DiyMapper {


    List<SysMenu> getMenuByUserIdAndOrgId(@Param("userId") Integer userId, @Param("orgId") Integer orgId,
                                          @Param("status") Integer status);

    List<SysMenu> getMenuByOrgId(@Param("orgId") Integer orgId, @Param("status") Integer status);

    List<SysOrg> getOrgByUser(@Param("userId") Integer userId);

    List<SysOrg> getOrgByMenuId(@Param("menuId") Integer menuId);

    List<SysUser> getSysUserByAuthId(@Param("authId") Integer authId);

    List<SysAuth> getSysAuthByMenuId(@Param("menuId") Integer menuId);

    List<SysAuth> getSysAuthByUserId(@Param("userId") Integer userId);

}
