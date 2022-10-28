package com.inssa.backend.bus.controller.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusResponse {

    private int lastBusNumber;
    private String lastVisitedBusStop;
    private List<String> busStops;
}
