package org.openmrs.module.endtb.bahmniextn.formconditions;

import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.dao.impl.ObsDaoImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.bahmniendtb.formconditions.RulesForFormFilled;
import org.openmrs.module.episodes.dao.impl.EpisodeDAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RulesForFormFilledTest {

    private RulesForFormFilled rulesForFormFilled;
    private BahmniEncounterTransaction bahmniEncounterTransaction;

    @Mock
    private ObsDao obsDao;

    @Mock
    private EpisodeDAO episodeDAO;

    @Mock
    private EncounterService encounterService;

    @Before
    public void setUp() {
        initMocks(this);
        rulesForFormFilled = new RulesForFormFilled(obsDao, episodeDAO, encounterService);
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnBahmniEncounterTransactionIfFormFilledForFirstTime() {
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Baseline Template"));
        BahmniEncounterTransaction actualResult = rulesForFormFilled.update(bahmniEncounterTransaction);
        BahmniEncounterTransaction expectedResult = bahmniEncounterTransaction;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldThrowExceptionIfFormFilledAlready() {
        Obs obs = new Obs();
        Encounter encounter = new Encounter();
        obs.setEncounter(encounter);
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs));
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Baseline Template"));
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Baseline Template is already filled for this treatment");
        rulesForFormFilled.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldReturnBahmniEncounterTransactionIfBaselineFormIsEditied() {
        Obs obs = new Obs();
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs));
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Baseline Template"));
        bahmniEncounterTransaction.getObservations().iterator().next().setUuid(obs.getUuid());
        BahmniEncounterTransaction actualResult = rulesForFormFilled.update(bahmniEncounterTransaction);
        BahmniEncounterTransaction expectedResult = bahmniEncounterTransaction;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldThrowExceptionIfBaselineFormFilledTwiceInSingleSave() {
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(new ArrayList<Obs>());
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Baseline Template", "Baseline Template"));
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Baseline Template is already filled for this treatment");
        rulesForFormFilled.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldReturnBahmniEncounterTransactionIfMonthlyCompletenessFormIsFilledOnlyOnceForMonthYear() {
        Obs obs = new Obs();
        obs.setObsDatetime(DateTime.now().plusMonths(5).toDate());
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs));
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Monthly Treatment Completeness Template"));
        bahmniEncounterTransaction.getObservations().iterator().next().setObservationDateTime(new Date());
        BahmniEncounterTransaction actualResult = rulesForFormFilled.update(bahmniEncounterTransaction);
        BahmniEncounterTransaction expectedResult = bahmniEncounterTransaction;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldThrowExceptionIfFormFilledForSameMonthYearTwice() {
        Obs obs = new Obs();
        obs.setObsDatetime(new Date());
        Encounter encounter = new Encounter();
        obs.setEncounter(encounter);
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs));
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Monthly Treatment Completeness Template"));
        bahmniEncounterTransaction.getObservations().iterator().next().setObservationDateTime(new Date());
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Monthly Treatment Completeness Template is already filled for this month and year for this treatment");
        rulesForFormFilled.update(bahmniEncounterTransaction);
    }

    @Test
    public void shouldReturnBahmniEncounterTransactionIfMonthlyCompletenessFormIsEditedInCurrentEncounter() {
        Obs obs = new Obs();
        obs.setObsDatetime(new Date());
        obs.setUuid("obsUuid");
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs));
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Monthly Treatment Completeness Template"));
        bahmniEncounterTransaction.getObservations().iterator().next().setObservationDateTime(new Date());
        bahmniEncounterTransaction.getObservations().iterator().next().setUuid("obsUuid");
        BahmniEncounterTransaction actualResult = rulesForFormFilled.update(bahmniEncounterTransaction);
        BahmniEncounterTransaction expectedResult = bahmniEncounterTransaction;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldThrowExceptionIfMonthlyCompletenessFormIsFilledTwiceInSingleSave() {
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(new ArrayList<Obs>());
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Monthly Treatment Completeness Template", "Monthly Treatment Completeness Template"));
        for (BahmniObservation observation: bahmniEncounterTransaction.getObservations()) {
            observation.setObservationDateTime(new Date());
        }
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Monthly Treatment Completeness Template is already filled for this month and year for this treatment");
        rulesForFormFilled.update(bahmniEncounterTransaction);
    }


    @Test
    public void shouldNotThrowExceptionWhenCorrectingTheExistingMTCFormsFilledOnSameMonth() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Obs obs1 = new Obs();
        obs1.setObsDatetime(simpleDateFormat.parse("2015-01-01"));
        obs1.setUuid("uuid1");
        Obs obs2 = new Obs();
        obs2.setObsDatetime(simpleDateFormat.parse("2015-01-01"));
        obs2.setUuid("uuid2");
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs1, obs2));
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Monthly Treatment Completeness Template", "Monthly Treatment Completeness Template"));

        Iterator<BahmniObservation> iterator = bahmniEncounterTransaction.getObservations().iterator();
        iterator.next().setUuid("uuid1").setObservationDateTime(simpleDateFormat.parse("2015-01-01"));
        iterator.next().setUuid("uuid2").setObservationDateTime(simpleDateFormat.parse("2015-03-01"));

        BahmniEncounterTransaction actualResult = rulesForFormFilled.update(bahmniEncounterTransaction);
        BahmniEncounterTransaction expectedResult = bahmniEncounterTransaction;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldNotThrowExceptionWhenSavingVoidedFormFilledOnSameMonth() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Obs obs1 = new Obs();
        obs1.setObsDatetime(simpleDateFormat.parse("2015-01-01"));
        obs1.setUuid("uuid1");

        Obs obs2 = new Obs();
        obs2.setObsDatetime(simpleDateFormat.parse("2015-01-01"));
        obs2.setUuid("uuid2");
        when(obsDao.getObsByPatientProgramUuidAndConceptNames(any(String.class), any(List.class), any(Integer.class), any(ObsDaoImpl.OrderBy.class), any(Date.class), any(Date.class))).thenReturn(Arrays.asList(obs1, obs2));
        bahmniEncounterTransaction = getBahmniEncounterTransaction(Arrays.asList("Monthly Treatment Completeness Template", "Monthly Treatment Completeness Template"));

        BahmniObservation observation1 = new BahmniObservation();
        observation1.setValue("");

        Iterator<BahmniObservation> iterator = bahmniEncounterTransaction.getObservations().iterator();
        iterator.next().setUuid("uuid1").setObservationDateTime(simpleDateFormat.parse("2015-01-01")).setGroupMembers(Arrays.asList(observation1));
        iterator.next().setUuid("uuid2").setObservationDateTime(simpleDateFormat.parse("2015-01-01"));

        BahmniEncounterTransaction actualResult = rulesForFormFilled.update(bahmniEncounterTransaction);
        BahmniEncounterTransaction expectedResult = bahmniEncounterTransaction;
        assertEquals(expectedResult, actualResult);
    }

    private BahmniEncounterTransaction getBahmniEncounterTransaction(List<String> conceptNames) {
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        BahmniObservation bahmniObservation;
        List<BahmniObservation> bahmniObservationList = new ArrayList<BahmniObservation>();
        for (String conceptName: conceptNames) {
            bahmniObservation = new BahmniObservation();
            bahmniObservation.addGroupMember(new BahmniObservation().setValue("Value"));
            bahmniObservation.setConcept(new EncounterTransaction.Concept("uuid", conceptName, true, null, null, null, null, null));
            bahmniObservationList.add(bahmniObservation);
        }
        bahmniEncounterTransaction.setObservations(bahmniObservationList);
        bahmniEncounterTransaction.setPatientProgramUuid("uuid");
        return bahmniEncounterTransaction;
    }

}
