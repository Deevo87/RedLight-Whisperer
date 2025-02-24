package org.redlightwhisperer.enums;

public enum LaneType {
    NORTH_FORWARD, SOUTH_FORWARD, EAST_FORWARD, WEST_FORWARD,
    NORTH_TURN_LEFT, SOUTH_TURN_LEFT, EAST_TURN_LEFT, WEST_TURN_LEFT,
    NORTH_TURN_RIGHT, SOUTH_TURN_RIGHT, EAST_TURN_RIGHT, WEST_TURN_RIGHT;

    public String getPrefix() {
        return this.name().substring(0, this.name().indexOf("_"));
    }

    public boolean isDoubleLine() {
        return this == NORTH_FORWARD || this == SOUTH_FORWARD;
    }
}
