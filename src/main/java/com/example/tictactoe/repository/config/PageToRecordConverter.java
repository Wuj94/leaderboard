package com.example.tictactoe.repository.config;

import java.util.ArrayList;
import java.util.List;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

public class PageToRecordConverter implements Subscriber<Page<PlayerRecord>>, Publisher<List<PlayerRecord>> {

    private static final int DEFAULT_DYNAMODB_RESULT_LIMIT = 20;
    public List<PlayerRecord> output = new ArrayList<>(DEFAULT_DYNAMODB_RESULT_LIMIT);;
    private Subscription upstreamSubscription;
    private Subscriber<? super List<PlayerRecord>> subscriber;

    @Override
    public void subscribe(Subscriber<? super List<PlayerRecord>> subscriber) {
        this.subscriber = subscriber;
        subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                upstreamSubscription.request(1);
            }

            @Override
            public void cancel() {
                upstreamSubscription.cancel();
            }
        });
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.upstreamSubscription = subscription;
    }

    @Override
    public void onNext(Page<PlayerRecord> playerRecordPage) {
        output.addAll(playerRecordPage.items());
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(new RuntimeException("PageToRecordConverter failure"));
    }

    @Override
    public void onComplete() {
        subscriber.onNext(output);
        subscriber.onComplete();
    }
}
