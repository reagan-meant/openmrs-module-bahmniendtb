package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FlowsheetConfig {
    private List<FlowsheetMilestone> flowsheetMilestones;
    private FlowsheetEntities flowsheetEntities;
    private String startDateConcept;
    private Set<String> startDateDrugConcepts;
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


    public Set<String> getStartDateDrugConcepts() {
        return startDateDrugConcepts;
    }

    @JsonProperty(FlowsheetConstant.START_DATE_DRUG_CONCEPTS)
    public void setStartDateDrugConcepts(Set<String> startDateDrugConcepts) {
        this.startDateDrugConcepts = startDateDrugConcepts;
    }

    public String getEndDateConcept() {
        return endDateConcept;
    }

    @JsonProperty(FlowsheetConstant.END_DATE_CONCEPT)
    public void setEndDateConcept(String endDateConcept) {
        this.endDateConcept = endDateConcept;
    }
}
