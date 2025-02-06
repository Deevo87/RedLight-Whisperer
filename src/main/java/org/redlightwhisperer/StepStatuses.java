package org.redlightwhisperer;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class StepStatuses {
    @JsonProperty("stepStatuses")
    private List<StepStatus> stepStatuses = new ArrayList<>();

    public void addStepStatus(StepStatus status) {
        stepStatuses.add(status);
    }
}
