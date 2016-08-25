package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.DrugDSTViolationHelper;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.module.episodes.Episode;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.bahmniendtb.EndTBConstants.*;
import static org.openmrs.module.bahmniendtb.EndTBConstants.DETECTED;
import static org.openmrs.module.bahmniendtb.EndTBConstants.NOT_DETECTED;

public class FirstLineDSTViolationTest {

    @Mock
    ConceptService conceptService;


    @Mock
    DataintegrityRuleService dataintegrityRuleService;

    @Mock
    DrugDSTViolationHelper drugDSTViolationHelper;


    @Mock
    Concept treatmentStartDate;
    @Mock
    Concept baselineDrugResistance;
    @Mock
    Concept confirmedDrugResistantTB;
    @Mock
    Concept hainIsoniazid;
    @Mock
    Concept isoniazidTwoMg;
    @Mock
    Concept susceptible;
    @Mock
    Concept resistant;
    @Mock
    Concept bacteriologyRifampicin;
    @Mock
    Concept xpertRifampicin;
    @Mock
    Concept dstRifampicin;
    @Mock
    Concept detected;
    @Mock
    Concept nonDetected;

    @Mock
    Episode validEpisode;

    @Mock
    Episode episodeWithRifampicinResistance;

    @Mock
    Episode episodeWithIsoniazidResistance;

    @Mock
    Episode invalidEpisode;



    @InjectMocks
    FirstLineDSTViolation firstLineDSTViolation;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void ensureFirstLineDSTViolationsAreReturned() {


        List<RuleResult<PatientProgram>> outputMock = new ArrayList<>();
        Set< Episode> episodesWithCDRTB = new HashSet<>();
        episodesWithCDRTB.add(validEpisode);
        episodesWithCDRTB.add(episodeWithRifampicinResistance);
        episodesWithCDRTB.add(episodeWithIsoniazidResistance);
        episodesWithCDRTB.add(invalidEpisode);


        Set<Episode> validEpisodes = new HashSet<>();
        validEpisodes.add(validEpisode);
        validEpisodes.add(episodeWithIsoniazidResistance);
        validEpisodes.add(episodeWithRifampicinResistance);

        outputMock.add(new RuleResult<PatientProgram>());

        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);

        when(conceptService.getConceptByName(TI_TREATMENT_START_DATE)).thenReturn(treatmentStartDate);
        when(conceptService.getConceptByName(BASELINE_DRUG_RESISTANCE)).thenReturn(baselineDrugResistance);
        when(conceptService.getConceptByName(BASELINE_CONFIRMED_DRUG_RESISTANT_TB)).thenReturn(confirmedDrugResistantTB);
        when(conceptService.getConceptByName(BACTERIOLOGY_HAIN_ISONIAZID)).thenReturn(hainIsoniazid);
        when(conceptService.getConceptByName(BACTERIOLOGY_ISONIAZID_2)).thenReturn(isoniazidTwoMg);
        when(conceptService.getConceptByName(SUSCEPTIBLE)).thenReturn(susceptible);
        when(conceptService.getConceptByName(RESISTANT)).thenReturn(resistant);
        when(conceptService.getConceptByName(BACTERIOLOGY_RIFAMPICIN)).thenReturn(bacteriologyRifampicin);
        when(conceptService.getConceptByName(BACTERIOLOGY_XPERT_RIFAMPICIN)).thenReturn(xpertRifampicin);
        when(conceptService.getConceptByName(BACTERIOLOGY_DST_RIFAMPICIN)).thenReturn(dstRifampicin);
        when(conceptService.getConceptByName(DETECTED)).thenReturn(detected);
        when(conceptService.getConceptByName(NOT_DETECTED)).thenReturn(nonDetected);


        when(dataintegrityRuleService
                .getEpisodesWithRequiredObs(Arrays.asList(treatmentStartDate)))
                .thenReturn(episodesWithCDRTB);

        when(dataintegrityRuleService
                .filterEpisodesForCodedObsWithAnswersInList(new ArrayList<>(episodesWithCDRTB), baselineDrugResistance, Arrays.asList(confirmedDrugResistantTB)))
                .thenReturn(episodesWithCDRTB);

        when(dataintegrityRuleService
                .getEpisodesWithResistance(new ArrayList<>(episodesWithCDRTB), treatmentStartDate ,Arrays.asList(hainIsoniazid, isoniazidTwoMg), Arrays.asList(susceptible, resistant)))
                .thenReturn(episodesWithCDRTB);


        when(dataintegrityRuleService.getEpisodesWithResistance(new ArrayList<>(episodesWithCDRTB), treatmentStartDate ,
                Arrays.asList(bacteriologyRifampicin, xpertRifampicin, dstRifampicin), Arrays.asList(susceptible, resistant , detected, nonDetected))).thenReturn(validEpisodes);


        when(drugDSTViolationHelper.getInconsistenciesForQuestion(any(Set.class) , any(String.class) , any(String.class), any(String.class)))
                .thenReturn(outputMock);

        List<RuleResult<PatientProgram>> result = firstLineDSTViolation.evaluate();


        verify(drugDSTViolationHelper)
                .getInconsistenciesForQuestion(argument.capture(), eq(BASELINE_FORM), eq(BASELINE_DRUG_RESISTANCE), eq(FIRST_LINE_RESISTANCE_RESULT_COMMENT));
        assertEquals(outputMock, result);
        assertEquals(1, argument.getValue().size());
        assertEquals(invalidEpisode, argument.getValue().iterator().next());


    }

}