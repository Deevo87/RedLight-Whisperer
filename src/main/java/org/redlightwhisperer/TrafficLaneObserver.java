package org.redlightwhisperer;

import org.redlightwhisperer.enums.LaneType;
import org.redlightwhisperer.enums.Light;
import org.redlightwhisperer.enums.VehicleState;

public interface TrafficLaneObserver {
    void onTrafficLightChange(LaneType lane, Light light);
    void updateVehicleCount(LaneType lane, int count, VehicleState state);
}
