package com.example.tictactoe;

import com.example.tictactoe.repository.PlayerRepository;
import com.example.tictactoe.repository.config.DynamoDbPlayerRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
@Log4j2
/**
 * This is a Spring Context for testing.<br/>
 * Should hold beans (or override beans) to work for tests
 */
@Profile("localDynamo")
public class TestingSpringContext {

    @Bean
    @Primary
    public AwsCredentialsProvider testAwsCredentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create("test", "test"));
    }

}
