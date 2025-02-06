package org.redlightwhisperer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.redlightwhisperer.enums.CommandType;
import org.redlightwhisperer.enums.Direction;

public class Command {
    private final CommandType type;
    private Car car;

    @JsonCreator
    public Command(@JsonProperty("type") CommandType type,
                   @JsonProperty("vehicleId") String vehicleId,
                   @JsonProperty("startRoad") Direction startRoad,
                   @JsonProperty("endRoad") Direction endRoad) {
       this.type = type;
       if (type == CommandType.ADD_VEHICLE) {
           this.car = new Car(vehicleId, startRoad, endRoad);
       }
    }

    public Car getCar() {
        return car;
    }

    public CommandType getType() {
        return type;
    }
}
