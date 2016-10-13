package org.bahmni.flowsheet.api.models;

import org.bahmni.flowsheet.api.QuestionType;
import org.openmrs.Concept;

import java.util.Set;

public class Question {


    private String name;
    private Set<Concept> concepts;

    private Result result;
    private QuestionType type;

    public Question(String name, Set<Concept> concepts, QuestionType type) {
        this.name = name;
        this.concepts = concepts;
        this.type = type;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
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

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;

    }

}
