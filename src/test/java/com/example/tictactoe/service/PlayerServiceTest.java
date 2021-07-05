package com.example.tictactoe.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static reactor.core.publisher.Mono.when;


import com.example.tictactoe.api.ApiRouterConfiguration;
import com.example.tictactoe.exceptions.ScoreRankQueryBadParametersException;
import com.example.tictactoe.repository.PlayerRepository;
import com.example.tictactoe.repository.config.DynamoDbPlayerRepository;
import com.example.tictactoe.repository.config.PageToRecordConverter;
import com.example.tictactoe.repository.config.PlayerRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.internal.client.DefaultDynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {PlayerService.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestPropertySource("classpath:application.properties")
public class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @MockBean
    private DynamoDbPlayerRepository playerRepository;

    @MockBean
    private DefaultDynamoDbAsyncIndex<PlayerRecord> gsiBetweenRanksIndex;

    @Test
    public void search_players_is_bad_request_for_invalid_rank_parameters() {
        Assert.assertThrows(ScoreRankQueryBadParametersException.class,
            () -> playerService.getNeighborhood(3, 2).block());

        Assert.assertThrows(ScoreRankQueryBadParametersException.class,
            () -> playerService.getNeighborhood(-5, -2).block());
    }

}
