package com.example.tictactoe.api.dto;

import com.example.tictactoe.repository.config.PlayerRecord;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@Builder(builderMethodName = "internalBuilder", toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchScoreRankResponse {

    @NonNull
    @lombok.Builder.Default
    private ArrayList<ScoreRankDto> scoreAndRanks = new ArrayList<>();

    public static SearchScoreRankResponse fromListOfPlayerRecord(List<PlayerRecord> playerRecord) {
        ArrayList<ScoreRankDto> result = new ArrayList<>();
        playerRecord.forEach(el -> result.add(ScoreRankDto.from(el)));
        return new SearchScoreRankResponse(result);
    }

}
