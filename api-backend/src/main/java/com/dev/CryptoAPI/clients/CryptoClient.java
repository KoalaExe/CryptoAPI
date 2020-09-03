package com.dev.CryptoAPI.clients;

import com.dev.CryptoAPI.dto.CurrencyDataDTO;
import com.dev.CryptoAPI.dto.CurrencyHistoryDTO;
import com.dev.CryptoAPI.dto.CurrencyMarketDTO;
import com.dev.CryptoAPI.dto.StatusUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "currencydata", url = "${external-api.url}")
public interface CryptoClient {

    @GetMapping("/coins/{id}")
    CurrencyDataDTO getCurrencyData(@PathVariable String id);

    @GetMapping("/coins/{id}/history")
    CurrencyHistoryDTO getCurrencyHistory(@PathVariable String id, @RequestParam(name = "date") String date);

    @GetMapping("/coins/markets")
    List<CurrencyMarketDTO> getCurrencyMarketData(@RequestParam(name = "vs_currency") String currency, @RequestParam(name = "per_page") String perPage, @RequestParam(name = "page") String page);

    @GetMapping("/coins/{id}/status_updates")
    StatusUpdateDTO getStatusUpdates(@PathVariable String id);
}
