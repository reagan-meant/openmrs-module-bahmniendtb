package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetConstant;

public class FlowsheetMilestone {
    private String name;
    private int min;
    private int max;
    private FlowsheetEntities flowsheetEntities;

    public String getName() {
        return this.name;
    }

    @JsonProperty(FlowsheetConstant.NAME)
    public void setName(String name) {
        this.name = name;
    }

    public int getMin() {
        return this.min;
    }

    @JsonProperty(FlowsheetConstant.MIN)
    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    @JsonProperty(FlowsheetConstant.MAX)
    public void setMax(int max) {
        this.max = max;
    }

    public FlowsheetEntities getFlowsheetEntities() {
        return this.flowsheetEntities;
    }

    @JsonProperty(FlowsheetConstant.ENTITIES)
    public void setFlowsheetEntities(FlowsheetEntities flowsheetEntities) {
        this.flowsheetEntities = flowsheetEntities;
    }
}
