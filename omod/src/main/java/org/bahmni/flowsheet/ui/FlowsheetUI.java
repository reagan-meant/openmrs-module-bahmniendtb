package org.bahmni.flowsheet.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlowsheetUI {

    private Set<String> flowsheetHeader;
    private Map<String, List<String>> flowsheetData;
    private String highlightedMilestone;


    public Set<String> getFlowsheetHeader() {
        if(null == this.flowsheetHeader) {
            this.flowsheetHeader = new LinkedHashSet<>();
        }
        return this.flowsheetHeader;
    }

    public void setFlowsheetHeader(Set<String> flowsheetHeader) {
        this.flowsheetHeader = flowsheetHeader;
    }

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
        return highlightedMilestone;
    }

    public void setHighlightedMilestone(String highlightedMilestone) {
        this.highlightedMilestone = highlightedMilestone;
    }
}
