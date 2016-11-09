package org.openmrs.module.endtb.bahmniextn.formconditions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniendtb.formconditions.DateConditionsForFilledForms;
import org.openmrs.module.endtb.bahmniextn.builder.BahmniEncounterTransactionBuilder;
import org.openmrs.module.endtb.bahmniextn.builder.BahmniObservationBuilder;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class DateConditionsForFilledFormsTest {
    private EncounterDataPreSaveCommand encounterDataPreSaveCommand = new DateConditionsForFilledForms();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldThrowExceptionIfDateOfOnSetIsAfterDateOfReport() throws Exception {
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-11").build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-10").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReport).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Date of onset (11 Mar 16) should be before Date of report (10 Mar 16) on Adverse Events Template");

        encounterDataPreSaveCommand.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldThrowExceptionIfDateOfOnSetIsAfterDateOfReportAndBothObservationsAreNotInTheSameLevel() throws Exception {
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-17").build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-07").build();
        BahmniObservation dateOfReportSet = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report Set", true, "N/A").withMember(dateOfReport).build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReportSet).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Date of onset (17 Mar 16) should be before Date of report (07 Mar 16) on Adverse Events Template");

        encounterDataPreSaveCommand.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldThrowExceptionIfDateOfOnSetIsAfterDateOfReportWhenMultipleAdverseEventsForms() throws Exception {
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-11").build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-11").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReport).build();
        BahmniObservation dateOfOnset1 = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-13").build();
        BahmniObservation dateOfReport1 = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-12").build();
        BahmniObservation adverseEventsTemplate1 = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset1).withMember(dateOfReport1).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Arrays.asList(adverseEventsTemplate,adverseEventsTemplate1)).build();

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Date of onset (13 Mar 16) should be before Date of report (12 Mar 16) on Adverse Events Template");

        encounterDataPreSaveCommand.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldNotThrowExcpetionIfDateOfOnSetIsBeforeDateOfReport() throws Exception {
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue("2016-03-07").build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-07").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReport).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        BahmniEncounterTransaction encounterTransaction = encounterDataPreSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(bahmniEncounterTransaction, encounterTransaction);
    }

    @Test
    public void shouldNotThrowExcpetionIfDateOfOnSetIsNotSelected() throws Exception {
        BahmniObservation dateOfOnset = new BahmniObservationBuilder().withConcept("AE Form, Date of AE onset", false, "Date").withValue(null).build();
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-07").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfOnset).withMember(dateOfReport).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        BahmniEncounterTransaction encounterTransaction = encounterDataPreSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(bahmniEncounterTransaction, encounterTransaction);
    }

    @Test
    public void shouldNotThrowExceptionIfOneOfTheFieldIsNotEntered() throws Exception {
        BahmniObservation dateOfReport = new BahmniObservationBuilder().withConcept("AE Form, Date of AE report", false, "Date").withValue("2016-03-07").build();
        BahmniObservation adverseEventsTemplate = new BahmniObservationBuilder().withConcept("Adverse Events Template", true, "N/A").withMember(dateOfReport).build();
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransactionBuilder().withObservations(Collections.singletonList(adverseEventsTemplate)).build();

        BahmniEncounterTransaction encounterTransaction = encounterDataPreSaveCommand.update(bahmniEncounterTransaction);

        assertEquals(bahmniEncounterTransaction, encounterTransaction);
    }

    @Test
    public void shouldNotThrowExceptionIfBothFieldIsNotEntered() throws Exception {
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