package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetConstant;

public class FlowsheetEntities {
    private FlowsheetConcept clinicalConcepts;
    private FlowsheetConcept bacteriologyConcepts;
    private FlowsheetConcept drugConcepts;

    public FlowsheetConcept getClinicalConcepts() {
        return this.clinicalConcepts;
    }

    @JsonProperty(FlowsheetConstant.CLINICAL)
    public void setClinicalConcepts(FlowsheetConcept clinicalConcepts) {
        this.clinicalConcepts = clinicalConcepts;
    }

    public FlowsheetConcept getBacteriologyConcepts() {
        return this.bacteriologyConcepts;
    }

    @JsonProperty(FlowsheetConstant.BACTERIOLOGY)
    public void setBacteriologyConcepts(FlowsheetConcept bacteriologyConcepts) {
        this.bacteriologyConcepts = bacteriologyConcepts;
    }

    public FlowsheetConcept getDrugConcepts() {
        return this.drugConcepts;
    }

    @JsonProperty(FlowsheetConstant.DRUGS)
    public void setDrugConcepts(FlowsheetConcept drugConcepts) {
        this.drugConcepts = drugConcepts;
    }

    public FlowsheetConcept getFlowSheetConceptByType(String type) {
        FlowsheetConcept flowsheetConcept = null;
        switch (type) {
            case FlowsheetConstant.CLINICAL:
                flowsheetConcept = getClinicalConcepts();
                break;
            case FlowsheetConstant.BACTERIOLOGY:
                flowsheetConcept = getBacteriologyConcepts();
                break;
            case FlowsheetConstant.DRUGS:
                flowsheetConcept = getDrugConcepts();
                break;
        }
        return flowsheetConcept;
    }
}
