package org.bahmni.flowsheet.definition.models;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.bahmni.flowsheet.api.models.Milestone;
import org.bahmni.flowsheet.api.models.Question;
import org.bahmni.flowsheet.definition.HandlerProvider;
import org.openmrs.PatientProgram;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class MilestoneDefinition {

    private String name;
    private Map<String, String> config;
    private String handler;
    private Set<QuestionDefinition> questionDefinitions;
    private HandlerProvider handlerProvider;


    public MilestoneDefinition(String name, Map<String, String> config,
                               String handler, HandlerProvider handlerProvider) {
        this.name = name;
        this.handler = handler;
        this.config = config;
        this.handlerProvider = handlerProvider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Set<QuestionDefinition> getQuestionDefinitions() {
        return questionDefinitions;
    }

    public void setQuestionDefinitions(Set<QuestionDefinition> questionDefinitions) {
        this.questionDefinitions = questionDefinitions;
    }

    public HandlerProvider getHandlerProvider() {
        return handlerProvider;
    }

    public void setHandlerProvider(HandlerProvider handlerProvider) {
        this.handlerProvider = handlerProvider;
    }

    public Milestone createMilestone(Date startDate, PatientProgram patientProgram) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Milestone milestone = new Milestone();
        milestone.setName(name);
        if (StringUtils.isNotEmpty(this.handler)) {
            startDate = handlerProvider.getHandler(this.handler).getDate(patientProgram);
            if (startDate == null) {
                return milestone;
            }
        }
        milestone.setStartDate(DateUtils.addDays(startDate, Integer.parseInt(config.get("min"))));
        milestone.setEndDate(DateUtils.addDays(startDate, Integer.parseInt(config.get("max"))));
        Set<Question> questions = new LinkedHashSet<>();
        for (QuestionDefinition questionDefinition : this.questionDefinitions) {
            questions.add(questionDefinition.createQuestion());
        }
        milestone.setQuestions(questions);
        return milestone;
    }
}
