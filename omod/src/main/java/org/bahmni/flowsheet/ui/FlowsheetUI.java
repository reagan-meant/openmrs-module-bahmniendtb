package org.bahmni.flowsheet.ui;

import org.bahmni.flowsheet.api.models.Milestone;
import org.bahmni.flowsheet.config.FlowsheetConfig;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlowsheetUI {

    private List<Milestone> milestones;
    private Map<String, List<String>> flowsheetData;
    private String highlightedCurrentMilestone;
    private String endDateMilestone;
    private FlowsheetConfig flowsheetConfig;

    public Map<String, List<String>> getFlowsheetData() {
        if(null == this.flowsheetData) {
            this.flowsheetData = new LinkedHashMap<>();
        }
        return this.flowsheetData;
    }

    public void setFlowsheetData(Map<String, List<String>> flowsheetData) {
        this.flowsheetData = flowsheetData;
    }

    public String getHighlightedCurrentMilestone() {
        return highlightedCurrentMilestone;
    }

    public void setHighlightedCurrentMilestone(String highlightedCurrentMilestone) {
        this.highlightedCurrentMilestone = highlightedCurrentMilestone;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

	public String getEndDateMilestone() {
		return endDateMilestone;
	}

	public void setEndDateMilestone(String endDateMilestone) {
		this.endDateMilestone = endDateMilestone;
	}

    public FlowsheetConfig getFlowsheetConfig() {
        return flowsheetConfig;
    }

    public void setFlowsheetConfig(FlowsheetConfig flowsheetConfig) {
        this.flowsheetConfig = flowsheetConfig;
    }
}
