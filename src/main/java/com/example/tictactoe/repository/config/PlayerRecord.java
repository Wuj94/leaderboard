package com.example.tictactoe.repository.config;

import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

//@Lombok
@DynamoDbBean
public class PlayerRecord {
    private UUID id;
    private String game;
    private int pos;
    private String username;
    private int score;

    public static final String BETWEEN_RANKS_INDEX = "between_ranks_index";

    public PlayerRecord() {
        this.pos = -1;
    }

    public PlayerRecord(UUID id, String game, int pos, String username, int score) {
        this.id = id;
        this.pos = pos;
        this.username = username;
        this.score = score;
        this.game = game;
    }

    @DynamoDbPartitionKey
    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    // GSI named 'between_ranks_index'
    @DynamoDbSecondarySortKey(indexNames = {BETWEEN_RANKS_INDEX})
    public int getPos() { return this.pos; }
    public void setPos(int pos) { this.pos = pos; }

    @DynamoDbSecondaryPartitionKey(indexNames = {BETWEEN_RANKS_INDEX})
    public String getGame() { return this.game; }
    public void setGame(String game) { this.game = game; }
    //

    public String getUsername() { return this.username; }
    public void setUsername(String username) { this.username = username; }

    public int getScore() { return this.score; }
    public void setScore(int score) { this.score = score; }
}
