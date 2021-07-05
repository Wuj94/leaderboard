package com.example.tictactoe.api.handler;

import com.example.tictactoe.api.dto.SearchScoreRankResponse;
import com.example.tictactoe.exceptions.ScoreRankQueryBadParametersException;
import com.example.tictactoe.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
public class AdminHandler {
    private static final String FROM_RANK_QUERY_PARAM = "fromRank";
    private static final String TO_RANK_QUERY_PARAM = "toRank";

    private static final ServerWebInputException EXCEPTION_FROM_TO_RANK_INVALID =
        new ServerWebInputException("Invalid query params. Reason: fromRank must be less than toRank");

    private final PlayerService service;

    public AdminHandler(@Autowired PlayerService service) {
        this.service = service;
    }

    public Mono<ServerResponse> search(ServerRequest request) {
        Integer fromRankParameter = request.queryParam(FROM_RANK_QUERY_PARAM).map(Integer::parseInt).orElseThrow(() -> EXCEPTION_FROM_TO_RANK_INVALID);
        Integer toRankParameter = request.queryParam(TO_RANK_QUERY_PARAM).map(Integer::parseInt).orElseThrow(() -> EXCEPTION_FROM_TO_RANK_INVALID);

        return verifyInputValid(fromRankParameter, toRankParameter)
            .switchIfEmpty(Mono.error(EXCEPTION_FROM_TO_RANK_INVALID))
            .flatMap(fromRankParam -> service.getNeighborhood(fromRankParam, toRankParameter))
            .flatMap(resultSet -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Mono.just(SearchScoreRankResponse.fromListOfPlayerRecord(resultSet)), SearchScoreRankResponse.class)
                    );
    }

    private Mono<Integer> verifyInputValid(int fromRankParameter, int toRankParameter) {
        if(fromRankParameter < 1 || toRankParameter < 1)
            return Mono.empty();
        return Mono.just(fromRankParameter);
    }
}
