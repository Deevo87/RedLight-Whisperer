package org.redlightwhisperer;

import org.redlightwhisperer.enums.LaneType;
import org.redlightwhisperer.enums.VehicleState;
import org.redlightwhisperer.gui.IntersectionController;

import java.util.ArrayList;
import java.util.List;

public class DoubleLaneTraffic extends TrafficLane {

    public DoubleLaneTraffic(LaneType laneType, IntersectionController controller) {
        super(laneType, controller);
    }

    public List<String> getExitedVehicles() {
        List<String> result = new ArrayList<>();
        if (vehicleQueue.isEmpty()) {
            return result;
        }

        if (vehicleQueue.size() >= 2) {
            for (int index = 0; index <= 1; index++) {
                result.add(vehicleQueue.removeFirst().vehicleId());
                controller.updateVehicleCount(laneType, vehicleQueue.size(), VehicleState.LEAVING);
            }
        } else {
            result.add(vehicleQueue.removeFirst().vehicleId());
            controller.updateVehicleCount(laneType, vehicleQueue.size(), VehicleState.LEAVING);
        }
        return result;
    }
}
