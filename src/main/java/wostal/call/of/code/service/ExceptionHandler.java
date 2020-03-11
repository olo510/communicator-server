package wostal.call.of.code.service;

import wostal.call.of.code.abstracts.AbstractException;

public class ExceptionHandler {
	
	public static String handle(Exception exception) {
		exception.printStackTrace();
		if(exception instanceof AbstractException) {
			return ((AbstractException)exception).getMessage();
		}else {
			return "Nieoczekiwany b³¹d";
		}
	}

}
