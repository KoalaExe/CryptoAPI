package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Service
public class ApiService {

    private static final String API_BASE_URL = "https://api.coingecko.com/api/v3";
    private static final String API_CURRENCY_URI = "/coins/";

    public CurrencyData getCurrencyData(String currencyId) throws Exception {
        CurrencyData currencyData = new CurrencyData(currencyId);

        TcpClient tcpClient = TcpClient
            .create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

        WebClient coinGeckoClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .baseUrl(API_BASE_URL)
            .build();

        WebClient.RequestHeadersSpec<?> apiURI = coinGeckoClient.get()
            .uri(API_CURRENCY_URI + currencyId);

        try {
            String apiResponse = apiURI
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            System.out.println(apiResponse);
        } catch(WebClientException e) {
            throw new CurrencyNotFoundException(currencyId + " was not found!");
        }

        return currencyData;
    }
}
