package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import org.apache.commons.collections.CollectionUtils;
import org.openmrs.module.endtb.flowsheet.constants.FlowsheetContant;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlowsheetEntities {
    private FlowsheetConcept clinicalConcepts;
    private FlowsheetConcept bacteriologyConcepts;
    private FlowsheetConcept drugConcepts;

    public FlowsheetConcept getClinicalConcepts() {
        return this.clinicalConcepts;
    }

    @JsonProperty(FlowsheetContant.CLINICAL)
    public void setClinicalConcepts(FlowsheetConcept clinicalConcepts) {
        this.clinicalConcepts = clinicalConcepts;
    }

    public FlowsheetConcept getBacteriologyConcepts() {
        return this.bacteriologyConcepts;
    }

    @JsonProperty(FlowsheetContant.BACTERIOLOGY)
    public void setBacteriologyConcepts(FlowsheetConcept bacteriologyConcepts) {
        this.bacteriologyConcepts = bacteriologyConcepts;
    }

    public FlowsheetConcept getDrugConcepts() {
        return this.drugConcepts;
    }

    @JsonProperty(FlowsheetContant.DRUGS)
    public void setDrugConcepts(FlowsheetConcept drugConcepts) {
        this.drugConcepts = drugConcepts;
    }

    public FlowsheetConcept getFlowSheetConceptByType(String type) {
        FlowsheetConcept flowsheetConcept = null;
        switch (type) {
            case FlowsheetContant.CLINICAL:
                flowsheetConcept = getClinicalConcepts();
                break;
            case FlowsheetContant.BACTERIOLOGY:
                flowsheetConcept = getBacteriologyConcepts();
                break;
            case FlowsheetContant.DRUGS:
                flowsheetConcept = getDrugConcepts();
                break;
        }
        return flowsheetConcept;
    }
}
