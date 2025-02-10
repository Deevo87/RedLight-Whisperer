package org.redlightwhisperer;

import org.redlightwhisperer.enums.LaneType;
import org.redlightwhisperer.enums.Light;
import org.redlightwhisperer.enums.VehicleState;
import org.redlightwhisperer.gui.IntersectionController;

import java.util.LinkedList;
import java.util.List;

public abstract class TrafficLane implements TrafficSignalObserver {
    protected final LinkedList<Vehicle> vehicleQueue = new LinkedList<>();
    protected final LaneType laneType;
    protected final IntersectionController controller;
    protected Light currentLight = Light.RED;
    protected Light previousLight = Light.RED;

    public TrafficLane(LaneType laneType, IntersectionController controller) {
        this.laneType = laneType;
        this.controller = controller;
    }

    public int getQueueSize() {
        return vehicleQueue.size();
    }

    public Light getCurrentLight() {
        return currentLight;
    }

    public void addVehicleToQueue(Vehicle vehicle) {
        vehicleQueue.add(vehicle);
        controller.updateVehicleCount(laneType, vehicleQueue.size(), VehicleState.WAITING);
    }

    public abstract List<String> getExitedVehicles();

    @Override
    public void onLightChange() {
        if (currentLight == Light.YELLOW) {
            switch (previousLight) {
                case RED -> {
                    currentLight = Light.GREEN;
                    controller.onTrafficLightChange(laneType, Light.GREEN);
                }
                case GREEN -> {
                    currentLight = Light.RED;
                    controller.onTrafficLightChange(laneType, Light.RED);
                }
            }
        } else {
            previousLight = currentLight;
            currentLight = Light.YELLOW;
            controller.onTrafficLightChange(laneType, Light.YELLOW);
        }
    }

}
