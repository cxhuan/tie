package com.chelaile.auth.base;

public class City extends BaseModel {

	private static final long serialVersionUID = 1L;

	private String cityId;

	private String cityEn;

	private String cityCn;

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityEn() {
		return cityEn;
	}

	public void setCityEn(String cityEn) {
		this.cityEn = cityEn;
	}

	public String getCityCn() {
		return cityCn;
	}

	public void setCityCn(String cityCn) {
		this.cityCn = cityCn;
	}
}