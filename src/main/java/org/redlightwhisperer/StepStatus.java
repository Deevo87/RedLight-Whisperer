package org.redlightwhisperer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class StepStatus {
    @JsonProperty("leftVehicles")
    private final List<String> leftVehicles = new ArrayList<>();

    public void addVehicle(List<String> vehicleIds) {
        leftVehicles.addAll(vehicleIds);
    }
}
