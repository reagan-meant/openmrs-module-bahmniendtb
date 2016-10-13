package org.bahmni.flowsheet.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.bahmni.flowsheet.constants.FlowsheetConstant;

import java.util.List;

public class QuestionConfig {
    private String name;
    private List<String> concepts;
    private String type;

    public String getName() {
        return name;
    }

    @JsonProperty(FlowsheetConstant.NAME)
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConcepts() {
        return concepts;
    }

    @JsonProperty(FlowsheetConstant.CONCEPTS)
    public void setConcepts(List<String> concepts) {
        this.concepts = concepts;
    }

    public String getType() {
        return type;
    }

    @JsonProperty(FlowsheetConstant.TYPE)
    public void setType(String type) {
        this.type = type;
    }
}
