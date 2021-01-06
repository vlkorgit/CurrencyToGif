package com.currency.services;

import ch.qos.logback.core.joran.spi.ConsoleTarget;
import com.currency.feign.GiphyClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

public class GiphyGifService implements GifService {

	@Autowired
	GiphyClient giphyClient;
	@Value("${Giphy.api.key}")
	private String apiKey;

	@Override
	public String getRandomRich() throws Exception{
		ResponseEntity<String> response = giphyClient.getRandomByTag("rich", apiKey);
		return mapOriginal(response.getBody());
	}

	@Override
	public String getRandomFail() throws Exception {
		ResponseEntity<String> response = giphyClient.getRandomByTag("fail", apiKey);
		return mapOriginal(response.getBody());
	}

	private String mapOriginal(String json) throws JsonProcessingException, NullPointerException {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(json).get("data").get("images").get("original").get("url").asText();
	}
}
