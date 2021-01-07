package com.currency.web;

import com.currency.model.CurrencyToGifResponse;
import com.currency.services.CurrencyService;
import com.currency.services.GifService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
public class CurrencyToGifController {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    GifService gifService;

    @GetMapping("/api/{code}")
    public ResponseEntity<CurrencyToGifResponse> getCurrencyGifUrl(@PathVariable("code") String code) {
        try {
            Double today = currencyService.getTodayCurrency(code);
            Double yesterday = currencyService.getYesterdayCurrency(code);
            String answer;
            if (today > yesterday) {
                answer = gifService.getRandomRich();
            } else {
                answer = gifService.getRandomFail();
            }
            return new ResponseEntity<>(new CurrencyToGifResponse(false, "ok", answer), HttpStatus.OK);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(new CurrencyToGifResponse(true,
                    "Something wrong with external api", null),
                    HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(new CurrencyToGifResponse(true, "Wrong request parameters", null),
                    HttpStatus.OK);
        }
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<CurrencyToGifResponse> getCurrencyGifRedirectOrElseJson(@PathVariable("code") String code,
                                                                                  HttpServletResponse response) {
        ResponseEntity<CurrencyToGifResponse> result = getCurrencyGifUrl(code);
        if (!result.getBody().error) {
            try {
                response.sendRedirect(result.getBody().gifUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }
}