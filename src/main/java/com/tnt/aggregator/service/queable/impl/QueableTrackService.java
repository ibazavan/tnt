package com.tnt.aggregator.service.queable.impl;

import com.tnt.aggregator.service.queable.AbstractQueableIntegrationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@Service
public class QueableTrackService extends AbstractQueableIntegrationService {
    @Value("${integration.endpoint.tracking}")
    private String endpoint;
    private Deque<String> inputQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentHashMap<String, String> sharedResultMap = new ConcurrentHashMap<>();

    public synchronized ConcurrentHashMap<String, String> getSharedResultMap() {
        return sharedResultMap;
    }
}
