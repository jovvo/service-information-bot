package com.jovvo.sibot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.jovvo.sibot.exception.UnknownClientException;
import com.jovvo.sibot.openweathermap.OpenWeatherMapClient;
import com.jovvo.sibot.openweathermap.OpenWeatherMapClientImpl;

public class ServiceInformationBot extends TelegramLongPollingBot {

	public static String TELEGRAM_BOT_NAME = "ServiceInformationBot";
	
	/**
	 * Name of the bot is: ServiceInformation8122018Bot
	 */
	public static final String TELEGRAM_BOT_TOKEN = "790634344:AAFjCX8r-XX7GxuiRacLnWj09QoIHDmWomA";

	public static final String WELCOME_MESSAGE = "Hello, \nI am serviceinformation bot. \n What is your name?";
	public static final String HANDSHAKE_MESSAGE = ":clientName, \nplease enter your city location.";
	public static final String ASK_FOR_COUNTRY_MESSAGE = ":clientName, \nplease enter your country.";
	public static final String GOODBY_MESSAGE = "Nice to chat with you :clientName, \nenjoy your day.";
	
	public static final int CLIENT_STATE_WAITING_NAME = 0;
	public static final int CLIENT_STATE_WAITING_CITY = 1;
	public static final int CLIENT_STATE_WAITING_COUNTRY = 2;
	

	public static final String LOCAL_INFO_WEATHER = "WEATHER";
	public static final String LOCAL_INFO_NEWS = "NEWS";
	public static final String LOCAL_INFO_RESTAURANTS = "RESTAURANTS";
	
	public static final String LOCAL_INF_MESSAGE_WEATHER = ":clientName, \n here is weather for :city \n :weather";
	public static final String LOCAL_INF_MESSAGE_NEWS = ":clientName, \n here are some news related to :country \n :news";
	
	boolean handShake = false;

	private Map<Long, String> clientNames = new HashMap<Long, String>();
	private Map<Long, Integer> clientState = new HashMap<Long, Integer>();
	private Map<Long, String> clientLocation = new HashMap<Long, String>();
	private Map<Long, String> clientCountry = new HashMap<Long, String>();
		
	private Map<String, Map<String, String>> locationInformation = new HashMap<String, Map<String, String>>();
	
	private OpenWeatherMapClient openWeatherMapClient = new OpenWeatherMapClientImpl();
	
	@Override
	public void onUpdateReceived(Update update) {
		String clientName;
		if (!update.hasMessage() || !update.getMessage().hasText()) {
			return;
		}
		long chatId = update.getMessage().getChatId();
		
		try {
			clientName = resolveClientName(chatId);
		} catch (UnknownClientException e1) {
			if (clientState.get(chatId) == null) {
				sendMessage(chatId, WELCOME_MESSAGE);
				clientState.put(chatId, CLIENT_STATE_WAITING_NAME);
				return;				
			}
		}
		
		String messageText = update.getMessage().getText();

		switch (clientState.get(chatId)) {
		case CLIENT_STATE_WAITING_NAME:
			clientNames.put(chatId, messageText);
			sendMessage(chatId, HANDSHAKE_MESSAGE.replace(":clientName", clientNames.get(chatId)));
			clientState.put(chatId, CLIENT_STATE_WAITING_CITY);
			return;
		case CLIENT_STATE_WAITING_CITY:
			clientLocation.put(chatId, messageText);
			String forecast = fetchForecast(messageText);
			sendMessage(chatId, LOCAL_INF_MESSAGE_WEATHER
					.replace(":clientName", clientNames.get(chatId))
					.replace(":city", messageText)
					.replace(":weather", forecast));
			sendMessage(chatId, ASK_FOR_COUNTRY_MESSAGE.replace(":clientName", clientNames.get(chatId)));
			clientState.put(chatId, CLIENT_STATE_WAITING_COUNTRY);
			return;
		case CLIENT_STATE_WAITING_COUNTRY:
			clientCountry.put(chatId, messageText);
			String news = fetchNews(messageText);
			sendMessage(chatId, LOCAL_INF_MESSAGE_NEWS
					.replace(":clientName", clientNames.get(chatId))
					.replace(":country", messageText)
					.replace(":news", news));
			sendMessage(chatId, GOODBY_MESSAGE.replace(":clientName", clientNames.get(chatId)));
			resetBot();
		}
	}

	private void resetBot() {
		clientNames.clear();
		clientState.clear();
		clientLocation.clear();
		locationInformation.clear();
	}
		
	private void sendMessage(long chatId, String messageText) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(messageText);
		try {
			execute(message);
		} catch (TelegramApiException e) {
			//TODO log error
			e.printStackTrace();
		}
	}
	
	
	String fetchForecast(String location){
		String forecast = openWeatherMapClient.fetchForecast(location).getBody().getWeather();
		return forecast;
	}

	
	String fetchNews(String location){
		String forecast = openWeatherMapClient.fetchForecast(location).getBody().getWeather();
		return forecast;
	}
	
	private String resolveClientName(long chatId) throws UnknownClientException {
		if (clientNames.keySet().contains(chatId)) {
			return clientNames.get(chatId);
		}
		throw new UnknownClientException(chatId);
	}

	@Override
	public String getBotUsername() {
		return TELEGRAM_BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return TELEGRAM_BOT_TOKEN;
	}
}
