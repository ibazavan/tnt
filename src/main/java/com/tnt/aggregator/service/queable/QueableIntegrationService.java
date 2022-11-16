package com.tnt.aggregator.service.queable;

import org.springframework.data.util.Pair;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface QueableIntegrationService<T, Y> {
    WebClient getWebClient();

    String getEndpoint();

    void setTimer(long timer);

    Deque<Pair<UUID,T>> getInputQueue();

    ConcurrentHashMap<Pair<UUID,T>, Y> getSharedResultMap();

    HashMap callService(ArrayDeque<Pair<UUID,T>> queries);
}
