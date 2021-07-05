package com.example.tictactoe.service;

import com.example.tictactoe.exceptions.ScoreRankQueryBadParametersException;
import com.example.tictactoe.repository.PlayerRepository;
import com.example.tictactoe.repository.config.PlayerRecord;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    PlayerService(@Autowired PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Mono<List<PlayerRecord>> getNeighborhood(int lowPosition, int highPosition) {
        return verifyInput(lowPosition, highPosition)
            .switchIfEmpty(Mono.error(new ScoreRankQueryBadParametersException()))
            .flatMap(low -> playerRepository.getNeighborhood(low, highPosition));
    }

    private Mono<Integer> verifyInput(int lowPosition, int highPosition) {
        if(lowPosition <= highPosition && lowPosition > 0 && highPosition > 0) {
            return Mono.just(lowPosition);
        }
        return Mono.empty();
    }

}