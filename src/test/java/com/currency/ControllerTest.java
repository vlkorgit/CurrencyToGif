package com.currency;

import com.currency.web.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ControllerTest {

	static WireMockServer wireMockServer = new WireMockServer();
	@Autowired
	Controller controller;

	@Value("${OpenExchange.uri.endpoints.latest}")
	String latestEndpoint;

	@Value("${OpenExchange.uri.endpoints.historical}")
	String historicalEndpoint;

	@Value("${Giphy.uri.endpoints.random}")
	String randomEndpoint;

	@Value("${Giphy.api.key}")
	String giphyKey;

	@BeforeAll
	static void init() {
		wireMockServer.start();
	}

	@AfterAll
	static void finish() {
		wireMockServer.stop();
	}


	@Test
	void controller_should_return_value_from_file() {
		try {
			historicalEndpoint = historicalEndpoint.replace("{date}", LocalDate.now().minusDays(1).toString());
			ObjectMapper mapper = new ObjectMapper();
			File yesterdayStubFile = ResourceUtils.getFile("classpath:json/yesterdayCurrenciesBaseUSD.json");
			File todayStubFile = ResourceUtils.getFile("classpath:json/todayCurrenciesBaseUSD.json");
			File randomRichFile = ResourceUtils.getFile("classpath:json/randomRich.json");
			File randomFailFile = ResourceUtils.getFile("classpath:json/randomFail.json");
			Double yesterdayValueFromFile = mapper.readTree(yesterdayStubFile).get("rates").get("RUB").asDouble();
			Double todayValueFromFile = mapper.readTree(todayStubFile).get("rates").get("RUB").asDouble();
			String imageUrl =
					mapper.readTree(randomFailFile).get("data").get("images").get("original").get("url").asText();
			stubFor(get(urlPathMatching(latestEndpoint + "([a-z]*)"))
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(todayStubFile.toPath()))));


			stubFor(get(urlPathMatching(historicalEndpoint + "([a-z]*)"))
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(yesterdayStubFile.toPath()))));

			stubFor(get(urlEqualTo(randomEndpoint+ "?tag=rich&api_key="+giphyKey) )
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(randomRichFile.toPath()))));

			stubFor(get(urlEqualTo(randomEndpoint+ "?tag=fail&api_key="+giphyKey))
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(randomFailFile.toPath()))));

			String result = controller.getCurrencyGifUrl("BYN").getBody().toString();
			Assert.assertEquals(mapper.readTree(result).get("gifUrl").asText(), imageUrl);

		} catch (Exception ex) {
			Assert.fail();
		}
	}

}
