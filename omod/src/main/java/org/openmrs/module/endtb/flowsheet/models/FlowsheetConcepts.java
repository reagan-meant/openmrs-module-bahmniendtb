package org.openmrs.module.endtb.flowsheet.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

public class FlowsheetConcepts {
    private Set<String> clinicalConcepts;
    private Set<String> bacteriologyConcepts;
    private Set<String> drugConcepts;

    public Set<String> getClinicalConcepts() {
        if(CollectionUtils.isEmpty(clinicalConcepts)) {
            clinicalConcepts = new HashSet<>();
        }
        return clinicalConcepts;
    }

    @JsonProperty("obs")
    public void setClinicalConcepts(Set<String> clinicalConcepts) {
        this.clinicalConcepts = clinicalConcepts;
    }

    public Set<String> getBacteriologyConcepts() {
        if(CollectionUtils.isEmpty(bacteriologyConcepts)) {
            bacteriologyConcepts = new HashSet<>();
        }
        return bacteriologyConcepts;
    }

    @JsonProperty("bacteriology")
    public void setBacteriologyConcepts(Set<String> bacteriologyConcepts) {
        this.bacteriologyConcepts = bacteriologyConcepts;
    }

    public Set<String> getDrugConcepts() {
        if(CollectionUtils.isEmpty(drugConcepts)) {
            drugConcepts = new HashSet<>();
        }
        return drugConcepts;
    }

    @JsonProperty("drugs")
    public void setDrugConcepts(Set<String> drugConcepts) {
        this.drugConcepts = drugConcepts;
    }
}
