package com.currency.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Giphy", url = "${Giphy.uri.domain}")
public interface GiphyClient {
	@RequestMapping(path="${Giphy.uri.endpoints.random}")
	ResponseEntity<String> getRandomByTag(@RequestParam("tag") String tag,
	                                  @RequestParam(value = "api_key") String api_key );
}
