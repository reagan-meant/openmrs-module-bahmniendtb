package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.CollectionUtils;

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

    @JsonProperty("obs")
    public void setClinicalConcepts(FlowsheetConcept clinicalConcepts) {
        this.clinicalConcepts = clinicalConcepts;
    }

    public FlowsheetConcept getBacteriologyConcepts() {
        return this.bacteriologyConcepts;
    }

    @JsonProperty("bacteriology")
    public void setBacteriologyConcepts(FlowsheetConcept bacteriologyConcepts) {
        this.bacteriologyConcepts = bacteriologyConcepts;
    }

    public FlowsheetConcept getDrugConcepts() {
        return this.drugConcepts;
    }

    @JsonProperty("drugs")
    public void setDrugConcepts(FlowsheetConcept drugConcepts) {
        this.drugConcepts = drugConcepts;
    }
}
