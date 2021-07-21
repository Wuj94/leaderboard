package com.example.tictactoe.repository.config;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
@AllArgsConstructor
@Data
public class PlayerRecord {

    @Getter(onMethod_= {@DynamoDbPartitionKey})
    private UUID id;

    @Getter(onMethod_= {@DynamoDbSecondaryPartitionKey(indexNames = {BETWEEN_RANKS_INDEX})})
    private String game;

    @Getter(onMethod_= {@DynamoDbSecondarySortKey(indexNames = {BETWEEN_RANKS_INDEX})})
    private int pos;

    private String username;


    private int score;

    public static final String BETWEEN_RANKS_INDEX = "between_ranks_index";

    public PlayerRecord() {
        this.pos = -1;
    }
}
