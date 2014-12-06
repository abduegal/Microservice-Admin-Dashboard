package com.aegal.framework.core;

import com.aegal.framework.core.exceptions.ServiceCallException;
import com.aegal.framework.core.exceptions.ServiceHTTPException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import java.io.IOException;

/**
 * Custom wrapper to build a feign object:
 * Created by vagrant on 11/29/14.
 */
public class FeignBuilder {

    private final ObjectMapper objectMapper;
    private final boolean IN_DEBUG = false;
    private boolean serialize = true; //adds decoder and encoder.

    public FeignBuilder(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public FeignBuilder(ObjectMapper objectMapper, boolean serialize) {
        this.objectMapper = objectMapper;
        this.serialize = serialize;
    }

    public <T> T build(String address, Class<T> clazz) {
        Feign.Builder builder = Feign.builder()
                .requestInterceptor(getRequestInterceptor())
                .errorDecoder(getErrorDecoder());

        if (serialize) {
            builder.decoder(new JacksonDecoder(objectMapper))
                   .encoder(new JacksonEncoder(objectMapper));
        }
        if (IN_DEBUG) {
            builder.logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL);
        }

        return builder.target(clazz, address);
    }

    private ErrorDecoder getErrorDecoder() {
        return new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                try {
                    return new ServiceHTTPException(Util.toString(response.body().asReader()));
                } catch (IOException io) {
                    throw new ServiceHTTPException();
                }
            }
        };
    }

    private RequestInterceptor getRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("Content-Type", "Application/JSON");
            }
        };
    }
}
