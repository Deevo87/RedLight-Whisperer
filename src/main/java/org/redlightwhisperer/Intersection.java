package org.redlightwhisperer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redlightwhisperer.enums.*;
import org.redlightwhisperer.gui.IntersectionController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Intersection {
    private final int RED_LIGHT_CHANGE_BUFFER = 5;
    private final int TRAFFIC_USAGE_LIMIT = 8;
    private final int PUNISHMENT = 5;

    private final CommandList commandList;
    private final String outputFilename;
    private final IntersectionController controller;
    private final StepStatuses stepStatuses = new StepStatuses();
    private final Map<LaneGroup, List<TrafficLane>> trafficLaneGroupMap = new HashMap<>();
    private final Map<LaneType, TrafficLane> lanes = new HashMap<>();
    private final Map<LaneGroup, Integer> trafficGroupCnts = new HashMap<>();
    private LaneGroup previousFlowGroup = null;
    private LaneGroup biggestFlowGroup;
    private int trafficUsage = 0;
    private LaneGroup punishedLaneGroup;
    private int punishmentCnt = PUNISHMENT;
    private boolean mustChangeLight = false;

    public Intersection(CommandList commandList, IntersectionController controller, String outputFilename) {
        this.commandList = commandList;
        this.controller = controller;
        this.outputFilename = outputFilename;
        init();
    }

    private void init() {
        for (LaneType laneType : LaneType.values()) {
            if (laneType.isDoubleLine()) {
                lanes.put(laneType, new DoubleLaneTraffic(laneType, controller));
            } else {
                lanes.put(laneType, new SingleLaneTraffic(laneType, controller));
            }
        }

        addLaneGroups();

        for (LaneGroup group : LaneGroup.values()) {
            trafficGroupCnts.put(group, 0);
        }
    }

    private void addLaneGroups() {
        addLaneGroup(LaneGroup.NS_FORWARD, LaneType.NORTH_FORWARD, LaneType.SOUTH_FORWARD, LaneType.EAST_TURN_RIGHT, LaneType.WEST_TURN_RIGHT);
        addLaneGroup(LaneGroup.EW_FORWARD, LaneType.WEST_FORWARD, LaneType.EAST_FORWARD, LaneType.SOUTH_TURN_RIGHT, LaneType.NORTH_TURN_RIGHT);
        addLaneGroup(LaneGroup.NS_TURN_LEFT, LaneType.NORTH_TURN_LEFT, LaneType.SOUTH_TURN_LEFT);
        addLaneGroup(LaneGroup.EW_TURN_LEFT, LaneType.WEST_TURN_LEFT, LaneType.EAST_TURN_LEFT);
    }

    private void addLaneGroup(LaneGroup laneGroup, LaneType... laneTypes) {
        List<TrafficLane> trafficList = new ArrayList<>();
        for (LaneType laneType : laneTypes) {
            trafficList.add(lanes.get(laneType));
        }
        trafficLaneGroupMap.put(laneGroup, trafficList);
    }

    public void startSimulation() {
        int i = 0;
        while (i < commandList.commands().size()) {
            Command command = commandList.commands().get(i);
            switch (command.getType()) {
                case CommandType.ADD_VEHICLE -> {
                    i += 1;
                    handleAddVehicle(command);
                }
                case CommandType.STEP -> {
                    if (!calculatePriority()) {
                        i += 1;
                        stepStatuses.addStepStatus(new StepStatus());
                        break;
                    }

                    if (trafficLaneGroupMap.get(biggestFlowGroup).getFirst().getCurrentLight() == Light.GREEN) {
                        i += 1;
                        handleGreenLight();
                    }

                    createSimulationDelay(1000);
                }
                default -> {
                }
            }
        }
        saveStepStatuses();
    }

    private void handleGreenLight() {
        StepStatus currentStep = new StepStatus();
        int cnt = trafficGroupCnts.get(biggestFlowGroup);
        for (TrafficLane lane : trafficLaneGroupMap.get(biggestFlowGroup)) {
            cnt -= 1;
            currentStep.addVehicle(lane.getExitedVehicles());
            createSimulationDelay(1000);
        }
        trafficGroupCnts.put(biggestFlowGroup, cnt);

        previousFlowGroup = biggestFlowGroup;
        stepStatuses.addStepStatus(currentStep);
    }

    private void handleAddVehicle(Command command) {
        Vehicle vehicle = command.getVehicle();
        lanes.get(vehicle.startRoad().getVehicleMovement(vehicle.endRoad())).addVehicleToQueue(vehicle);
    }

    private boolean calculatePriority() {
        int priority = -1;
        LaneGroup biggestFlowCandidate = biggestFlowGroup;

        for (Map.Entry<LaneGroup, List<TrafficLane>> laneGroup : trafficLaneGroupMap.entrySet()) {
            if (mustChangeLight && punishedLaneGroup == laneGroup.getKey()) {
                punishmentCnt -= 1;
                continue;
            }

            int cnt = 0;
            for (TrafficLane traffic : laneGroup.getValue()) {
                cnt += traffic.getQueueSize();
            }
            trafficGroupCnts.put(laneGroup.getKey(), cnt);

            if (shouldPrioritizeCurrentGroup(laneGroup.getKey(), cnt)) {
                cnt += RED_LIGHT_CHANGE_BUFFER;
            }

            if (cnt > priority) {
                priority = cnt;
                biggestFlowCandidate = laneGroup.getKey();
            }
        }
        biggestFlowGroup = biggestFlowCandidate;

        if (punishmentCnt <= 0) {
            punishmentCnt = PUNISHMENT;
            mustChangeLight = false;
        }

        if (priority <= 0) {
            return resetTrafficUsage();
        }

        manageTrafficLightChanges();
        return true;
    }

    private boolean shouldPrioritizeCurrentGroup(LaneGroup laneGroup, int cnt) {
        return biggestFlowGroup == laneGroup &&
                trafficLaneGroupMap.get(biggestFlowGroup).getFirst().getCurrentLight() == Light.GREEN &&
                cnt > 0;
    }

    private boolean resetTrafficUsage() {
        if (mustChangeLight) {
            biggestFlowGroup = punishedLaneGroup;
            mustChangeLight = false;
            trafficUsage = 0;
            return true;
        }
        return false;
    }

    private void manageTrafficLightChanges() {
        if (biggestFlowGroup == previousFlowGroup) {
            trafficUsage += 1;
            if (trafficUsage >= TRAFFIC_USAGE_LIMIT) {
                trafficUsage = 0;
                mustChangeLight = true;
                punishedLaneGroup = biggestFlowGroup;
            }
        } else {
            changeLightForGroup(biggestFlowGroup);
            if (previousFlowGroup != null) {
                changeLightForGroup(previousFlowGroup);
            }
        }
    }

    private void changeLightForGroup(LaneGroup group) {
        trafficLaneGroupMap.get(group).forEach(TrafficLane::onLightChange);
    }

    private void saveStepStatuses() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(outputFilename), stepStatuses);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save step statuses to file: " + outputFilename, e);
        }
    }

    private void createSimulationDelay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulation delay interrupted after " + time + " ms", e);
        }
    }
}
