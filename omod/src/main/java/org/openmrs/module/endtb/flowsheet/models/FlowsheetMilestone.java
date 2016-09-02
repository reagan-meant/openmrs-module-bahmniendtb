package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlowsheetMilestone {
    private String name;
    private int min;
    private int max;
    private FlowsheetConcepts flowsheetConcepts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public FlowsheetConcepts getFlowsheetConcepts() {
        return flowsheetConcepts;
    }

    @JsonProperty("concepts")
    public void setFlowsheetConcepts(FlowsheetConcepts flowsheetConcepts) {
        this.flowsheetConcepts = flowsheetConcepts;
    }
}
