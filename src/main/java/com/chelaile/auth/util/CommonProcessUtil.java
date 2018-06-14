package com.chelaile.auth.util;


import com.chelaile.auth.base.City;
import com.chelaile.auth.base.LogObj;
import com.chelaile.auth.model.entity.SysOrg;

import java.util.List;

public class CommonProcessUtil extends LogObj {

	public static void handleCityCn(List<SysOrg> list) {
		for (SysOrg s : list) {
			City city = ConfigPropertiesUtil.getCity(s.getCityId());
			s.setCityCn(city == null ? null : city.getCityCn());
		}
	}
}
