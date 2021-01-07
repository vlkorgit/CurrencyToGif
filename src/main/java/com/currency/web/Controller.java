package com.currency.web;

import com.currency.services.CurrencyService;
import com.currency.services.GifService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class Controller {

	@Autowired
	CurrencyService currencyService;

	@Autowired
	GifService gifService;

	@GetMapping("/api/{code}")
	public ResponseEntity<Object> getCurrencyGifUrl(@PathVariable("code") String code) {
		try {
			Double today = currencyService.getTodayCurrency(code);
			Double yesterday = currencyService.getYesterdayCurrency(code);
			String answer;
			if (today > yesterday) {
				answer = gifService.getRandomRich();
			} else {
				answer = gifService.getRandomFail();
			}
			return new ResponseEntity<Object>(new ControllerResponse(false, "ok", answer), HttpStatus.OK);
		} catch (FeignException feignException) {
			return new ResponseEntity<Object>(new ControllerResponse(true,
					"Something wrong with external api", null),
					HttpStatus.OK);
		} catch (Exception exception) {
			return new ResponseEntity<Object>(new ControllerResponse(true, "Wrong request parameters", null),
					HttpStatus.OK);
		}
	}

	@GetMapping("/redirect/{code}")
	public ResponseEntity<Object> getCurrencyGifRedirect(@PathVariable("code") String code,
	                                                     HttpServletResponse response) {
		try {
			Double today = currencyService.getTodayCurrency(code);
			Double yesterday = currencyService.getYesterdayCurrency(code);
			String answer;
			if (today > yesterday) {
				answer = gifService.getRandomRich();
			} else {
				answer = gifService.getRandomFail();
			}
			response.sendRedirect(answer);
			return new ResponseEntity<Object>(new ControllerResponse(false, "ok", answer), HttpStatus.OK);
		} catch (FeignException feignException) {
			return new ResponseEntity<Object>(new ControllerResponse(true,
					"Something wrong with external api",
					null),
					HttpStatus.OK);
		} catch (Exception exception) {
			return new ResponseEntity<Object>(new ControllerResponse(true, "Wrong request parameters", null),
					HttpStatus.OK);
		}
	}
}

@JsonAutoDetect
class ControllerResponse {
	public Boolean error;
	public String message;
	public String gifUrl;

	public ControllerResponse(Boolean error, String message, String gifUrl) {
		this.error = error;
		this.message = message;
		this.gifUrl = gifUrl;
	}

	@Override
	public String toString() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(this);
		} catch (Exception exception) {
			return exception.getMessage();
		}
	}
}