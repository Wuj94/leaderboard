package com.example.tictactoe.repository;

import com.example.tictactoe.repository.config.PlayerRecord;
import java.util.List;
import reactor.core.publisher.Mono;

public interface PlayerRepository {
    Mono<List<PlayerRecord>> getNeighborhood(int lowRank, int highRank);
}
