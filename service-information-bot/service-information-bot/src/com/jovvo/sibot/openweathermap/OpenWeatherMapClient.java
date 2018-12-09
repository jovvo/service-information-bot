package com.jovvo.sibot.openweathermap;

import org.springframework.http.ResponseEntity;

public interface OpenWeatherMapClient {
	public ResponseEntity<OpenWeatherForecastResponse> fetchForecast(String city);
	

}
