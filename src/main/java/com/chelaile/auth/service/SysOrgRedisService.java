package com.chelaile.auth.service;

import com.chelaile.auth.model.entity.SysOrg;

import java.util.List;

/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/13 20:39
 */
public interface SysOrgRedisService {

    List<SysOrg> getByUserId(Integer userId);

    void add(List<SysOrg> orgList, Integer userId);

    void deleteByUser(Integer userId);

}
