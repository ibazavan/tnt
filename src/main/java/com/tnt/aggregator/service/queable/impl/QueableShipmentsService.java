package com.tnt.aggregator.service.queable.impl;

import com.tnt.aggregator.service.queable.AbstractQueableIntegrationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@Getter
public class QueableShipmentsService extends AbstractQueableIntegrationService {
    @Value("${integration.endpoint.shipments}")
    private String endpoint;
    private Deque<String> inputQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentHashMap<String, List<String>> sharedResultMap = new ConcurrentHashMap<>();

    public synchronized ConcurrentHashMap<String, List<String>> getSharedResultMap() {
        return sharedResultMap;
    }
}
