package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.clients.CryptoClient;
import com.dev.CryptoAPI.dto.CurrencyMarketDTO;
import com.dev.CryptoAPI.dto.StatusUpdateDTO;
import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.dto.CurrencyDataDTO;
import com.dev.CryptoAPI.dto.CurrencyHistoryDTO;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ApiService {

    private final CryptoClient cryptoClient;
    private static final String AUS_DATE_FORMAT = "dd-MM-yyyy";
    private static final DateTimeFormatter AUS_DATE_FORMATTER = DateTimeFormatter.ofPattern(AUS_DATE_FORMAT);
    private static final List<String> requiredCurrencies = new ArrayList<>();
    private static final Map<String, String> symbolMap = new HashMap<>();

    static {
        requiredCurrencies.add("aud");
        requiredCurrencies.add("usd");
        requiredCurrencies.add("jpy");
        requiredCurrencies.add("btc");

        symbolMap.put("usd", "$");
        symbolMap.put("aud", "$");
        symbolMap.put("jpy", "¥");
    }

    @Autowired
    public ApiService(CryptoClient cryptoClient) {
        this.cryptoClient = cryptoClient;
    }

    public List<PaginatedCurrencyData> getPaginatedCurrencyDataList(String currency, int limit, int pageNumber) throws Exception {
        if(limit < 1 || limit > 10) {
            throw new Exception("Pagination limit out of range!");
        }

        if(!symbolMap.keySet().contains(currency)) {
            throw new CurrencyNotFoundException("Invalid currency! Use usd, aud or jpy");
        }

        List<PaginatedCurrencyData> paginatedCurrencyDataList = new ArrayList<>();
        List<CurrencyMarketDTO> marketsData = cryptoClient.getCurrencyMarketData(currency, Integer.toString(limit), Integer.toString(pageNumber));
        String currencySymbol = symbolMap.get(currency);

        for(CurrencyMarketDTO currentData : marketsData) {
            String currencyId = currentData.getId();
            Double currentPrice = currentData.getCurrent_price();
            Long marketCap = currentData.getMarket_cap();

            PaginatedCurrencyData paginatedCurrencyData = new PaginatedCurrencyData(currencyId);
            paginatedCurrencyData.setCurrentPrice(currencySymbol + Double.toString(currentPrice));
            paginatedCurrencyData.setMarketCap(currencySymbol + Long.toString(marketCap));

            StatusUpdateDTO statusUpdateDTO = cryptoClient.getStatusUpdates(currencyId);
            List<Map<String, String>> statusUpdates = new ArrayList<>();

            for(Map<String, Object> currentStatusUpdate : statusUpdateDTO.getStatus_updates()) {
                Map<String, String> statusUpdate = new HashMap<>();

                LocalDate createDate = LocalDateTime.ofInstant(Instant.parse((String)currentStatusUpdate.get("created_at")), ZoneId.of(ZoneOffset.UTC.getId())).toLocalDate();

                statusUpdate.put("title", (String)currentStatusUpdate.get("user_title"));
                statusUpdate.put("description", (String)currentStatusUpdate.get("description"));
                statusUpdate.put("createdAt", AUS_DATE_FORMATTER.format(createDate));

                statusUpdates.add(statusUpdate);
            }

            paginatedCurrencyData.setStatusUpdates(statusUpdates);

            paginatedCurrencyDataList.add(paginatedCurrencyData);
        }

        return paginatedCurrencyDataList;
    }

    public CurrencyData getCurrencyData(String currencyId) throws Exception {
        CurrencyData currencyData = new CurrencyData(currencyId);

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        String lastWeekString = AUS_DATE_FORMATTER.format(lastWeek);

        try {
            CurrencyDataDTO currencyDataDTO = cryptoClient.getCurrencyData(currencyId);
            CurrencyHistoryDTO currencyHistoryDTO = cryptoClient.getCurrencyHistory(currencyId, lastWeekString);

            currencyData.setId(currencyDataDTO.getId());
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
}
