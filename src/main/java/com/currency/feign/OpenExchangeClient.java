package com.currency.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "OpenExchange", url = "${OpenExchange.uri.domain}")
public interface OpenExchangeClient {
	@RequestMapping(path = "${OpenExchange.uri.endpoints.latest}", method = RequestMethod.GET)
	ResponseEntity<String> getTodayCurrencies(@RequestParam("app_id") String app_id, @RequestParam("code") String code);

	@RequestMapping(path = "${OpenExchange.uri.endpoints.historical}", method = RequestMethod.GET)
	ResponseEntity<String> getYesterdayCurrencies(@PathVariable("date") String date,
	                                            @RequestParam("app_id") String app_id,
			@RequestParam("code") String code);
}
