package org.openmrs.module.endtb.bahmniextn.formconditions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.endtb.bahmniextn.builder.BahmniEncounterTransactionBuilder;
import org.openmrs.module.endtb.bahmniextn.builder.BahmniObservationBuilder;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class AdverseEventFormConditionsCommandTest {
    private EncounterDataPreSaveCommand encounterDataPreSaveCommand = new AdverseEventFormConditionsCommand();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldThrowExceptionIfDateOfOnSetIsAfterDateOfReport() throws Exception {
        Date date = new Date();
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-11").build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-10").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReport).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("\"Date of onset\" should be before \"Date of report\" on adverse events form.");

        encounterDataPreSaveCommand.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldThrowExceptionIfDateOfOnSetIsAfterDateOfReportAndBothObservationsAreNotInTheSameLevel() throws Exception {
        Date date = new Date();
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-17").build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-07").build();
        BahmniObservation dateOfReportSet = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report Set", true, "N/A").withMember(dateOfReport).build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReportSet).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("\"Date of onset\" should be before \"Date of report\" on adverse events form.");

        encounterDataPreSaveCommand.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldNotThrowExcpetionIfDateOfOnSetIsBeforeDateOfReport() throws Exception {
        Date date = new Date();
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-07").build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-07").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReport).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        BahmniEncounterTransaction encounterTransaction = encounterDataPreSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(bahmniEncounterTransaction, encounterTransaction);
    }

    @Test
    public void shouldNotThrowExceptionIfOneOfTheFieldIsNotEntered() throws Exception {
        Date date = new Date();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-07").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfReport).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        BahmniEncounterTransaction encounterTransaction = encounterDataPreSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(bahmniEncounterTransaction, encounterTransaction);
    }

    @Test
    public void shouldNotThrowExceptionIfBothFieldIsNotEntered() throws Exception {
        Date date = new Date();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        BahmniEncounterTransaction encounterTransaction = encounterDataPreSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(bahmniEncounterTransaction, encounterTransaction);
    }

    @Test
    public void shouldNotThrowExceptionIfTheTemplateIsNotFilled() throws Exception {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();

        BahmniEncounterTransaction encounterTransaction = encounterDataPreSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(bahmniEncounterTransaction, encounterTransaction);
    }

}