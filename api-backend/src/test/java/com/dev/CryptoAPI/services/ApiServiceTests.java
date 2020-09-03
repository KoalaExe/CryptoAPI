package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.clients.CryptoClient;
import com.dev.CryptoAPI.dto.CurrencyDataDTO;
import com.dev.CryptoAPI.dto.CurrencyHistoryDTO;
import com.dev.CryptoAPI.dto.CurrencyMarketDTO;
import com.dev.CryptoAPI.dto.StatusUpdateDTO;
import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApiServiceTests {

    private static final String BITCOIN = "bitcoin";
    private static final String INVALID_CURRENCY = "bitcoi";
    private static final String USD = "usd";

    private static final int VALID_LIMIT = 10;
    private static final int INVALID_LIMIT = 15;
    private static final int PAGE_NUMBER_ONE = 1;

    @Mock
    private CryptoClient mockCryptoClient;

    private ApiService apiService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        apiService = new ApiService(mockCryptoClient);
    }

    @Test
    public void test_get_currency_data_with_valid_currency() throws Exception {
        CurrencyDataDTO currencyDataDTO = new CurrencyDataDTO();

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        String lastWeekString = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(lastWeek);

        currencyDataDTO.setId("bitcoin");
        currencyDataDTO.setSymbol("btc");
        currencyDataDTO.setName("Bitcoin");
        currencyDataDTO.setGenesis_date("2009-01-03");
        currencyDataDTO.setLast_updated("2020-09-02T01:24:18.219Z");

        Map<String, Map<String, ? extends Number>> market_data = new HashMap<>();

        Map<String, Long> marketCaps = new HashMap<>();
        marketCaps.put("usd", 219549868922L);

        market_data.put("market_cap", marketCaps);

        Map<String, Double> currentPrices = new HashMap<>();
        currentPrices.put("aud", 16175.85D);
        currentPrices.put("usd", 11898.14D);
        currentPrices.put("jpy", 1261834D);
        currentPrices.put("btc", 1.0D);

        market_data.put("current_price", currentPrices);

        Map<String, Double> priceChanges = new HashMap<>();
        priceChanges.put("aud", 2.73808D);
        priceChanges.put("usd", 2.40044D);
        priceChanges.put("jpy", 2.65895D);
        priceChanges.put("btc", 0.0D);

        market_data.put("price_change_percentage_24h_in_currency", priceChanges);

        currencyDataDTO.setMarket_data(market_data);

        CurrencyHistoryDTO currencyHistoryDTO = new CurrencyHistoryDTO();

        Map<String, Map<String, ? extends Number>> historicMarketData = new HashMap<>();

        Map<String, Double> historicCurrentPrices = new HashMap<>();
        historicCurrentPrices.put("aud", 15757.388389597621D);
        historicCurrentPrices.put("usd", 11350.753473213D);
        historicCurrentPrices.put("jpy", 1207339.9193085101D);
        historicCurrentPrices.put("btc", 1.0D);

        historicMarketData.put("current_price", historicCurrentPrices);

        currencyHistoryDTO.setMarket_data(historicMarketData);

        when(mockCryptoClient.getCurrencyData(BITCOIN)).thenReturn(currencyDataDTO);
        when(mockCryptoClient.getCurrencyHistory(BITCOIN, lastWeekString)).thenReturn(currencyHistoryDTO);

        CurrencyData currencyData = apiService.getCurrencyData(BITCOIN);

        assertEquals(BITCOIN, currencyData.getId());
        assertEquals("btc", currencyData.getSymbol());
        assertEquals("Bitcoin", currencyData.getName());
        assertEquals("219549868922", currencyData.getMarketCap());
        assertEquals("03-01-2009", currencyData.getGenesisDate());
        assertEquals("02-09-2020", currencyData.getLastUpdate());

        assertEquals("16175.85", currencyData.getCurrentPrices().get("aud"));
        assertEquals("11898.14", currencyData.getCurrentPrices().get("usd"));
        assertEquals("1261834.0", currencyData.getCurrentPrices().get("jpy"));
        assertEquals("1.0", currencyData.getCurrentPrices().get("btc"));

        assertEquals("2.73808", currencyData.getPricePercentageChange().get("aud"));
        assertEquals("2.40044", currencyData.getPricePercentageChange().get("usd"));
        assertEquals("2.65895", currencyData.getPricePercentageChange().get("jpy"));
        assertEquals("0.0", currencyData.getPricePercentageChange().get("btc"));

        assertEquals("15757.388389597621", currencyData.getLastWeekPrice().get("aud"));
        assertEquals("11350.753473213", currencyData.getLastWeekPrice().get("usd"));
        assertEquals("1207339.9193085101", currencyData.getLastWeekPrice().get("jpy"));
        assertEquals("1.0", currencyData.getLastWeekPrice().get("btc"));
    }

    @Test
    public void test_get_currency_data_with_invalid_currency_id_throws_currency_not_found_exception() throws Exception {
        when(mockCryptoClient.getCurrencyData(INVALID_CURRENCY)).thenThrow(FeignException.class);

        CurrencyNotFoundException notFoundException = assertThrows(CurrencyNotFoundException.class, () -> {
            apiService.getCurrencyData(INVALID_CURRENCY);
        });

        assertTrue(notFoundException.getMessage().contains(INVALID_CURRENCY + " was not found!"));
    }

    @Test
    public void test_get_paginated_data_with_valid_currency_id_limit_page_api_request() throws Exception {
        String USER_TITLE = "Operations Director";
        String DESCRIPTION_ONE = "For all you Atari flashback fans out there, get ready to use Litecoin for everything from games to hotels. Today we announce our newest partnership with Atari! Litecoin will also become an option for purchasing the much-anticipated new Atari VCS gaming console at a discount! \r\n\r\nFor more info see our blog: litecoin-foundation.org/atari/";
        String DESCRIPTION_TWO = "Litecoin Foundation Partners With MeconCash, Enabling Fiat Withdrawal At Over 13,000 ATMs Across South Korea.\r\n\r\n👇 Read More\r\nhttps://litecoin-foundation.org/litecoin-foundation-partners-with-meconcash-enabling-fiat-withdrawal-at-over-13000-atms-across-south-korea/";
        String CREATED_DATE_ONE = "2020-05-21T12:15:08.418Z";
        String CREATED_DATE_TWO = "2020-03-02T13:08:21.379Z";

        List<CurrencyMarketDTO> data = new ArrayList<>();
        StatusUpdateDTO bitcoinStatusUpdateDTO = new StatusUpdateDTO();
        StatusUpdateDTO litecoinStatusUpdateDTO = new StatusUpdateDTO();

        CurrencyMarketDTO btcData = new CurrencyMarketDTO();
        btcData.setId("bitcoin");
        btcData.setCurrent_price(11753.6D);
        btcData.setMarket_cap(217332541768L);

        data.add(btcData);

        CurrencyMarketDTO liteData = new CurrencyMarketDTO();
        liteData.setId("litecoin");
        liteData.setCurrent_price(62.23D);
        liteData.setMarket_cap(4066289130L);

        data.add(liteData);

        when(mockCryptoClient.getCurrencyMarketData("usd", "10", "1")).thenReturn(data);

        bitcoinStatusUpdateDTO.setStatus_updates(new ArrayList<>());
        when(mockCryptoClient.getStatusUpdates("bitcoin")).thenReturn(bitcoinStatusUpdateDTO);

        List<Map<String, Object>> litecoinStatusUpdates = new ArrayList<>();
        Map<String, Object> statusUpdateOne = new HashMap<>();
        statusUpdateOne.put("user_title", USER_TITLE);
        statusUpdateOne.put("description", DESCRIPTION_ONE);
        statusUpdateOne.put("created_at", CREATED_DATE_ONE);
        litecoinStatusUpdates.add(statusUpdateOne);

        Map<String, Object> statusUpdateTwo = new HashMap<>();
        statusUpdateTwo.put("user_title", USER_TITLE);
        statusUpdateTwo.put("description", DESCRIPTION_TWO);
        statusUpdateTwo.put("created_at", CREATED_DATE_TWO);
        litecoinStatusUpdates.add(statusUpdateTwo);

        litecoinStatusUpdateDTO.setStatus_updates(litecoinStatusUpdates);

        when(mockCryptoClient.getStatusUpdates("litecoin")).thenReturn(litecoinStatusUpdateDTO);

        List<PaginatedCurrencyData> paginatedCurrencyDataList = apiService.getPaginatedCurrencyDataList(USD, VALID_LIMIT, PAGE_NUMBER_ONE);

        assertEquals(2, paginatedCurrencyDataList.size());

        assertEquals("bitcoin", paginatedCurrencyDataList.get(0).getId());
        assertEquals("$11753.6", paginatedCurrencyDataList.get(0).getCurrentPrice());
        assertEquals("$217332541768", paginatedCurrencyDataList.get(0).getMarketCap());
        assertEquals(0, paginatedCurrencyDataList.get(0).getStatusUpdates().size());

        assertEquals("litecoin", paginatedCurrencyDataList.get(1).getId());
        assertEquals("$62.23", paginatedCurrencyDataList.get(1).getCurrentPrice());
        assertEquals("$4066289130", paginatedCurrencyDataList.get(1).getMarketCap());
        assertEquals(2, paginatedCurrencyDataList.get(1).getStatusUpdates().size());

        assertEquals(USER_TITLE, paginatedCurrencyDataList.get(1).getStatusUpdates().get(0).get("title"));
        assertEquals(DESCRIPTION_ONE, paginatedCurrencyDataList.get(1).getStatusUpdates().get(0).get("description"));
        assertEquals("21-05-2020", paginatedCurrencyDataList.get(1).getStatusUpdates().get(0).get("createdAt"));

        assertEquals(USER_TITLE, paginatedCurrencyDataList.get(1).getStatusUpdates().get(1).get("title"));
        assertEquals(DESCRIPTION_TWO, paginatedCurrencyDataList.get(1).getStatusUpdates().get(1).get("description"));
        assertEquals("02-03-2020", paginatedCurrencyDataList.get(1).getStatusUpdates().get(1).get("createdAt"));
    }

    @Test
    public void test_get_paginated_data_with_invalid_currency_throws_currency_exception() {
        Exception invalidCurrencyException = assertThrows(Exception.class, () -> {
            apiService.getPaginatedCurrencyDataList(INVALID_CURRENCY, VALID_LIMIT, PAGE_NUMBER_ONE);
        });

        assertTrue(invalidCurrencyException.getMessage().contains("Invalid currency! Use usd, aud or jpy"));
    }

    @Test
    public void test_get_paginated_data_with_invalid_limit_throws_exception() {
        Exception invalidCurrencyException = assertThrows(Exception.class, () -> {
            apiService.getPaginatedCurrencyDataList(USD, INVALID_LIMIT, PAGE_NUMBER_ONE);
        });

        assertTrue(invalidCurrencyException.getMessage().contains("Pagination limit out of range!"));
    }
}
