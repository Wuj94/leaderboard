package com.example.tictactoe.api.dto;

import com.example.tictactoe.repository.config.PlayerRecord;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class ScoreRankDto {
    @NonNull
    private Integer score = 0;

    @NonNull
    private Integer rank = 0;

    public static ScoreRankDto from(PlayerRecord playerRecord) {
        return new ScoreRankDto(playerRecord.getScore(), playerRecord.getPos());
    }
}
