package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FlowsheetConfig {
    private List<FlowsheetMilestone> flowsheetMilestones;
    private FlowsheetEntities flowsheetEntities;
    private String startDateConcept;

    public List<FlowsheetMilestone> getFlowsheetMilestones() {
        return flowsheetMilestones;
    }

    @JsonProperty("milestones")
    public void setFlowsheetMilestones(List<FlowsheetMilestone> flowsheetMilestones) {
        this.flowsheetMilestones = flowsheetMilestones;
    }

    public FlowsheetEntities getFlowsheetEntities() {
        return flowsheetEntities;
    }

    @JsonProperty("entities")
    public void setFlowsheetEntities(FlowsheetEntities flowsheetEntities) {
        this.flowsheetEntities = flowsheetEntities;
    }

    public String getStartDateConcept() {
        return startDateConcept;
    }

    @JsonProperty("startDateConcept")
    public void setStartDateConcept(String startDateConcept) {
        this.startDateConcept = startDateConcept;
    }
}
