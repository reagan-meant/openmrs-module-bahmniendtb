package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetContant;

public class FlowsheetMilestone {
    private String name;
    private int min;
    private int max;
    private FlowsheetEntities flowsheetEntities;

    public String getName() {
        return this.name;
    }

    @JsonProperty(FlowsheetContant.NAME)
    public void setName(String name) {
        this.name = name;
    }

    public int getMin() {
        return this.min;
    }

    @JsonProperty(FlowsheetContant.MIN)
    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    @JsonProperty(FlowsheetContant.MAX)
    public void setMax(int max) {
        this.max = max;
    }

    public FlowsheetEntities getFlowsheetEntities() {
        return this.flowsheetEntities;
    }

    @JsonProperty(FlowsheetContant.ENTITIES)
    public void setFlowsheetEntities(FlowsheetEntities flowsheetEntities) {
        this.flowsheetEntities = flowsheetEntities;
    }
}
