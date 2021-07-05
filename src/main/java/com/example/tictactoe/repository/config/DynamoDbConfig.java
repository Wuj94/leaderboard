package com.example.tictactoe.repository.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientAsyncConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.internal.client.DefaultDynamoDbAsyncIndex;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.SdkEventLoopGroup;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.dynamodb.endpoint}")
    private String awsDynamoDBEndPoint;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(AwsCredentialsProvider awsCredentialsProvider,
                                                   @Value("${aws.dynamodb.endpoint:}") String dynamoDbEndPointUrl,
                                                   @Value("${aws.dynamodb.region:eu-west-1}") String awsRegion) {
        DynamoDbAsyncClientBuilder builder = DynamoDbAsyncClient.builder()
            .credentialsProvider(awsCredentialsProvider)
            .httpClientBuilder(NettyNioAsyncHttpClient.builder()
                .maxConcurrency(1000)
                .eventLoopGroupBuilder(SdkEventLoopGroup.builder()
                    .numberOfThreads(Math.max(Runtime.getRuntime().availableProcessors() * 10, 24))))
            .asyncConfiguration(ClientAsyncConfiguration.builder()
                .advancedOption(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR, Runnable::run)
                .build())
            .region(Region.of(awsRegion));

        if (StringUtils.hasLength(dynamoDbEndPointUrl)) {
            builder = builder.endpointOverride(URI.create(dynamoDbEndPointUrl));
        }

        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(dynamoDbAsyncClient)
            .build();
    }

    @Bean
    public DynamoDbAsyncTable<PlayerRecord> playersTable(
        DynamoDbEnhancedAsyncClient asyncClient, @Value("${database.table}") String tableName) {

        return asyncClient.table(tableName, TableSchema.fromClass(PlayerRecord.class));
    }

    @Bean
    public DefaultDynamoDbAsyncIndex<PlayerRecord> playersTableGsiToFilterByRank(
        DynamoDbEnhancedAsyncClient asyncClient, @Value("${database.table}") String tableName) {

        return (DefaultDynamoDbAsyncIndex<PlayerRecord>)
            asyncClient.table(tableName, TableSchema.fromClass(PlayerRecord.class))
                .index(PlayerRecord.BETWEEN_RANKS_INDEX);
    }
}