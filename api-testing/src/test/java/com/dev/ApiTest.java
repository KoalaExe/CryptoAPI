package com.dev;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class ApiTest
{
    private static final String BASE_URI = "http://localhost:8080";
    private static final String BITCOIN = "bitcoin";
    private static final String INVALID_COIN = "bitcoi";
    private static final String USD = "usd";
    private static final String LIMIT = "10";
    private static final String PAGE = "1";

    @BeforeAll
    public void init() {
        baseURI = BASE_URI;
    }

    @Test
    public void test_that_get_currency_header_is_application_json() {
        get("/coins/" + BITCOIN).then().contentType("application/json");
    }

    @Test
    public void that_get_currency_response_time_is_OK() {
        get("/coins/" + BITCOIN).then().time(lessThan(1000L));
    }

    @Test
    public void test_that_api_returns_200_and_stubbed_currency_info_data_in_correct_format_for_valid_currency() {
        get("/coins/" + BITCOIN)
            .then()
            .assertThat().statusCode(200)
            .body("id", equalTo("bitcoin"))
            .body("symbol", equalTo("btc"))
            .body("name", equalTo("Bitcoin"))
            .body("marketCap", equalTo("225371848920"))
            .body("genesisDate", equalTo("03-01-2009"))
            .body("lastUpdate", equalTo("18-08-2020"));
    }

    @Test
    public void test_that_api_returns_200_and_stubbed_current_prices_data_in_correct_format_for_valid_currency() {
        get("/coins/" + BITCOIN)
            .then()
            .assertThat().statusCode(200)
            .body("currentPrices.aud", equalTo("16868.56"))
            .body("currentPrices.usd", equalTo("12207.18"))
            .body("currentPrices.jpy", equalTo("1287797.0"))
            .body("currentPrices.btc", equalTo("1.0"));
    }

    @Test
    public void test_that_api_returns_200_and_stubbed_price_percentage_change_data_in_correct_format_for_valid_currency() {
        get("/coins/" + BITCOIN)
            .then()
            .assertThat().statusCode(200)
            .body("pricePercentageChange.aud", equalTo("1.86061"))
            .body("pricePercentageChange.usd", equalTo("2.57275"))
            .body("pricePercentageChange.jpy", equalTo("1.81386"))
            .body("pricePercentageChange.btc", equalTo("0.0"));
    }

    @Test
    public void test_that_api_returns_200_and_stubbed_last_week_price_data_in_correct_format_for_valid_currency() {
        get("/coins/" + BITCOIN)
            .then()
            .assertThat().statusCode(200)
            .body("lastWeekPrice.aud", equalTo("15951.186295908086"))
            .body("lastWeekPrice.usd", equalTo("11398.671060896633"))
            .body("lastWeekPrice.jpy", equalTo("1213955.2112711808"))
            .body("lastWeekPrice.btc", equalTo("1.0"));
    }

    @Test
    public void test_that_api_returns_404_for_invalid_currency() {
        get("/coins/" + INVALID_COIN)
            .then()
            .assertThat()
            .statusCode(404)
            .body("message", equalTo("bitcoi was not found!"));
    }

    @Test
    public void test_that_markets_endpoint_header_is_application_json() {
        get("/coins/markets?vs_currency=" + USD + "&limit=" + LIMIT + "&page=" + PAGE).then().contentType("application/json");
    }

    @Test
    public void that_markets_endpoint_response_time_is_OK() {
        get("/coins/markets?vs_currency=" + USD + "&limit=" + LIMIT + "&page=" + PAGE).then().time(lessThan(1000L));
    }

    @Test
    public void test_that_markets_endpoint_returns_200_and_stubbed_data_has_10_items() {
        get("/coins/markets?vs_currency=" + USD + "&limit=" + LIMIT + "&page=" + PAGE)
            .then()
            .assertThat().statusCode(200)
            .body("size()", is(10));
    }

    @Test
    public void test_that_markets_endpoint_returns_200_and_has_correct_bitcoin_object() {
        get("/coins/markets?vs_currency=" + USD + "&limit=" + LIMIT + "&page=" + PAGE)
            .then()
            .assertThat().statusCode(200)
            .body("[0].id", equalTo("bitcoin"))
            .body("[0].currentPrice", equalTo("$12259.8"))
            .body("[0].marketCap", equalTo("$226351166421"))
            .body("[0].statusUpdates.size()", is(0));
    }

    @Test
    public void test_that_markets_endpoint_returns_200_and_stubbed_data_has_correct_crypto_currencies() {
        get("/coins/markets?vs_currency=" + USD + "&limit=" + LIMIT + "&page=" + PAGE)
            .then()
            .assertThat().statusCode(200)
            .body("id", hasItems("bitcoin", "ethereum", "bitcoin-cash", "ripple", "tether", "chainlink", "cardano", "litecoin", "bitcoin-cash-sv", "eos"));
    }

    @Test
    public void test_that_markets_endpoint_returns_200_and_stubbed_data_has_currencies_if_using_default_page_number() {
        get("/coins/markets?vs_currency=" + USD + "&limit=" + LIMIT)
            .then()
            .assertThat().statusCode(200)
            .body("id", hasItems("bitcoin", "ethereum", "bitcoin-cash", "ripple", "tether", "chainlink", "cardano", "litecoin", "bitcoin-cash-sv", "eos"));
    }

    @Test
    public void test_that_markets_endpoint_returns_200_and_stubbed_data_has_currencies_if_using_default_limit() {
        get("/coins/markets?vs_currency=" + USD + "&page=" + PAGE)
            .then()
            .assertThat().statusCode(200)
            .body("id", hasItems("bitcoin", "ethereum", "bitcoin-cash", "ripple", "tether", "chainlink", "cardano", "litecoin", "bitcoin-cash-sv", "eos"));
    }

    @Test
    public void test_that_markets_endpoint_returns_200_and_stubbed_data_has_currencies_if_using_default_limit_and_page_number() {
        get("/coins/markets?vs_currency=" + USD)
            .then()
            .assertThat().statusCode(200)
            .body("id", hasItems("bitcoin", "ethereum", "bitcoin-cash", "ripple", "tether", "chainlink", "cardano", "litecoin", "bitcoin-cash-sv", "eos"));
    }

    @Test
    public void test_that_markets_endpoint_returns_404_if_not_given_vs_currency_parameter() {
        get("/coins/markets?limit=" + LIMIT + "&page=" + PAGE)
            .then()
            .assertThat()
            .statusCode(400);
    }


}
