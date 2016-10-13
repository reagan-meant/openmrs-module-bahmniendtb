package org.bahmni.flowsheet.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.bahmni.flowsheet.constants.FlowsheetConstant;

import java.util.ArrayList;
import java.util.List;

public class FlowsheetConfig {
    private List<MilestoneConfig> milestoneConfigs;
    private List<QuestionConfig> questionConfigs = new ArrayList<>();

    public List<QuestionConfig> getQuestionConfigs() {
        return questionConfigs;
    }

    @JsonProperty(FlowsheetConstant.QUESTIONS)
    public void setQuestionConfigs(List<QuestionConfig> questionConfigs) {
        this.questionConfigs = questionConfigs;
    }

    @JsonProperty(FlowsheetConstant.MILESTONES)
    public List<MilestoneConfig> getMilestoneConfigs() {
        return milestoneConfigs;
    }

    public void setMilestoneConfigs(List<MilestoneConfig> milestoneConfigs) {
        this.milestoneConfigs = milestoneConfigs;
    }

    public QuestionConfig getQuestionConfigByName(String name){
        for (QuestionConfig questionConfig : questionConfigs) {
            if(questionConfig.getName().equals(name)){
                return questionConfig;
            }
        }
        return null;
    }
}
