package org.bahmni.flowsheet.api.models;

import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.Status;
import org.bahmni.flowsheet.config.QuestionConfig;
import org.bahmni.flowsheet.definition.models.QuestionDefinition;
import org.bahmni.module.bahmnicore.service.BahmniConceptService;
import org.openmrs.Concept;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class Milestone {
    private String name;
    private Date startDate;
    private Date endDate;
    private Set<Question> questions = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }


    public boolean isQuestionAdded(QuestionConfig questionConfig, BahmniConceptService bahmniConceptService, QuestionEvaluatorFactory questionEvaluatorFactory) {
        Question questionFromName = null;
        boolean isQuestionAdded = false;

        //create question with question Name

        if (questionConfig == null) return false;


          Set<Concept> conceptSet = new LinkedHashSet<>();
          for (String conceptName : questionConfig.getConcepts()) {
              Concept questionConcept = bahmniConceptService.getConceptByFullySpecifiedName(conceptName);
              if (questionConcept!=  null)conceptSet.add(questionConcept);
          }


          // if( the null condition) return false
        if (this.getStartDate() == null || this.getEndDate() == null ) return false;
          QuestionDefinition questionDefinetion =
                  new QuestionDefinition(questionConfig.getName(), conceptSet, getQuestionType(questionConfig.getType()));
          questionFromName = questionDefinetion.createQuestion();
          questionFromName.setResult(questionEvaluatorFactory.getEvaluator(questionFromName.getType()).evaluate(questionFromName.getConcepts(), this.getStartDate(), this.getEndDate()));

          isQuestionAdded = questionFromName.getResult().getStatus().equals(Status.DATA_ADDED);


        return isQuestionAdded;
    }

    private QuestionType getQuestionType(String type) {

        return type.equalsIgnoreCase("Drug") ? QuestionType.DRUG : QuestionType.OBS;
    }
}
