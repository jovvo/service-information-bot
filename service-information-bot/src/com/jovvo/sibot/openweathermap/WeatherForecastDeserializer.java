package com.jovvo.sibot.openweathermap;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class WeatherForecastDeserializer extends StdDeserializer<OpenWeatherForecastResponse> {
	Logger LOGGER = LoggerFactory.getLogger(WeatherForecastDeserializer.class);

	public WeatherForecastDeserializer() {
		this(null);
	}

	public WeatherForecastDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public OpenWeatherForecastResponse deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		JsonNode node = jp.getCodec().readTree(jp);
		String weather = node.get("list").get(0).get("weather").get(0).get("description").asText();
		LOGGER.debug(weather);
		OpenWeatherForecastResponse weatherForecast = new OpenWeatherForecastResponse();
		weatherForecast.setWeather(weather);
		return weatherForecast;
	}

}
