package org.redlightwhisperer.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Direction {
    SOUTH("south"), NORTH("north"), EAST("east"), WEST("west");

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Direction fromString(String value) {
        for (Direction direction : Direction.values()) {
            if (direction.value.equalsIgnoreCase(value)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown direction type: " + value);
    }

    public LaneType getCarMovement(Direction destination) {
        if (destination == this) {
            throw new IllegalStateException("You can't turn around! Destination has to be different than starting road.");
        }

        return switch (this) {
            case NORTH -> (destination == WEST) ? LaneType.NORTH_TURN_RIGHT:
                    (destination == EAST) ? LaneType.NORTH_TURN_LEFT :
                           LaneType.NORTH_FORWARD;
            case SOUTH -> (destination == EAST) ? LaneType.SOUTH_TURN_RIGHT :
                    (destination == WEST) ? LaneType.SOUTH_TURN_LEFT :
                            LaneType.SOUTH_FORWARD;
            case EAST -> (destination == NORTH) ? LaneType.EAST_TURN_RIGHT :
                    (destination == SOUTH) ? LaneType.EAST_TURN_LEFT :
                            LaneType.EAST_FORWARD;
            case WEST -> (destination == SOUTH) ? LaneType.WEST_TURN_RIGHT :
                    (destination == NORTH) ? LaneType.WEST_TURN_LEFT :
                            LaneType.WEST_FORWARD;
        };
    }
}
