package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ApiService {

    private static final String API_BASE_URL = "http://localhost:9999";
    private static final String API_CURRENCY_URI = "/coins/";
    private static final String AUS_DATE_FORMAT = "dd-MM-yyyy";
    private static final DateTimeFormatter AUS_DATE_FORMATTER = DateTimeFormatter.ofPattern(AUS_DATE_FORMAT);
    private static final List<String> requiredCurrencies = new ArrayList<>();

    static {
        requiredCurrencies.add("aud");
        requiredCurrencies.add("usd");
        requiredCurrencies.add("jpy");
        requiredCurrencies.add("btc");
    }

    public CurrencyData getCurrencyData(String currencyId) throws Exception {
        CurrencyData currencyData = new CurrencyData(currencyId);

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        String lastWeekString = AUS_DATE_FORMATTER.format(lastWeek);

        Map<String, String> lastPriceParams = new HashMap<>();
        lastPriceParams.put("date", lastWeekString);

        WebClient.RequestHeadersSpec<?> currencyDataURI = createApiRequest(currencyId, new HashMap<>());
        WebClient.RequestHeadersSpec<?> lastPriceURI = createApiRequest(currencyId + "/history", lastPriceParams);

        try {
            String currencyDataResponse = currencyDataURI
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            String lastPriceResponse = lastPriceURI
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            processCurrencyDataJSON(currencyData, currencyDataResponse);
            processLastPriceData(currencyData, lastPriceResponse);
        } catch(WebClientException e) {
            throw new CurrencyNotFoundException(currencyId + " was not found!");
        }

        return currencyData;
    }

    private WebClient.RequestHeadersSpec<?> createApiRequest(String requestData, Map<String, String> params) {
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
                .uri(uriBuilder -> {
                    UriBuilder buildingUri = uriBuilder.path(API_CURRENCY_URI + requestData);

                    Set<String> paramKeys = params.keySet();

                    for(String paramKey : paramKeys) {
                        buildingUri = buildingUri.queryParam(paramKey, params.get(paramKey));
                    }

                    return buildingUri.build();
                });

        return apiURI;
    }

    private void processCurrencyDataJSON(CurrencyData currencyData, String jsonData) throws Exception {
        if(currencyData == null) {
            throw new Exception("Currency data is null!");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode parsedObj = mapper.readTree(jsonData);

        String currencyId = parsedObj.get("id").textValue();

        if(currencyId == null || !currencyId.equals(currencyData.getId())) {
            throw new Exception("No currency found!");
        }

        String symbol = parsedObj.get("symbol").textValue();
        String name = parsedObj.get("name").textValue();
        String marketCap = Long.toString(parsedObj.get("market_data").get("market_cap").get("usd").asLong());

        LocalDate genesisDate = LocalDate.parse(parsedObj.get("genesis_date").textValue());
        LocalDate lastUpdate = LocalDateTime.ofInstant(Instant.parse(parsedObj.get("last_updated").textValue()), ZoneId.of(ZoneOffset.UTC.getId())).toLocalDate();

        currencyData.setSymbol(symbol);
        currencyData.setName(name);
        currencyData.setMarketCap(marketCap);

        currencyData.setGenesisDate(AUS_DATE_FORMATTER.format(genesisDate));
        currencyData.setLastUpdate(AUS_DATE_FORMATTER.format(lastUpdate));

        for(String currentCurrency : requiredCurrencies) {
            String currentPrice = Double.toString(parsedObj.get("market_data").get("current_price").get(currentCurrency).asDouble());
            String priceChange = Double.toString(parsedObj.get("market_data").get("price_change_percentage_24h_in_currency").get(currentCurrency).asDouble());

            currencyData.getCurrentPrices().put(currentCurrency, currentPrice);
            currencyData.getPricePercentageChange().put(currentCurrency, priceChange);
        }
    }

    private void processLastPriceData(CurrencyData currencyData, String jsonData) throws Exception {
        if(currencyData == null) {
            throw new Exception("Currency data is null!");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode parsedObj = mapper.readTree(jsonData);

        String currencyId = parsedObj.get("id").textValue();

        if(currencyId == null || !currencyId.equals(currencyData.getId())) {
            throw new Exception("No currency found!");
        }

        for(String currentCurrency : requiredCurrencies) {
            String lastWeekPrice = Double.toString(parsedObj.get("market_data").get("current_price").get(currentCurrency).asDouble());
            currencyData.getLastWeekPrice().put(currentCurrency, lastWeekPrice);
        }
    }
}
