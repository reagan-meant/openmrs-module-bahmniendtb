package org.bahmni.flowsheet.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.bahmni.flowsheet.constants.FlowsheetConstant;

public class Config {

    private String min;
    private String max;

    public String getMin() {
        return min;
    }

    @JsonProperty(FlowsheetConstant.MIN)
    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    @JsonProperty(FlowsheetConstant.MAX)
    public void setMax(String max) {
        this.max = max;
    }
}
