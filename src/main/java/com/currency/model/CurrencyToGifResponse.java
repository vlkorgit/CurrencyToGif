package com.currency.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonAutoDetect
public class CurrencyToGifResponse {
    public Boolean error;
    public String message;
    public String gifUrl;

    public CurrencyToGifResponse(Boolean error, String message, String gifUrl) {
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