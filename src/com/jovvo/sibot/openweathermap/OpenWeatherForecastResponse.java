package com.jovvo.sibot.openweathermap;

import java.io.Serializable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class OpenWeatherForecastResponse implements Serializable {
		
	private String weather;

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	
}