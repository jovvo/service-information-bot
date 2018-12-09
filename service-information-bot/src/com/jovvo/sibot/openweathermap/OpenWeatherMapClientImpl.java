package com.jovvo.sibot.openweathermap;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class OpenWeatherMapClientImpl  implements OpenWeatherMapClient {
	Logger LOGGER = LoggerFactory.getLogger(OpenWeatherMapClientImpl.class);
	
	private String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=845af4d9cc0745a0f63e4b85740d7aea&q=:city&type=like&sort=population&cnt=1";

	@Override
	public ResponseEntity<OpenWeatherForecastResponse> fetchForecast(String city) {
		
		RestTemplate restTemplate = createRestTemplate();	
		OpenWeatherForecastResponse response = null;
	    
	    final RequestCallback requestCallback = new RequestCallback() {
	        @Override
	        public void doWithRequest(final ClientHttpRequest request) throws IOException {
	        	request.getHeaders().setExpires(0);
	        	request.getHeaders().setCacheControl("no-cache, no-store, max-age=0, must-revalidate");
	        	request.getHeaders().setPragma("no-cache");
	        }
	    };
	    
		try {
			response = restTemplate.execute(OPEN_WEATHER_MAP_URL.replace(":city", city), 
		    		HttpMethod.GET, requestCallback, new ResponseFromHeadersExtractor());
		} catch (Exception e) {
			if (e instanceof HttpStatusCodeException) {
				String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
				LOGGER.error(errorResponse);
			}
			if (e instanceof HttpClientErrorException) {
				String errorResponse = ((HttpClientErrorException) e).getResponseBodyAsString();
				LOGGER.error(errorResponse);
			}
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	private RestTemplate createRestTemplate() {
	    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
	    RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	
	
	private class ResponseFromHeadersExtractor implements ResponseExtractor<OpenWeatherForecastResponse> {

	    @Override
	    public OpenWeatherForecastResponse extractData(ClientHttpResponse response) throws IOException {
	    	LOGGER.debug("StringFromHeadersExtractor - response headers: " + response.getHeaders());
	        
	        String responseAsString = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
	        LOGGER.debug("Response body: {} " + responseAsString);
			
			ObjectMapper objectMapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();
			module.addDeserializer(OpenWeatherForecastResponse.class, new WeatherForecastDeserializer());
			objectMapper.registerModule(module);
			
			OpenWeatherForecastResponse openWeatherForecastResponse = 
		    	objectMapper.readValue(responseAsString, OpenWeatherForecastResponse.class);

			return openWeatherForecastResponse;
	    }
	}
	
}





