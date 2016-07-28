package org.openmrs.module.bahmniendtb.dataintegrity.service;

import java.util.List;

public class ObsQueryHelper {
    private String conceptPath;

    public void setConceptPath(String conceptPath) {
        this.conceptPath = conceptPath;
    }

    public void setObsValuesAllowed(List<String> validValuesForObs, boolean includeMissingObs) {

    }
}
