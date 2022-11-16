package com.tnt.aggregator.service;

import com.tnt.aggregator.service.queable.impl.QueablePriceService;
import com.tnt.aggregator.service.queable.impl.QueableShipmentsService;
import com.tnt.aggregator.service.queable.impl.QueableTrackService;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.AggregationResponseV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class AggregationService {
    public static final int TIMEOUT = 10;
    @Autowired
    QueablePriceService priceService;
    @Autowired
    QueableShipmentsService shipmentsService;
    @Autowired
    QueableTrackService trackService;

    public ResponseEntity<AggregationResponseV1> getAggregatedResponse(List<Pair<UUID, String>> pricing,
                                                                       List<Pair<UUID, String>> track,
                                                                       List<Pair<UUID, String>> shipments) {
        AggregationResponseV1 aggregationResponseV1 = new AggregationResponseV1();

        try {
            CompletableFuture.allOf(priceService.queueRequest(new HashSet<>(pricing)),
                            shipmentsService.queueRequest(new HashSet<>(shipments)),
                            trackService.queueRequest(new HashSet<>(track)))
                    .get(TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException t) {
            log.error("Exception : {}", t);
            return new ResponseEntity<>(aggregationResponseV1, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        aggregationResponseV1.setPricing(getPricingResultsFromService(pricing));
        aggregationResponseV1.setShipments(getShipmentResultsFromService(shipments));
        aggregationResponseV1.setTrack(getTrackingResultsFromService(track));
        return new ResponseEntity<>(aggregationResponseV1, HttpStatus.OK);
    }

    private HashMap<String, String> getTrackingResultsFromService(List<Pair<UUID, String>> shipments) {
        HashMap<String, String> trackingResults = new HashMap<>();
        shipments.forEach(p -> {
            trackingResults.put(p.getSecond(), trackService.getSharedResultMap().get(p));
            trackService.getSharedResultMap().remove(p);
        });
        return trackingResults;
    }

    private HashMap<String, List<String>> getShipmentResultsFromService(List<Pair<UUID, String>> shipments) {
        HashMap<String, List<String>> shipmentResults = new HashMap<>();
        shipments.forEach(p -> {
            shipmentResults.put(p.getSecond(), shipmentsService.getSharedResultMap().get(p));
            shipmentsService.getSharedResultMap().remove(p);
        });
        return shipmentResults;
    }

    private HashMap<String, BigDecimal> getPricingResultsFromService(List<Pair<UUID, String>> pricing) {
        synchronized (priceService.getSharedResultMap()) {
            HashMap<String, BigDecimal> pricingResults = new HashMap<>();
            pricing.forEach(p -> {
                pricingResults.put(p.getSecond(), BigDecimal.valueOf(priceService.getSharedResultMap().get(p)));
                priceService.getSharedResultMap().remove(p);
            });
            return pricingResults;
        }
    }
}
