package com.example.tictactoe.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.tictactoe.TestingSpringContext;
import com.example.tictactoe.repository.config.DynamoDbConfig;
import com.example.tictactoe.repository.config.DynamoDbPlayerRepository;
import com.example.tictactoe.repository.config.DynamoDbTableSetup;
import com.example.tictactoe.repository.config.PageToRecordConverter;
import com.example.tictactoe.repository.config.PlayerRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
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
import reactor.test.StepVerifier;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestingSpringContext.class, DynamoDbConfig.class, DynamoDbTableSetup.class,
    DynamoDbPlayerRepository.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestPropertySource("classpath:application.properties")
public class PlayerRepositoryIntegrationTest {

    @Container
    public static GenericContainer<?> dynamoDbLocal = new GenericContainer<>(
        DockerImageName.parse("amazon/dynamodb-local"))
        .withExposedPorts(8000);

    @Resource(name = "dynamoDbPlayerRepository")
    private DynamoDbPlayerRepository dynamoDbPlayerRepository;

    @DynamicPropertySource
    static void dynamoDBProperties(DynamicPropertyRegistry registry) {
        System.out.println("Started Dynamo on http://" + dynamoDbLocal.getHost() + ":" + dynamoDbLocal.getFirstMappedPort());
        registry.add("aws.dynamodb.endpoint", () -> "http://" + dynamoDbLocal.getHost() + ":" + dynamoDbLocal.getFirstMappedPort());
    }

    @Test
    public void get_score_and_rank_from_a_section_of_the_leaderboard_returns_correct_players() {
        //To test the 1st iteration PoC, I'm exploiting the default dataset to actually verify
        //the correctness of the PoC.
        //Default dataset includes 3 shippers, 2 of these are in (10,20).
        //        Test the 3rd one is not included
        //        Test the result set is not empty

        final Mono<List<PlayerRecord>> result = dynamoDbPlayerRepository.getNeighborhood(2, 3);

        // wait a bit
        List<PlayerRecord> list = new ArrayList<>();
        list.add(new PlayerRecord(UUID.fromString("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0"), "alwaysMe", 2, "bob", 18));
        list.add(new PlayerRecord(UUID.fromString("9e441c44-e4cb-459d-ab98-c193dc2eff49"), "alwaysMe", 3, "mic", 17));

        StepVerifier.create(result)
            .expectNextMatches(checkResult()).verifyComplete();
    }

    @Test
    public void get_score_and_rank_from_a_section_of_the_leaderboard_returns_no_players() {
        //To test the 1st iteration PoC, I'm exploiting the default dataset to actually verify
        //the correctness of the PoC.
        //Default dataset includes 3 shippers, 2 of these are in (10,20).
        //        Test the 3rd one is not included
        //        Test the result set is not empty

        final Mono<List<PlayerRecord>> result = dynamoDbPlayerRepository.getNeighborhood(7, 8);

        StepVerifier.create(result).expectNextMatches(List::isEmpty)
            .verifyComplete();
    }

    private Predicate<? super List<PlayerRecord>> checkResult() {
        return new Predicate<List<PlayerRecord>>() {
            @Override
            public boolean test(List<PlayerRecord> playerRecords) {
                return playerRecords.size() == 2 &&
                    (playerRecords.get(0).getId().toString().equals("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0") ||
                        playerRecords.get(0).getId().toString().equals("9e441c44-e4cb-459d-ab98-c193dc2eff49")) &&
                    (playerRecords.get(1).getId().toString().equals("d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0") ||
                        playerRecords.get(1).getId().toString().equals("9e441c44-e4cb-459d-ab98-c193dc2eff49"));
            }
        };
    }

    @Test
    public void test_db_connection() {
        Assert.assertEquals(dynamoDbPlayerRepository.ciaoDynamo(), true);
    }

}
