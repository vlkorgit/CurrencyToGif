package com.currency.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonAutoDetect
@Data
@RequiredArgsConstructor
public final class CurrencyToGifResponse {
    private final Boolean error;
    private final String message;
    private final String gifUrl;
    private final String baseCurrencyCode;
    private final String targetCurrencyCode;
    private final Double todayCurrencyRate;
    private final Double yesterdayCurrencyRate;

}