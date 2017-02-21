package org.openmrs.module.endtb.admin.persister;

import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.hibernate.FlushMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.endtb.admin.constants.SAETemplateConstants;
import org.openmrs.module.endtb.admin.models.SaeEncounterRow;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SaeEncounterPersisterIT  extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private SaeEncounterPersister saeEncounterPersister;

    @Autowired
    DbSessionFactory sessionFactory;

    private FlushMode flushMode;

    @Autowired
    private BahmniObsService bahmniObsService;

    @Before
    public void setUp() throws Exception {
        flushMode = sessionFactory.getCurrentSession().getFlushMode();
        sessionFactory.getCurrentSession().setFlushMode(FlushMode.MANUAL);

        executeDataSet("diagnosisMetadata.xml");
        executeDataSet("dispositionMetadata.xml");
        executeDataSet("saeMetaData.xml");
        executeDataSet("patientProgramTestData.xml");
        executeDataSet("saeTestData.xml");
        saeEncounterPersister.init(Context.getUserContext(), null);
    }

    @After
    public void destroy() {
        sessionFactory.getCurrentSession().setFlushMode(flushMode);

    }

    @Test
    public void itShouldImportSAEEncounter() throws Exception{
        SaeEncounterRow encounterRow = createSaeEncounterRow("Bedaquiline", "TRUE", "Dose reduced");

        saeEncounterPersister.persist(encounterRow);

        Context.openSession();
        Context.authenticate("admin", "test");
        Collection<BahmniObservation> bahmniObservations = bahmniObsService.getObservationsForPatientProgram("ppuuid2", Arrays.asList(SAETemplateConstants.SAE_TEMPLATE));
        Context.closeSession();

        BahmniObservation SAEObservation = bahmniObservations.stream().findFirst().get();
        BahmniObservation SAEOutcome = filterByConceptName(SAEObservation, SAETemplateConstants.SAE_OUTCOME_PV);

        List<BahmniObservation> SAETbTreatments = SAEOutcome.getGroupMembers()
                .stream()
                .filter(observation -> observation.getConcept().getName().equalsIgnoreCase(SAETemplateConstants.SAE_TB_DRUG_TREATMENT))
                .collect(Collectors.toList());
        assertEquals(1, SAETbTreatments.size());

        BahmniObservation SAETbTreatmentPV = SAETbTreatments.get(0);
        BahmniObservation SAETbDrug = filterByConceptName( SAETbTreatmentPV, SAETemplateConstants.SAE_TB_DRUG_NAME);
        BahmniObservation tbDrugFinalAction = filterByConceptName(SAETbTreatmentPV, SAETemplateConstants.SAE_TB_DRUG_FINAL_ACTION);

        assertEquals(encounterRow.tbDrug, SAETbDrug.getValueAsString());
        assertEquals(encounterRow.tbDrugFinalAction, tbDrugFinalAction.getValueAsString());
    }

    @Test
    public void itShouldNotDuplicateIdenticalExistingTBDrugSection() throws Exception{
        SaeEncounterRow encounterRow = createSaeEncounterRow("Bedaquiline", "TRUE", "Dose reduced");

        saeEncounterPersister.persist(encounterRow);
        saeEncounterPersister.persist(encounterRow);

        Context.openSession();
        Context.authenticate("admin", "test");
        Collection<BahmniObservation> bahmniObservations = bahmniObsService.getObservationsForPatientProgram("ppuuid2", Arrays.asList(SAETemplateConstants.SAE_TEMPLATE));
        Context.closeSession();

        BahmniObservation SAEObservation = bahmniObservations.stream().findFirst().get();
        BahmniObservation SAEOutcome = filterByConceptName(SAEObservation, SAETemplateConstants.SAE_OUTCOME_PV);

        List<BahmniObservation> SAETbTreatments = SAEOutcome.getGroupMembers()
                .stream()
                .filter(observation -> observation.getConcept().getName().equalsIgnoreCase(SAETemplateConstants.SAE_TB_DRUG_TREATMENT))
                .collect(Collectors.toList());
        assertEquals(1, SAETbTreatments.size());
    }

    private BahmniObservation filterByConceptName(BahmniObservation parentObservation, String conceptName) {
        return parentObservation.getGroupMembers()
                .stream()
                .filter(observation -> observation.getConcept().getName().equalsIgnoreCase(conceptName))
                .findFirst()
                .get();
    }

    private SaeEncounterRow createSaeEncounterRow(String drugName, String isRelated, String finalAction) {
        SaeEncounterRow encounterRow = new SaeEncounterRow();
        encounterRow.dateOfSaeOnset = "2017-02-01";
        encounterRow.dateOfSaeReport = "2017-02-02";
        encounterRow.saeTerm = "Hypertension";
        encounterRow.dateOfSaeOutcome = "";//2017-02-03
        encounterRow.saeOutcome = "";//Not resolved
        encounterRow.saeRelatedTbDrug = "TRUE";
        encounterRow.tbDrug = drugName;
        encounterRow.tbDrugFinalAction = finalAction;
        encounterRow.otherCausalFactor="";
        encounterRow.nonTBdrug = "";
        encounterRow.otherSaeTerm="";
        encounterRow.coMorbidity="";
        encounterRow.tbDrugRelated= isRelated;
        encounterRow.registrationNumber="REG123456";
        encounterRow.saeCaseNumber="";
        encounterRow.maxSeverityOfSae="";
        encounterRow.saeOtherCasualFactors="";
        return encounterRow;
    }

}