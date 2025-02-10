package org.redlightwhisperer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.redlightwhisperer.enums.Direction;

public record Vehicle(String vehicleId, Direction startRoad, Direction endRoad) {
    @JsonCreator
    public Vehicle(@JsonProperty("vehicleId") String vehicleId,
                   @JsonProperty("startRoad") Direction startRoad,
                   @JsonProperty("endRoad") Direction endRoad) {
        this.vehicleId = vehicleId;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
    }
}
