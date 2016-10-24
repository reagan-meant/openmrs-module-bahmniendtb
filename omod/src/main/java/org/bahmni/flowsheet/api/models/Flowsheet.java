package org.bahmni.flowsheet.api.models;

import org.bahmni.flowsheet.api.QuestionType;
import org.openmrs.Concept;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Flowsheet {

    private Date startDate;
    private Set<Milestone> milestones;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Set<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(Set<Milestone> milestones) {
        this.milestones = milestones;
    }

    public void evaluate(QuestionEvaluatorFactory factory) {
        for (Milestone milestone : milestones) {
            for (Question question : milestone.getQuestions()) {
                question.setResult(factory.getEvaluator(question.getType()).evaluate(question.getConcepts(), milestone.getStartDate(), milestone.getEndDate()));
            }
        }
    }

    public Set<Concept> getObsFlowsheetConcepts() {
        Set<Concept> concepts = new HashSet<>();
        for (Milestone milestone : milestones) {
            for (Question question : milestone.getQuestions()) {
                if (question.getType().equals(QuestionType.OBS)) {
                    concepts.addAll(question.getConcepts());
                }
            }
        }
        return concepts;
    }
}
