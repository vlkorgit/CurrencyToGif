package com.currency.services;

public interface CurrencyService {
	Double getTodayCurrency(String baseCode) throws Exception;

	Double getYesterdayCurrency(String baseCode) throws Exception;
}
