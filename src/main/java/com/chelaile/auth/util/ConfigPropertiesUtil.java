package com.chelaile.auth.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chelaile.auth.base.City;
import com.chelaile.auth.base.LogObj;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mchange.v1.util.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ConfigPropertiesUtil extends LogObj {

	public static Map<String, Object> config = Maps.newHashMap();

	public static String defaultCities = "[{\"cityId\":\"008\",\"cityEn\":\"dongguan\",\"cityCn\":\"东莞\"},{\"cityId\":\"028\",\"cityEn\":\"jiaxing\",\"cityCn\":\"嘉兴\"},"
			+ "{\"cityId\":\"019\",\"cityEn\":\"foshan\",\"cityCn\":\"佛山\"},{\"cityId\":\"006\",\"cityEn\":\"tianjin\",\"cityCn\":\"天津\"},"
			+ "{\"cityId\":\"100\",\"cityEn\":\"yinchuan\",\"cityCn\":\"银川\"},{\"cityId\":\"021\",\"cityEn\":\"zhongshan\",\"cityCn\":\"中山\"},"
			+ "{\"cityId\":\"074\",\"cityEn\":\"sanya\",\"cityCn\":\"三亚\"},{\"cityId\":\"014\",\"cityEn\":\"shenzhen\",\"cityCn\":\"深圳\"},"
			+ "{\"cityId\":\"049\",\"cityEn\":\"huhehaote\",\"cityCn\":\"呼和浩特\"},{\"cityId\":\"093\",\"cityEn\":\"taian\",\"cityCn\":\"泰安\"},"
			+ "{\"cityId\":\"107\",\"cityEn\":\"panjin\",\"cityCn\":\"盘锦\"},{\"cityId\":\"007\",\"cityEn\":\"chengdu\",\"cityCn\":\"成都\"},"
			+ "{\"cityId\":\"888\",\"cityEn\":\"demo\",\"cityCn\":\"demo\"}]";

	public static void setConfigMap(Map<String, Object> _config) {
		config = _config;
	}

	public static String getValue(String name) {
		if (StringUtils.isBlank(name))
			return null;
		return config.get(name) == null ? null : config.get(name).toString();
	}

	public static String getValue(String name, String defaultStr) {
		if (StringUtils.isBlank(name))
			return defaultStr;
		return config.get(name) == null ? defaultStr : config.get(name).toString();
	}

	public static List<City> getSupportCities() {
		if (config == null) {
			return Lists.newArrayList();
		}

		String cityJson = getValue("supportCities", defaultCities);
		JSONArray cityArr = JSON.parseArray(cityJson);
		List<City> cities = Lists.newArrayList();
		for (Object obj : cityArr) {
			City cityObj = JSONArray.parseObject(obj.toString(), City.class);
			cities.add(cityObj);
		}
		return cities;
	}

	public static City getCity(String cityId) {
		for (City c : getSupportCities()) {
			if (c.getCityId().equals(cityId)) {
				return c;
			}
		}
		return null;
	}
}