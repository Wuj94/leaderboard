package com.example.tictactoe.repository.config;


import com.example.tictactoe.repository.PlayerRepository;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.internal.client.DefaultDynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Log4j2
@Repository
public class DynamoDbPlayerRepository implements PlayerRepository {

    private final DynamoDbAsyncTable<PlayerRecord> playerDynamoDbAsyncTable;
    private final DynamoDbEnhancedAsyncClient enhancedClient;
    private DefaultDynamoDbAsyncIndex<PlayerRecord> gsiBetweenRanksIndex;


    public DynamoDbPlayerRepository(DynamoDbEnhancedAsyncClient enhancedClient,
                                    @Qualifier("playersTableGsiToFilterByRank") DefaultDynamoDbAsyncIndex<PlayerRecord> gsiBetweenRanksIndex,
                                    @Value("${database.table}") String tableName) {
        this.enhancedClient = enhancedClient;
        this.gsiBetweenRanksIndex = gsiBetweenRanksIndex;
        //        log.info("Starting Repository with DynamoDb table name: {}", tableName);
        this.playerDynamoDbAsyncTable = enhancedClient.table(tableName, TableSchema.fromClass(PlayerRecord.class));
    }

    public boolean ciaoDynamo() {
        return true;
    }

 //
////    aws dynamodb query \
////        --table-name players \
////        --key-condition-expression "score = :score AND rank BETWEEN :lower_rank AND :upper_rank" \
////        --expression-attribute-values :score = ${my score} AND :lower_rank = 10 AND :upper_rank = 20 \
    @Override
    public Mono<List<PlayerRecord>> getNeighborhood(int lowRank, int highRank) {
        final PageToRecordConverter converter = new PageToRecordConverter();
        gsiBetweenRanksIndex.query(QueryEnhancedRequest.builder().queryConditional(
            QueryConditional.sortBetween(Key.builder().partitionValue("alwaysMe").sortValue(lowRank).build(),
                Key.builder().partitionValue("alwaysMe").sortValue(highRank).build())).build()).subscribe(converter);
        return Mono.from(converter);
    }

}
