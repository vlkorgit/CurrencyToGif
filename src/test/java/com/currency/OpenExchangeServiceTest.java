package com.currency;

import com.currency.services.OpenExchangeCurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenExchangeServiceTest {

	@Autowired
	OpenExchangeCurrencyService service;

	static WireMockServer wireMockServer = new WireMockServer();

	@BeforeAll
	static void startServer() {
		wireMockServer.start();
	}

	@AfterAll
	static void stopServer() {
		wireMockServer.stop();
	}

	@Value("${OpenExchange.uri.endpoints.latest}")
	String latestEndpoint;

	@Value("{currency.base}")
	String currencyBase;

	@Test
	void today_currency_from_service_should_be_equal_currency_from_file() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File stubFile = ResourceUtils.getFile("classpath:json/todayCurrenciesBaseUSD.json");
			Double fromFile = mapper.readTree(stubFile).get("rates").get("RUB").asDouble();
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			Double fromService = service.getTodayCurrency("RUB");
			Assert.assertEquals(fromService, fromFile);
		} catch (Exception ex) {
			Assert.fail();
		}
	}

	@Test
	void yesterday_currency_from_service_should_be_equal_currency_from_file() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			//File stubFile = ResourceUtils.getFile("classpath:json/yesterdayCurrenciesBaseUSD.json");
			File stubFile = new File("json/yesterdayCurrenciesBaseUSD.json");
			Assert.assertFalse(stubFile.exists());
			Assert.fail();
			Double fromFile = mapper.readTree(stubFile).get("rates").get("RUB").asDouble();
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			Double fromService = service.getYesterdayCurrency("RUB");
			Assert.assertEquals(fromService, fromFile);
		} catch (Exception ex) {
			Assert.fail();
		}
	}

	@Test
	void getYesterdayCurrencyMethod_should_throw_on_wrong_json(){
		try {
			File stubFile = ResourceUtils.getFile("classpath:json/someJson.json");
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			Double result = service.getYesterdayCurrency("RUB");
			Assert.fail();
		} catch (Exception ex) {
			//ok then
		}
	}

	@Test
	void getTodayCurrencyMethod_should_throw_on_wrong_json(){
		try {
			File stubFile = ResourceUtils.getFile("classpath:json/someJson.json");
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			Double result = service.getTodayCurrency("RUB");
			Assert.fail();
		} catch (Exception ex) {
			//ok then
		}
	}

	@Test
	void getTodayCurrencyMethod_should_throw_on_bad_response(){
		try {
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
			Double result = service.getTodayCurrency("RUB");
			Assert.fail();
		} catch (Exception ex) {
			//ok then
		}
	}

	@Test
	void getYesterdayCurrencyMethod_should_throw_on_bad_response(){
		try {
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
			Double result = service.getYesterdayCurrency("RUB");
			Assert.fail();
		} catch (Exception ex) {
			//ok then
		}
	}
}