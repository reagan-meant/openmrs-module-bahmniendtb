package org.openmrs.module.endtb.bahmniextn.builder;

import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;

import java.util.List;

public class BahmniEncounterTransactionBuilder {
    BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();

    public BahmniEncounterTransaction build() {
        return bahmniEncounterTransaction;
    }

    public BahmniEncounterTransactionBuilder withObservations(List<BahmniObservation> bahmniObservations) {
        bahmniEncounterTransaction.setObservations(bahmniObservations);
        return this;
    }
}
