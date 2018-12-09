package com.jovvo.sibot.exception;


public class UnknownClientException extends Exception {
	   private long chatId;
	   
	   public UnknownClientException(long chatId) {
	      this.chatId = chatId;
	   }
	   
	   public long getChatId() {
	      return chatId;
	   }
	}