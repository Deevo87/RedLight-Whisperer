package org.redlightwhisperer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        CommandList commandList;
        try {
            commandList = objectMapper.readValue(new File("src/main/resources/test.json"), CommandList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't parse json, error: " + e);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open the file, error: " + e);
        }

        Intersection intersection = new Intersection(commandList);
        intersection.startSimulation();
        StepStatuses stepStatuses = intersection.getStepStatuses();

        try {
            objectMapper.writeValue(new File("stepStatuses.json"), stepStatuses);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save result file, error: " + e);
        }

    }
}