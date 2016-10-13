package org.bahmni.flowsheet.api;


public enum QuestionType {

    DRUG("Drug"), OBS("Obs");

    private String type;

    QuestionType(String type) {
        this.type = type;
    }
}
