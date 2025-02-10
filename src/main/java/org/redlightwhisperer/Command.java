package org.redlightwhisperer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.redlightwhisperer.enums.CommandType;
import org.redlightwhisperer.enums.Direction;

public class Command {
    private final CommandType type;
    private Vehicle vehicle;

    @JsonCreator
    public Command(@JsonProperty("type") CommandType type,
                   @JsonProperty("vehicleId") String vehicleId,
                   @JsonProperty("startRoad") Direction startRoad,
                   @JsonProperty("endRoad") Direction endRoad) {
       this.type = type;
       if (type == CommandType.ADD_VEHICLE) {
           this.vehicle = new Vehicle(vehicleId, startRoad, endRoad);
       }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public CommandType getType() {
        return type;
    }
}
