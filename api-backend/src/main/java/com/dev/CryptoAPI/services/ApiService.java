package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.clients.CryptoClient;
import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.dto.CurrencyDataDTO;
import com.dev.CryptoAPI.dto.CurrencyHistoryDTO;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ApiService {

    private final String API_BASE_URL;
    private final CryptoClient cryptoClient;
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

    public ApiService(@Value("${external-api.url}") String apiUrl, CryptoClient cryptoClient) {
        API_BASE_URL = apiUrl;
        this.cryptoClient = cryptoClient;
    }

    public List<PaginatedCurrencyData> getPaginatedCurrencyDataList(String currency, int limit, int pageNumber) throws Exception {
        Map<String, String> symbolMap = new HashMap<>();

        symbolMap.put("usd", "$");
        symbolMap.put("aud", "$");
        symbolMap.put("jpy", "¥");

        if(limit < 1 || limit > 10) {
            throw new Exception("Pagination limit out of range!");
        }

        if(!requiredCurrencies.contains(currency) || currency.equals("btc")) {
            throw new CurrencyNotFoundException("Invalid currency! Use usd, aud or jpy");
        }

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("vs_currency", currency);
        requestParams.put("per_page", Integer.toString(limit));
        requestParams.put("page", Integer.toString(pageNumber));

        WebClient.RequestHeadersSpec<?> paginatedCurrencyDataURI = createApiRequest("markets", requestParams);

        String currencyDataResponse = paginatedCurrencyDataURI
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return processPaginatedData(currencyDataResponse, symbolMap.get(currency));

    }

    public CurrencyData getCurrencyData(String currencyId) throws Exception {
        CurrencyData currencyData = new CurrencyData(currencyId);

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        String lastWeekString = AUS_DATE_FORMATTER.format(lastWeek);

        try {
            CurrencyDataDTO currencyDataDTO = cryptoClient.getCurrencyData(currencyId);
            CurrencyHistoryDTO currencyHistoryDTO = cryptoClient.getCurrencyHistory(currencyId, lastWeekString);

            currencyData.setSymbol(currencyDataDTO.getSymbol());
            currencyData.setName(currencyDataDTO.getName());
            currencyData.setMarketCap(Long.toString(((Map<String, Long>)currencyDataDTO.getMarket_data().get("market_cap")).get("usd")));

            LocalDate genesisDate = LocalDate.parse(currencyDataDTO.getGenesis_date());
            currencyData.setGenesisDate(AUS_DATE_FORMATTER.format(genesisDate));

            LocalDate lastUpdate = LocalDateTime.ofInstant(Instant.parse(currencyDataDTO.getLast_updated()), ZoneId.of(ZoneOffset.UTC.getId())).toLocalDate();
            currencyData.setLastUpdate(AUS_DATE_FORMATTER.format(lastUpdate));

            for(String currentCurrency : requiredCurrencies) {
                currencyData.getCurrentPrices().put(currentCurrency, Double.toString(((Map<String, Number>)currencyDataDTO.getMarket_data().get("current_price")).get(currentCurrency).doubleValue()));
                currencyData.getPricePercentageChange().put(currentCurrency, Double.toString(((Map<String, Number>)currencyDataDTO.getMarket_data().get("price_change_percentage_24h_in_currency")).get(currentCurrency).doubleValue()));
                currencyData.getLastWeekPrice().put(currentCurrency, Double.toString(currencyHistoryDTO.getMarket_data().get("current_price").get(currentCurrency).doubleValue()));
            }
        } catch(FeignException e) {
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

    private List<PaginatedCurrencyData> processPaginatedData(String jsonData, String currencySymbol) throws Exception {
        List<PaginatedCurrencyData> paginatedCurrencyDataList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode returnedObjects = mapper.readTree(jsonData);

        for(JsonNode data : returnedObjects) {
            String currencyId = data.get("id").textValue();
            PaginatedCurrencyData currencyData = new PaginatedCurrencyData(currencyId);

            currencyData.setCurrentPrice(currencySymbol + Double.toString(data.get("current_price").asDouble()));
            currencyData.setMarketCap(currencySymbol + Long.toString(data.get("market_cap").asLong()));
            currencyData.setStatusUpdates(getStatusUpdates(currencyId));

            paginatedCurrencyDataList.add(currencyData);
        }

        return paginatedCurrencyDataList;
    }

    private List<Map<String, String>> getStatusUpdates(String currencyId) throws Exception {
        WebClient.RequestHeadersSpec<?> statusUpdateURI = createApiRequest(currencyId + "/status_updates", new HashMap<>());

        String statusUpdateResponse = statusUpdateURI
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return processStatusUpdates(statusUpdateResponse);
    }

    private List<Map<String, String>> processStatusUpdates(String jsonData) throws Exception {
        List<Map<String, String>> statusUpdates = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseData = mapper.readTree(jsonData);
        JsonNode allStatusUpdates = responseData.get("status_updates");

        for(JsonNode currentStatusUpdate : allStatusUpdates) {
            HashMap<String, String> statusUpdate = new HashMap<>();

            LocalDate createDate = LocalDateTime.ofInstant(Instant.parse(currentStatusUpdate.get("created_at").textValue()), ZoneId.of(ZoneOffset.UTC.getId())).toLocalDate();

            statusUpdate.put("title", currentStatusUpdate.get("user_title").textValue());
            statusUpdate.put("description", currentStatusUpdate.get("description").textValue());
            statusUpdate.put("createdAt", AUS_DATE_FORMATTER.format(createDate));

            statusUpdates.add(statusUpdate);
        }

        return statusUpdates;
    }

    private void processCurrencyDataJSON(CurrencyData currencyData, String jsonData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode parsedObj = mapper.readTree(jsonData);

        String currencyId = parsedObj.get("id").textValue();

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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode parsedObj = mapper.readTree(jsonData);

        String currencyId = parsedObj.get("id").textValue();

        for(String currentCurrency : requiredCurrencies) {
            String lastWeekPrice = Double.toString(parsedObj.get("market_data").get("current_price").get(currentCurrency).asDouble());
            currencyData.getLastWeekPrice().put(currentCurrency, lastWeekPrice);
        }
    }
}
