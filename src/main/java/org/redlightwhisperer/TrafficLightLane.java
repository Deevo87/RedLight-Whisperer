package org.redlightwhisperer;

import org.redlightwhisperer.enums.LaneType;
import org.redlightwhisperer.enums.Light;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TrafficLightLane implements TrafficLightObserver {
    private final LinkedList<Car> carQueue = new LinkedList<>();
    private final LaneType laneType;
    private Light currentLight = Light.RED;
    private Light previousLight = Light.RED;

    public TrafficLightLane(LaneType laneType) {
        this.laneType = laneType;
    }

    public void addCarToQueue(Car car) {
        carQueue.add(car);
    }

    public int getQueueSize() {
        return carQueue.size();
    }

    public Light getCurrentLight() {
        return currentLight;
    }

    // need to be refactored
    public List<String> getExitedVehicles() {
        List<String> result = new ArrayList<>();
        // if trafficLane has two lane in (two situations)
        if (carQueue.size() > 1 && (laneType == LaneType.NORTH_FORWARD || laneType == LaneType.SOUTH_FORWARD)) {
            result.add(carQueue.removeFirst().vehicleId());
        }
        if (!carQueue.isEmpty()) {
            result.add(carQueue.removeFirst().vehicleId());
        }
        return result;
    }

    @Override
    public void onLightChange() {
        if (currentLight == Light.YELLOW) {
            switch (previousLight) {
                case RED -> currentLight = Light.GREEN;
                case GREEN -> currentLight = Light.RED;
            }
        } else {
            previousLight = currentLight;
            currentLight = Light.YELLOW;
        }
    }
}
