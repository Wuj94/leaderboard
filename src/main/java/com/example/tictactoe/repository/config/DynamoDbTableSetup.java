package com.example.tictactoe.repository.config;

import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import java.util.List;
import software.amazon.awssdk.services.dynamodb.model.Projection;

@Log4j2
@Component
public class DynamoDbTableSetup {
    private final DynamoDbAsyncClient client;
    private final DynamoDbEnhancedAsyncClient enhancedClient;
    private final String tableName;

    public DynamoDbTableSetup(DynamoDbAsyncClient client,
                              DynamoDbEnhancedAsyncClient enhancedClient,
                              @Value("${database.table}") String tableName) {
        this.client = client;
        this.enhancedClient = enhancedClient;
        this.tableName = tableName;
        run();
    }

    public void run() {
        try {
            final List<String> tableNames = client.listTables().get().tableNames();
            DynamoDbAsyncTable<PlayerRecord> dynamoTable = enhancedClient.table(tableName, TableSchema.fromClass(PlayerRecord.class));
            if (!tableNames.contains(tableName)) {
                dynamoTable.createTable(getEnhancedRequestForGSIs()).get();
            }

            //Loading test dataset
            dynamoTable.putItem(new PlayerRecord(UUID.fromString("3a4a1ed5-5785-480e-b9a4-72b45dace2d5"), "alwaysMe", 1, "wuj", 22)).get();
            dynamoTable.putItem(new PlayerRecord(UUID.fromString("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0"), "alwaysMe", 2, "bob", 18)).get();
            dynamoTable.putItem(new PlayerRecord(UUID.fromString("9e441c44-e4cb-459d-ab98-c193dc2eff49"), "alwaysMe", 3, "mic", 17)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CreateTableEnhancedRequest getEnhancedRequestForGSIs() {
        return CreateTableEnhancedRequest.builder()
            .globalSecondaryIndices(
                EnhancedGlobalSecondaryIndex.builder()
                    .indexName(PlayerRecord.BETWEEN_RANKS_INDEX)
                    .projection(Projection.builder().projectionType("ALL").build())
                    .build())
            .build();
    }

}
