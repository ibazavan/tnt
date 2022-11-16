package com.tnt.aggregator.controller;

import com.tnt.aggregator.service.AggregationService;
import com.tnt.aggregator.validation.PricingListConstraint;
import org.openapitools.api.AggregationApi;
import org.openapitools.model.AggregationResponseV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Validated
public class AggregationController implements AggregationApi {
    @Autowired
    AggregationService aggregationService;

    @Override
    public ResponseEntity<AggregationResponseV1> getAggregatedDataV1(@NotNull @Valid List<String> pricing,
                                                                     @NotNull @Valid List<String> track,
                                                                     @NotNull @Valid List<String> shipments) {
        return aggregationService.getAggregatedResponse(createUUIDtoIdPairs(pricing), createUUIDtoIdPairs(track), createUUIDtoIdPairs(shipments));
    }

    private List<Pair<UUID, String>> createUUIDtoIdPairs(List<String> pricing) {
        return pricing.stream().map(it -> Pair.of(UUID.randomUUID(), it)).collect(Collectors.toList());
    }
}
