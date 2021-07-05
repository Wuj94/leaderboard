package com.example.tictactoe.api.handler;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.equalTo;

import com.example.tictactoe.api.ApiRouterConfiguration;
import com.example.tictactoe.repository.config.PlayerRecord;
import com.example.tictactoe.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import( {ApiRouterConfiguration.class, AdminHandler.class, PlayerService.class})
public class AdminHandlerTest {

    @Autowired
    private WebTestClient client;
    @MockBean
    private PlayerService service;


    @Test
    public void search_players_in_ranks_returns_list_of_players() {
        List<PlayerRecord> list = new ArrayList<>();
        list.add(new PlayerRecord(UUID.fromString("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0"), "alwaysMe", 2, "bob", 18));
        list.add(new PlayerRecord(UUID.fromString("9e441c44-e4cb-459d-ab98-c193dc2eff49"), "alwaysMe", 3, "mic", 17));

        when(service.getNeighborhood(anyInt(),anyInt())).thenReturn(Mono.just(list));

        client.get()
            .uri(ApiRouterConfiguration.PLAYERS_BASE_RESOURCE + String.format("?fromRank=%s&toRank=%s", 2, 3))
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .exchange().expectStatus().isOk()
//            .expectBody(String.class)
//            .consumeWith(str -> System.out.println(str));
            .expectBody()
            .jsonPath("$.scoreAndRanks.[0].score").value(equalTo(18))
            .jsonPath("$.scoreAndRanks.[1].score").value(equalTo(17))
            .jsonPath("$.scoreAndRanks.[0].rank").exists()
            .jsonPath("$.scoreAndRanks.[0].rank").exists()
            .jsonPath("$.scoreAndRanks.[0].id").doesNotExist();
    }

    @Test
    public void search_players_is_bad_request_for_negative_rank_parameters() {

        client.get()
            .uri(ApiRouterConfiguration.PLAYERS_BASE_RESOURCE + String.format("?fromRank=%s&toRank=%s", -2, 3))
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .exchange().expectStatus().isBadRequest();

        client.get()
            .uri(ApiRouterConfiguration.PLAYERS_BASE_RESOURCE + String.format("?fromRank=%s&toRank=%s", 2, -3))
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .exchange().expectStatus().isBadRequest();

        client.get()
            .uri(ApiRouterConfiguration.PLAYERS_BASE_RESOURCE + String.format("?fromRank=%s&toRank=%s", -2, -3))
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .exchange().expectStatus().isBadRequest();
    }

    @Test
    public void search_players_is_bad_request_for_missing_rank_parameters() {

        client.get()
            .uri(ApiRouterConfiguration.PLAYERS_BASE_RESOURCE + String.format("?fromRank=%s", 3))
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .exchange().expectStatus().isBadRequest();

        client.get()
            .uri(ApiRouterConfiguration.PLAYERS_BASE_RESOURCE + String.format("?fromRank=%s", 3))
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .exchange().expectStatus().isBadRequest();

    }
}
