package com.currency;

import com.currency.services.GifService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class GiphyServiceTest {

	static WireMockServer wireMockServer = new WireMockServer();

	@Autowired
	GifService gifService;


	@BeforeAll
	static void startMockServer() {
		wireMockServer.start();
	}

	@AfterAll
	static void shutDownMockServer() {
		wireMockServer.stop();
	}

	@Test
	void rich_gif_url_from_service_should_be_equal_from_file() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File stubFile = ResourceUtils.getFile("classpath:json/randomRich.json");
			String richUrlFromFile =
					mapper.readTree(stubFile).get("data").get("images").get("original").get("url").asText();
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			String richUrlFromService = gifService.getRandomRich();
			Assert.assertEquals(richUrlFromFile, richUrlFromService);
		} catch (Exception ex) {
			Assert.fail();
		}
	}

	@Test
	void fail_gif_url_from_service_should_be_equal_from_file() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File stubFile = ResourceUtils.getFile("classpath:json/randomFail.json");
			String failUrlFromFile =
					mapper.readTree(stubFile).get("data").get("images").get("original").get("url").asText();
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			String failFromUrl = gifService.getRandomRich();
			Assert.assertEquals(failFromUrl, failFromUrl);
		} catch (Exception ex) {
			Assert.fail();
		}
	}

	@Test
	void getRandomFailMethod_response_with_fault_should_throw(){
		try {
			stubFor(any(anyUrl()).willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
			gifService.getRandomFail();
			Assert.fail();
		}
		catch (Exception exception){
			//ok then
		}
	}

	@Test
	void getRandomRichMethod_response_with_fault_should_throw(){
		try {
			stubFor(any(anyUrl()).willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
			gifService.getRandomRich();
			Assert.fail();
		}
		catch (Exception exception){
			//ok then
		}
	}

	@Test
	void getRandomRichMethod_response_with_wrong_json(){
		try {
			File stubFile = ResourceUtils.getFile("classpath:json/someJson.json");
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			String randomRich = gifService.getRandomRich();
			Assert.fail();
		} catch (Exception ex) {
			//ok then
		}
	}

	@Test
	void getRandomFailMethod_response_with_wrong_json(){
		try {
			File stubFile = ResourceUtils.getFile("classpath:json/someJson.json");
			stubFor(any(anyUrl())
					.willReturn(aResponse()
							.withHeader("Content-Type", "application/json")
							.withBody(Files.readAllBytes(stubFile.toPath()))));
			String randomFail = gifService.getRandomFail();
			Assert.fail();
		} catch (Exception ex) {
			//ok then
		}
	}
}
