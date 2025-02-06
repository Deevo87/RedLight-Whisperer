package org.redlightwhisperer.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommandType {
    ADD_VEHICLE("addVehicle"), STEP("step");

    private final String value;

    CommandType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CommandType fromString(String value) {
        for (CommandType type : CommandType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown command type: " + value);
    }
}
