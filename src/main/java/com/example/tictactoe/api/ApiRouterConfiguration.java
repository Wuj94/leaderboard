package com.example.tictactoe.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


import com.example.tictactoe.api.handler.AdminHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ApiRouterConfiguration {
    public static final String PLAYERS_BASE_RESOURCE = "/admin/players";

    @Bean
    RouterFunction<ServerResponse> routes(AdminHandler handler) {
        return route(GET(PLAYERS_BASE_RESOURCE), handler::search);
    }
}
