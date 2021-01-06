package com.currency.services;

import com.currency.feign.OpenExchangeClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public class OpenExchangeCurrencyService implements CurrencyService {

	@Value("${OpenExchange.api.key}")
	private String appId;

	@Value("${currency.base}")
	private String base;

	@Autowired
	private OpenExchangeClient client;


	//openexchange в случае ошибки, например неправильного ключа, возвращает ответ с кодом 200 и json
	//в случае отсутствия соединения feign бросит исключение
	@Override
	public Double getTodayCurrency(String currencyCode) throws Exception {
		ResponseEntity<String> response = client.getTodayCurrencies(appId, base);
		return mapCode(response.getBody(), currencyCode);
	}

	@Override
	public Double getYesterdayCurrency(String currencyCode) throws Exception {
		ResponseEntity<String> response = client.getYesterdayCurrencies(LocalDate.now().minusDays(1).toString(), appId,
				base);
		return mapCode(response.getBody(), currencyCode);
	}

	private Double mapCode(String json, String code) throws JsonProcessingException, NullPointerException {
		ObjectMapper om = new ObjectMapper();
		return om.readTree(json).get("rates").get(code).asDouble();
	}

}
