package org.redlightwhisperer;

import org.redlightwhisperer.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Intersection {
    private final int RED_LIGHT_CHANGE_BUFFER = 2;

    private final CommandList commandList;
    private final Map<LaneGroup, List<TrafficLightLane>> trafficLaneGroupMap = new HashMap<>();
    private final Map<LaneType, TrafficLightLane> lanes = new HashMap<>();
    private final Map<LaneGroup, Integer> trafficGroupCnts = new HashMap<>();
    private LaneGroup previousFlowGroup = null;
    private LaneGroup biggestFlowGroup;
    private final List<List<Car>> stepResults = new ArrayList<>();

    public Intersection(CommandList commandList) {
        this.commandList = commandList;

        init();
    }

    private void init() {
        for (LaneType laneType : LaneType.values()) {
            lanes.put(laneType, new TrafficLightLane(laneType));
        }

        addLaneGroup(LaneGroup.NS_FORWARD, LaneType.NORTH_FORWARD, LaneType.SOUTH_FORWARD);
        addLaneGroup(LaneGroup.EW_FORWARD, LaneType.WEST_FORWARD, LaneType.EAST_FORWARD);
        addLaneGroup(LaneGroup.NS_TURN_LEFT, LaneType.NORTH_TURN_LEFT, LaneType.SOUTH_TURN_LEFT);
        addLaneGroup(LaneGroup.EW_TURN_LEFT, LaneType.WEST_TURN_LEFT, LaneType.EAST_TURN_LEFT);
        addLaneGroup(LaneGroup.NS_TURN_RIGHT, LaneType.NORTH_TURN_RIGHT, LaneType.SOUTH_TURN_RIGHT);
        addLaneGroup(LaneGroup.EW_TURN_RIGHT, LaneType.WEST_TURN_RIGHT, LaneType.EAST_TURN_RIGHT);

        for (LaneGroup group : LaneGroup.values()) {
            trafficGroupCnts.put(group, 0);
        }
    }

    private void addLaneGroup(LaneGroup laneGroup, LaneType... laneTypes) {
        List<TrafficLightLane> trafficList = new ArrayList<>();
        for (LaneType laneType : laneTypes) {
            trafficList.add(lanes.get(laneType));
        }

        trafficLaneGroupMap.put(laneGroup, trafficList);
    }

    public List<List<Car>> startSimulation() {
        int i = 0;
        while (i < commandList.commands().size()) {
            Command command = commandList.commands().get(i);
            switch (command.getType()) {
                case CommandType.ADD_VEHICLE -> {
                    i += 1;
                    Car car = command.getCar();
                    lanes.get(car.startRoad().getCarMovement(car.endRoad())).addCarToQueue(car);
                }
                case CommandType.STEP -> {
                    List<Car> currentStep = new ArrayList<>();
                    calculatePriority();

                    if (trafficLaneGroupMap.get(biggestFlowGroup).getFirst().getCurrentLight() == Light.GREEN) {
                        i += 1;
                        for (TrafficLightLane lane : trafficLaneGroupMap.get(biggestFlowGroup)) {
                            trafficGroupCnts.put(biggestFlowGroup, trafficGroupCnts.get(biggestFlowGroup) - 1);
                            currentStep.addAll(lane.popCar());
                        }

                        switch (biggestFlowGroup) {
                            case NS_FORWARD -> {
                                for (TrafficLightLane lane : trafficLaneGroupMap.get(LaneGroup.NS_TURN_RIGHT)) {
                                    trafficGroupCnts.put(biggestFlowGroup, trafficGroupCnts.get(LaneGroup.NS_TURN_RIGHT) - 1);
                                    currentStep.addAll(lane.popCar());
                                }
                            }
                            case EW_FORWARD -> {
                                for (TrafficLightLane lane : trafficLaneGroupMap.get(LaneGroup.EW_TURN_RIGHT)) {
                                    trafficGroupCnts.put(biggestFlowGroup, trafficGroupCnts.get(LaneGroup.EW_TURN_RIGHT) - 1);
                                    currentStep.addAll(lane.popCar());
                                }
                            }
                        }

                        previousFlowGroup = biggestFlowGroup;
                        stepResults.add(currentStep);
                    }

                }
                default -> {
                }
            }
        }
        return stepResults;
    }

    private void calculatePriority() {
        int cnt;
        int priority = -1;
        for (Map.Entry<LaneGroup, List<TrafficLightLane>> laneGroup : trafficLaneGroupMap.entrySet()) {
            for (TrafficLightLane traffic : laneGroup.getValue()) {
                cnt = trafficGroupCnts.get(laneGroup.getKey()) + traffic.getQueueSize();
                trafficGroupCnts.put(laneGroup.getKey(), cnt);

                if (biggestFlowGroup == laneGroup.getKey() && trafficLaneGroupMap.get(biggestFlowGroup).getFirst().getCurrentLight() == Light.GREEN) {
                    cnt += RED_LIGHT_CHANGE_BUFFER;
                }
                if (cnt > priority) {
                    priority = cnt;
                    biggestFlowGroup = laneGroup.getKey();
                }
            }
        }
        if (biggestFlowGroup == previousFlowGroup) {
            return;
        }
        changeLightForGroup(biggestFlowGroup);
    }

    private void changeLightForGroup(LaneGroup group) {
        for (TrafficLightLane lane : trafficLaneGroupMap.get(group)) {
            lane.onLightChange();
        }
    }
}
