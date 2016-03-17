package org.openmrs.module.endtb.bahmniextn.builder;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;

public class BahmniObservationBuilder {
    private BahmniObservation bahmniObservation = new BahmniObservation();

    public BahmniObservation build() {
        return bahmniObservation;
    }

    public BahmniObservationBuilder withConcept(String name, boolean isSet, String datatype) {
        bahmniObservation.setConcept(new EncounterTransaction.Concept(name, name, isSet, datatype, null, "Misc", "Adverse Events", null));
        return this;
    }

    public BahmniObservationBuilder withValue(String value) {
        bahmniObservation.setValue(value);
        return this;
    }

    public BahmniObservationBuilder withMember(BahmniObservation dateOfOnset) {
        bahmniObservation.addGroupMember(dateOfOnset);
        return this;
    }
}
