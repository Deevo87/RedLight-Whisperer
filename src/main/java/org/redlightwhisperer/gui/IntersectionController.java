package org.redlightwhisperer.gui;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.redlightwhisperer.*;
import org.redlightwhisperer.enums.LaneType;
import org.redlightwhisperer.enums.Light;
import org.redlightwhisperer.enums.VehicleState;
import org.redlightwhisperer.utils.LanePositions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntersectionController implements TrafficLaneObserver {
    @FXML
    private Pane carLayer;

    @FXML
    private Rectangle northForward1, northForward2, southForward1, southForward2, westForward, eastForward, northTurnRight,
            southTurnRight, westTurnRight, eastTurnRight, northTurnLeft, southTurnLeft, westTurnLeft, eastTurnLeft;

    @FXML
    private Label northForward1Cnt, northForward2Cnt, southForward1Cnt, southForward2Cnt, eastForwardCnt, westForwardCnt,
            northTurnRightCnt, southTurnRightCnt, eastTurnRightCnt, westTurnRightCnt, northTurnLeftCnt, southTurnLeftCnt, eastTurnLeftCnt, westTurnLeftCnt;

    private final Point2D verticalVehicleSize = new Point2D(20, 40);
    private final Point2D horizontalVehicleSize = new Point2D(40, 20);
    private final Map<LaneType, List<Rectangle>> trafficLights = new HashMap<>();
    private final Map<LaneType, List<Label>> trafficCnts = new HashMap<>();
    private final Map<LaneType, List<Rectangle>> vehicles = new HashMap<>();
    private CommandList commandList;
    private Intersection intersection;
    private String outputFilename;

    public IntersectionController() {
    }

    @FXML
    public void initialize() {
        initializeLights();
    }

    private void initializeLights() {
        for (LaneType lane : LaneType.values()) {
            switch (lane) {
                case NORTH_FORWARD -> {
                    trafficLights.put(lane, List.of(northForward1, northForward2));
                    trafficCnts.put(lane, List.of(northForward1Cnt, northForward2Cnt));
                }
                case SOUTH_FORWARD -> {
                    trafficLights.put(lane, List.of(southForward1, southForward2));
                    trafficCnts.put(lane, List.of(southForward1Cnt, southForward2Cnt));
                }
                case EAST_FORWARD -> {
                    trafficLights.put(lane, List.of(eastForward));
                    trafficCnts.put(lane, List.of(eastForwardCnt));
                }
                case WEST_FORWARD -> {
                    trafficLights.put(lane, List.of(westForward));
                    trafficCnts.put(lane, List.of(westForwardCnt));
                }
                case NORTH_TURN_LEFT -> {
                    trafficLights.put(lane, List.of(northTurnLeft));
                    trafficCnts.put(lane, List.of(northTurnLeftCnt));
                }
                case SOUTH_TURN_LEFT -> {
                    trafficLights.put(lane, List.of(southTurnLeft));
                    trafficCnts.put(lane, List.of(southTurnLeftCnt));
                }
                case EAST_TURN_LEFT -> {
                    trafficLights.put(lane, List.of(eastTurnLeft));
                    trafficCnts.put(lane, List.of(eastTurnLeftCnt));
                }
                case WEST_TURN_LEFT -> {
                    trafficLights.put(lane, List.of(westTurnLeft));
                    trafficCnts.put(lane, List.of(westTurnLeftCnt));
                }
                case NORTH_TURN_RIGHT -> {
                    trafficLights.put(lane, List.of(northTurnRight));
                    trafficCnts.put(lane, List.of(northTurnRightCnt));
                }
                case SOUTH_TURN_RIGHT -> {
                    trafficLights.put(lane, List.of(southTurnRight));
                    trafficCnts.put(lane, List.of(southTurnRightCnt));
                }
                case EAST_TURN_RIGHT -> {
                    trafficLights.put(lane, List.of(eastTurnRight));
                    trafficCnts.put(lane, List.of(eastTurnRightCnt));
                }
                case WEST_TURN_RIGHT -> {
                    trafficLights.put(lane, List.of(westTurnRight));
                    trafficCnts.put(lane, List.of(westTurnRightCnt));
                }
            }
        }
    }

    public void setCommandListAndOutputFile(CommandList commandList, String outputFilename) {
        this.commandList = commandList;
        this.outputFilename = outputFilename;
        startEngine();
    }

    private void startEngine() {
        if (commandList != null) {
            intersection = new Intersection(commandList, this, outputFilename);
            Thread engineThread = new Thread(() -> {
                intersection.startSimulation();
                Platform.runLater(Platform::exit);
            });
            engineThread.setDaemon(true);
            engineThread.start();
        }
    }

    public void addVehicleToPane(LaneType lane, Point2D position) {
        Rectangle vehicle;

        if (lane.getPrefix().equals("NORTH") || lane.getPrefix().equals("SOUTH")) {
            vehicle = new Rectangle(verticalVehicleSize.getX(), verticalVehicleSize.getY(), Color.BLUE);
        } else {
            vehicle = new Rectangle(horizontalVehicleSize.getX(), horizontalVehicleSize.getY(), Color.BLUE);
        }

        vehicle.setLayoutX(position.getX());
        vehicle.setLayoutY(position.getY());
        carLayer.getChildren().add(vehicle);
        vehicles.computeIfAbsent(lane, k -> new ArrayList<>()).add(vehicle);
    }

    public void moveVehicle(LaneType lane, int count) {
        List<Rectangle> vehicleList = vehicles.get(lane);

        if (lane == LaneType.NORTH_FORWARD || lane == LaneType.SOUTH_FORWARD) {
            moveForward(vehicleList, count % 2, LanePositions.PATHS.get(lane).get(count % 2));
        } else if (lane == LaneType.EAST_FORWARD || lane == LaneType.WEST_FORWARD) {
            moveForward(vehicleList, 0, LanePositions.PATHS.get(lane).getFirst());
        } else {
            performTurn(vehicleList, vehicleList.getFirst(), lane, LanePositions.PATHS.get(lane), 0);
        }
    }

    private void moveForward(List<Rectangle> vehicleList, int index, Point2D target) {
        Rectangle vehicle = vehicleList.get(index);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), vehicle);
        transition.setToX(target.getX() - vehicle.getLayoutX());
        transition.setToY(target.getY() - vehicle.getLayoutY());
        transition.setInterpolator(Interpolator.LINEAR);
        transition.setOnFinished(e -> {
            carLayer.getChildren().remove(vehicle);
            vehicleList.remove(vehicle);
        });
        transition.play();
    }

    private void performTurn(List<Rectangle> vehicleList, Rectangle vehicle, LaneType lane, List<Point2D> path, int index) {
        if (index >= path.size()) {
            carLayer.getChildren().remove(vehicle);
            vehicleList.remove(vehicle);
            return;
        }

        Point2D target = path.get(index);
        if (index - 1 >= 0) {
            rotateTowards(vehicle, lane, path.get(index - 1), target);
        }

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), vehicle);
        transition.setToX(target.getX() - vehicle.getLayoutX());
        transition.setToY(target.getY() - vehicle.getLayoutY());
        transition.setInterpolator(Interpolator.LINEAR);

        transition.setOnFinished(e -> performTurn(vehicleList, vehicle, lane, path, index + 1));
        transition.play();
    }

    public void rotateTowards(Rectangle vehicle, LaneType lane, Point2D from, Point2D to) {
        double deltaX = to.getX() - from.getX();
        double deltaY = to.getY() - from.getY();

        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        if (lane.getPrefix().equals("NORTH") || lane.getPrefix().equals("SOUTH")) {
            angle -= 90;
        }

        vehicle.setRotate(angle);
    }

    @Override
    public void onTrafficLightChange(LaneType lane, Light light) {
        for (Rectangle trafficLight : trafficLights.get(lane)) {
            trafficLight.setFill(switch (light) {
                case GREEN -> Color.GREEN;
                case YELLOW -> Color.YELLOW;
                case RED -> Color.RED;
            });
        }
    }

    @Override
    public void updateVehicleCount(LaneType lane, int count, VehicleState state) {
        for (Label counter : trafficCnts.get(lane)) {
            Platform.runLater(() -> {
                counter.setText(String.valueOf(count));

                switch (state) {
                    case LEAVING -> {
                        if (!vehicles.get(lane).isEmpty()) {
                            moveVehicle(lane, count);
                        }
                    }
                    case WAITING -> {
                        if (!vehicles.containsKey(lane)) {
                            vehicles.put(lane, new ArrayList<>());
                        }

                        if (vehicles.get(lane).size() < count) {
                            Point2D position;
                            if (lane == LaneType.NORTH_FORWARD || lane == LaneType.SOUTH_FORWARD) {
                                position = LanePositions.STARTING_POSITIONS.get(lane).get(count % 2);
                            } else {
                                position = LanePositions.STARTING_POSITIONS.get(lane).getFirst();
                            }

                            addVehicleToPane(lane, position);
                        }
                    }
                }
            });
        }
    }

}
