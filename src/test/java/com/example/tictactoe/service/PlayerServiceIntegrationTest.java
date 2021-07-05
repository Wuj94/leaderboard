package com.example.tictactoe.service;


import static reactor.core.publisher.Mono.when;


import com.example.tictactoe.TestingSpringContext;
import com.example.tictactoe.repository.config.DynamoDbConfig;
import com.example.tictactoe.repository.config.DynamoDbPlayerRepository;
import com.example.tictactoe.repository.config.DynamoDbTableSetup;
import com.example.tictactoe.repository.config.PlayerRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestingSpringContext.class, DynamoDbConfig.class, DynamoDbTableSetup.class,
    DynamoDbPlayerRepository.class, PlayerService.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestPropertySource("classpath:application.properties")
public class PlayerServiceIntegrationTest {

    @Container
    public static GenericContainer<?> dynamoDbLocal = new GenericContainer<>(
        DockerImageName.parse("amazon/dynamodb-local"))
        .withExposedPorts(8000);

    @Autowired
    private PlayerService playerService;


    @DynamicPropertySource
    static void dynamoDBProperties(DynamicPropertyRegistry registry) {
        System.out.println("Started Dynamo on http://" + dynamoDbLocal.getHost() + ":" + dynamoDbLocal.getFirstMappedPort());
        registry.add("aws.dynamodb.endpoint", () -> "http://" + dynamoDbLocal.getHost() + ":" + dynamoDbLocal.getFirstMappedPort());
    }

    @Test
    public void getNeighboorhoodReturnsListOfPlayers() {
        List<PlayerRecord> list = new ArrayList<>();
        list.add(new PlayerRecord(UUID.fromString("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0"), "alwaysMe", 2, "bob", 18));
        list.add(new PlayerRecord(UUID.fromString("9e441c44-e4cb-459d-ab98-c193dc2eff49"), "alwaysMe", 3, "mic", 17));

        List<PlayerRecord> result = playerService.getNeighborhood(2,3).block();

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.get(0).getId().toString().equals("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0") ||
            result.get(0).getId().toString().equals("9e441c44-e4cb-459d-ab98-c193dc2eff49"));
        Assert.assertTrue(result.get(1).getId().toString().equals("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0") ||
            result.get(1).getId().toString().equals("9e441c44-e4cb-459d-ab98-c193dc2eff49"));
    }

    @Test
    public void getNeighboorhoodReturnsEmptyListOfPlayers() {
        List<PlayerRecord> result = playerService.getNeighborhood(4,7).block();

        Assert.assertEquals(0, result.size());
    }

}
