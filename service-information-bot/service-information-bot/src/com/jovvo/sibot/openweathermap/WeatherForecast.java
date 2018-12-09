package com.jovvo.sibot.openweathermap;

import java.io.Serializable;

public class WeatherForecast implements Serializable {

	private String forecast;

	public String getForecast() {
		return forecast;
	}

	public void setForecast(String forecast) {
		this.forecast = forecast;
	}

}
