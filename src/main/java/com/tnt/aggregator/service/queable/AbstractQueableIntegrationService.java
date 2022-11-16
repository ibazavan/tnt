package com.tnt.aggregator.service.queable;

import com.tnt.aggregator.config.IntegrationProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public abstract class AbstractQueableIntegrationService<T, Y> implements QueableIntegrationService {

    public static final String QUERY = "q";
    public static final int NUMBER_OF_RETRIES = 3;
    @Autowired
    private WebClient webClient;
    @Autowired
    private IntegrationProperties integrationProperties;
    private long timer;
    public static final int CALL_TIMEOUT_IN_SECONDS = 5;
    public static final int BATCH_SIZE = 5;

    public CompletableFuture<Void> queueRequest(Set<Pair<UUID, T>> orders) {
        // Add to queue
        getInputQueue().addAll(orders);
        // Reset timer
        this.setTimer(System.nanoTime());
        // Return callable
        return makeCall(orders);
    }

    private CompletableFuture<Void> makeCall(Set<Pair<UUID, T>> itemsLeftToQuery) {
        return CompletableFuture.runAsync(() ->
        {
            while (itemsLeftToQuery.size() > 0 || !getInputQueue().isEmpty()) {
                // All results in? Return
                if (areRequiredResultsInTheMap(itemsLeftToQuery)) {
                    return;
                }
                // Batch sized reached / time passed ? Make call
                if (getInputQueue().size() >= BATCH_SIZE || hasScheduledTimePassed()) {
                    ArrayDeque<Pair<UUID, T>> requestedItems = getRequestedItemsFromQueue();
                    getSharedResultMap().putAll(callService(requestedItems));
                    itemsLeftToQuery.removeAll(requestedItems);
                }
            }
        });

    }

    private ArrayDeque<Pair<UUID, T>> getRequestedItemsFromQueue() {
        ArrayDeque<Pair<UUID, T>> requestedItems = new ArrayDeque<>(BATCH_SIZE);
        while (requestedItems.size() < BATCH_SIZE && !getInputQueue().isEmpty()) {
            requestedItems.add((Pair<UUID, T>) getInputQueue().remove());
        }
        return requestedItems;
    }

    @Override
    public HashMap callService(ArrayDeque queries) {
        ArrayDeque<Pair<UUID, String>> requestedItems = queries;

        WebClient.RequestHeadersUriSpec<?> uriSpec = getWebClient().get();
        WebClient.RequestHeadersSpec<?> bodySpec = createUriWithQueryParams(requestedItems, uriSpec);


        HashMap responseFromApi = bodySpec.retrieve()
                .bodyToMono(HashMap.class).retryWhen(Retry.max(NUMBER_OF_RETRIES)).onErrorReturn(createMapWithNullValues(requestedItems)).block();

        HashMap resultsWithUUIDAttached = new HashMap<>();
        requestedItems.forEach(it -> resultsWithUUIDAttached.put(it, responseFromApi.get(it.getSecond())));

        return resultsWithUUIDAttached;
    }

    private HashMap createMapWithNullValues(ArrayDeque<Pair<UUID, String>> requestedItems) {
        HashMap nullValues = new HashMap<>();
        requestedItems.forEach(it -> nullValues.put(it, null));
        return nullValues;

    }

    private WebClient.RequestHeadersSpec<?> createUriWithQueryParams(ArrayDeque<Pair<UUID, String>> requestedItems, WebClient.RequestHeadersUriSpec<?> uriSpec) {
        return uriSpec.uri(
                uriBuilder -> uriBuilder.pathSegment(getEndpoint()).queryParam(QUERY, requestedItems.stream().map(it -> it.getSecond()).collect(Collectors.toList())).build());
    }

    private boolean hasScheduledTimePassed() {
        return TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - timer) > CALL_TIMEOUT_IN_SECONDS;
    }

    private boolean areRequiredResultsInTheMap(Set<Pair<UUID, T>> itemsLeftToQuery) {
        return itemsLeftToQuery.stream().filter(c -> getSharedResultMap().get(c) == null).collect(Collectors.toList()).size() == 0;
    }


}
