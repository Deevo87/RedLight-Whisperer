package org.redlightwhisperer;

import org.redlightwhisperer.enums.LaneType;
import org.redlightwhisperer.enums.VehicleState;
import org.redlightwhisperer.gui.IntersectionController;

import java.util.ArrayList;
import java.util.List;

public class SingleLaneTraffic extends TrafficLane {
    public SingleLaneTraffic(LaneType laneType, IntersectionController controller) {
        super(laneType, controller);
    }

    public List<String> getExitedVehicles() {
        List<String> result = new ArrayList<>();
        if (!vehicleQueue.isEmpty()) {
            result.add(vehicleQueue.removeFirst().vehicleId());
            controller.updateVehicleCount(laneType, vehicleQueue.size(), VehicleState.LEAVING);
        }
        return result;
    }
}
