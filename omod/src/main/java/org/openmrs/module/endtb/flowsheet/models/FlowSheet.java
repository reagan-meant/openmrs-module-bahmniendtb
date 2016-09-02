package org.openmrs.module.endtb.flowsheet.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Flowsheet {

    private List<String> flowsheetHeader = new ArrayList<>();
    private Map<String, List<String>> flowsheetData = new LinkedHashMap<>();


    public List<String> getFlowsheetHeader() {
        return flowsheetHeader;
    }

    public void setFlowsheetHeader(List<String> flowsheetHeader) {
        this.flowsheetHeader = flowsheetHeader;
    }

    public Map<String, List<String>> getFlowsheetData() {
        return flowsheetData;
    }

    public void setFlowsheetData(Map<String, List<String>> flowsheetData) {
        this.flowsheetData = flowsheetData;
    }

    public Flowsheet addFlowSheetHeader(String header) {
        flowsheetHeader.add(header);
        return this;
    }

    public void addFlowSheetData(String conceptName, String value) {
        if(!flowsheetData.containsKey(conceptName)) {
            flowsheetData.put(conceptName, new ArrayList<String>());
        }
        flowsheetData.get(conceptName).add(value);
    }
}
