package com.currency;

import com.currency.services.CurrencyService;
import com.currency.services.GifService;
import com.currency.services.GiphyGifService;
import com.currency.services.OpenExchangeCurrencyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class CurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyApplication.class, args);
	}

	@Bean
	CurrencyService getCurrencyService(){
		return new OpenExchangeCurrencyService();
	}

	@Bean
	GifService getGifService(){
		return new GiphyGifService();
	}
}
