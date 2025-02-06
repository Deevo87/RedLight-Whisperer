package org.redlightwhisperer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CommandList(List<Command> commands) {
    @JsonCreator
    public CommandList(@JsonProperty("commands") List<Command> commands) {
        this.commands = commands;
    }
}
