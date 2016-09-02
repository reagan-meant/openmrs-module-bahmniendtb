package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FlowsheetConfig {
    private List<FlowsheetMilestone> flowsheetMilestones;
    private FlowsheetConcepts flowsheetConcepts;

    public List<FlowsheetMilestone> getFlowsheetMilestones() {
        return flowsheetMilestones;
    }

    @JsonProperty("milestones")
    public void setFlowsheetMilestones(List<FlowsheetMilestone> flowsheetMilestones) {
        this.flowsheetMilestones = flowsheetMilestones;
    }

    public FlowsheetConcepts getFlowsheetConcepts() {
        return flowsheetConcepts;
    }

    @JsonProperty("concepts")
    public void setFlowsheetConcepts(FlowsheetConcepts flowsheetConcepts) {
        this.flowsheetConcepts = flowsheetConcepts;
    }
}
