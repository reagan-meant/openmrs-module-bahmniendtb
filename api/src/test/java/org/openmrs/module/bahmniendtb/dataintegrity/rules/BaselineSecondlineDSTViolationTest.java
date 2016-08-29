package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.junit.Before;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.bahmniendtb.EndTBConstants.*;
import static org.openmrs.module.bahmniendtb.EndTBConstants.BASELINE_DRUG_RESISTANCE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.FIRST_LINE_RESISTANCE_RESULT_COMMENT;

public class BaselineSecondlineDSTViolationTest {
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
    Concept bacteriologyFluoroquinolone;
    @Mock
    Concept ofloxacin;
    @Mock
    Concept susceptible;
    @Mock
    Concept resistant;
    @Mock
    Concept levofloxacin;
    @Mock
    Concept moxifloxacin;
    @Mock
    Concept bacteriologyMTBDRslInjectable;
    @Mock
    Concept amikacin;
    @Mock
    Concept kanamycin;
    @Mock
    Concept capreomycin;

    @Mock
    Episode validEpisode;

    @Mock
    Episode episodesWithFluoroquinoloneResistance;

    @Mock
    Episode episodesWithInjectableResistance;

    @Mock
    Episode invalidEpisode;



    @InjectMocks
    BaselineSecondlineDSTViolation baselineSecondLineDSTViolation;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void ensureFirstLineDSTViolationsAreReturned() {

        List<RuleResult<PatientProgram>> outputMock = new ArrayList<>();
        Set< Episode> episodesWithCDRTB = new HashSet<>();
        episodesWithCDRTB.add(validEpisode);
        episodesWithCDRTB.add(episodesWithFluoroquinoloneResistance);
        episodesWithCDRTB.add(episodesWithInjectableResistance);
        episodesWithCDRTB.add(invalidEpisode);


        Set<Episode> validEpisodes = new HashSet<>();
        validEpisodes.add(validEpisode);
        validEpisodes.add(episodesWithFluoroquinoloneResistance);
        validEpisodes.add(episodesWithInjectableResistance);

        outputMock.add(new RuleResult<PatientProgram>());

        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);

        when(conceptService.getConceptByName(TI_TREATMENT_START_DATE)).thenReturn(treatmentStartDate);
        when(conceptService.getConceptByName(BASELINE_DRUG_RESISTANCE)).thenReturn(baselineDrugResistance);
        when(conceptService.getConceptByName(BASELINE_CONFIRMED_DRUG_RESISTANT_TB)).thenReturn(confirmedDrugResistantTB);
        when(conceptService.getConceptByName(BACTERIOLOGY_FLUOROQUINOLONE)).thenReturn(bacteriologyFluoroquinolone);
        when(conceptService.getConceptByName(BACTERIOLOGY_OFLOXACIN)).thenReturn(ofloxacin);
        when(conceptService.getConceptByName(SUSCEPTIBLE)).thenReturn(susceptible);
        when(conceptService.getConceptByName(RESISTANT)).thenReturn(resistant);
        when(conceptService.getConceptByName(BACTERIOLOGY_LEVOFLOXACIN)).thenReturn(levofloxacin);
        when(conceptService.getConceptByName(BACTERIOLOGY_MOXIFLOXACIN_5)).thenReturn(moxifloxacin);
        when(conceptService.getConceptByName(BACTERIOLOGY_MTBDRSL_INJECTABLE)).thenReturn(bacteriologyMTBDRslInjectable);
        when(conceptService.getConceptByName(BACTERIOLOGY_AMIKACIN)).thenReturn(amikacin);
        when(conceptService.getConceptByName(BACTERIOLOGY_KANAMYCIN)).thenReturn(kanamycin);
        when(conceptService.getConceptByName(BACTERIOLOGY_CAPREOMYCIN)).thenReturn(capreomycin);


        when(dataintegrityRuleService
                .getEpisodesWithRequiredObs(Arrays.asList(treatmentStartDate)))
                .thenReturn(episodesWithCDRTB);

        when(dataintegrityRuleService
                .filterEpisodesForCodedObsWithAnswersInList(new ArrayList<>(episodesWithCDRTB), baselineDrugResistance, Arrays.asList(confirmedDrugResistantTB)))
                .thenReturn(episodesWithCDRTB);

        when(dataintegrityRuleService
                .getEpisodesWithResistance(new ArrayList<>(episodesWithCDRTB), treatmentStartDate ,Arrays.asList(levofloxacin, moxifloxacin, ofloxacin, bacteriologyFluoroquinolone), Arrays.asList(susceptible, resistant)))
                .thenReturn(episodesWithCDRTB);


        when(dataintegrityRuleService.getEpisodesWithResistance(new ArrayList<>(episodesWithCDRTB), treatmentStartDate ,
                Arrays.asList(bacteriologyMTBDRslInjectable, amikacin, capreomycin, kanamycin), Arrays.asList(susceptible, resistant))).thenReturn(validEpisodes);


        when(drugDSTViolationHelper.getInconsistenciesForQuestion(any(Set.class) , any(String.class) , any(String.class), any(String.class)))
                .thenReturn(outputMock);

        List<RuleResult<PatientProgram>> result = baselineSecondLineDSTViolation.evaluate();


        verify(drugDSTViolationHelper)
                .getInconsistenciesForQuestion(argument.capture(), eq(BASELINE_FORM), eq(BASELINE_DRUG_RESISTANCE), eq(FIRST_LINE_RESISTANCE_RESULT_COMMENT));
        assertEquals(outputMock, result);
        assertEquals(1, argument.getValue().size());
        assertEquals(invalidEpisode, argument.getValue().iterator().next());


    }
}