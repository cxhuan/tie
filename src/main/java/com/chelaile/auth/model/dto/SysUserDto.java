package com.chelaile.auth.model.dto;

import com.chelaile.auth.model.entity.SysOrg;

import java.util.List;

public class SysUserDto {


	private Integer id;

	private String userAccount;

	private String userName;

	private List<SysOrg> orgList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<SysOrg> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<SysOrg> orgList) {
		this.orgList = orgList;
	}
}