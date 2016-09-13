package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetConstant;

import java.util.ArrayList;
import java.util.List;

public class FlowsheetConfig {
    private List<FlowsheetMilestone> flowsheetMilestones;
    private FlowsheetEntities flowsheetEntities;
    private String startDateConcept;
    private String endDateConcept;

    public List<FlowsheetMilestone> getFlowsheetMilestones() {
        if(null == this.flowsheetMilestones) {
            this.flowsheetMilestones = new ArrayList<>();
        }
        return this.flowsheetMilestones;
    }

    @JsonProperty(FlowsheetConstant.MILESTONES)
    public void setFlowsheetMilestones(List<FlowsheetMilestone> flowsheetMilestones) {
        this.flowsheetMilestones = flowsheetMilestones;
    }

    public FlowsheetEntities getFlowsheetEntities() {
        return flowsheetEntities;
    }

    @JsonProperty(FlowsheetConstant.ENTITIES)
    public void setFlowsheetEntities(FlowsheetEntities flowsheetEntities) {
        this.flowsheetEntities = flowsheetEntities;
    }

    public String getStartDateConcept() {
        return this.startDateConcept;
    }

    @JsonProperty(FlowsheetConstant.START_DATE_CONCEPT)
    public void setStartDateConcept(String startDateConcept) {
        this.startDateConcept = startDateConcept;
    }

    public String getEndDateConcept() {
        return endDateConcept;
    }

    @JsonProperty(FlowsheetConstant.END_DATE_CONCEPT)
    public void setEndDateConcept(String endDateConcept) {
        this.endDateConcept = endDateConcept;
    }
}
