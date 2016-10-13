package org.bahmni.flowsheet.definition.models;

import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.models.Question;
import org.openmrs.Concept;

import java.util.Set;

public class QuestionDefinition {
    private String name;
    private Set<Concept> concepts;
    private QuestionType questionType;

    public QuestionDefinition(String name, Set<Concept> concepts, QuestionType questionType) {
        this.name = name;
        this.concepts = concepts;
        this.questionType = questionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public Question createQuestion() {
        return new Question(name, concepts, questionType);
    }

}
