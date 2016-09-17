package org.openmrs.module.endtb.flowsheet.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Flowsheet {

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

    public Flowsheet addFlowSheetHeader(String header) {
        getFlowsheetHeader().add(header);
        return this;
    }

    public void addFlowSheetData(String conceptName, String value) {
        if (!getFlowsheetData().containsKey(conceptName)) {
            getFlowsheetData().put(conceptName, new ArrayList<String>());
        }
        getFlowsheetData().get(conceptName).add(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flowsheet flowsheet = (Flowsheet) o;

        if (!getFlowsheetHeader().equals(flowsheet.getFlowsheetHeader())) return false;
        return getFlowsheetData().equals(flowsheet.getFlowsheetData());

    }

    @Override
    public int hashCode() {
        int result = getFlowsheetHeader().hashCode();
        result = 31 * result + getFlowsheetData().hashCode();
        return result;
    }
}
