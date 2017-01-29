package org.bahmni.flowsheet.ui;

import org.bahmni.flowsheet.api.models.Milestone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlowsheetUI {

    private List<Milestone> milestones;
    private Map<String, List<String>> flowsheetData;
    private String highlightedCurrentMilestone;
    private String endDateMilestone;

    public Map<String, List<String>> getFlowsheetData() {
        if(null == this.flowsheetData) {
            this.flowsheetData = new LinkedHashMap<>();
        }
        return this.flowsheetData;
    }

    public void setFlowsheetData(Map<String, List<String>> flowsheetData) {
        this.flowsheetData = flowsheetData;
    }

    public String getHighlightedMilestone() {
        return highlightedCurrentMilestone;
    }

    public void setHighlightedMilestone(String highlightedMilestone) {
        this.highlightedCurrentMilestone = highlightedMilestone;
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
    
}
