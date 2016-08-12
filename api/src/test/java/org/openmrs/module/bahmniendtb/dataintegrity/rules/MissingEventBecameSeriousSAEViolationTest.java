package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.episodes.Episode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class MissingEventBecameSeriousSAEViolationTest {
    @Mock
    ConceptService conceptService;

    @Mock
    DataintegrityRuleService dataintegrityRuleService;

    @Mock
    EndTBObsService endTBObsService;

    @InjectMocks
    MissingOnsetDateSAEViolation missingOnsetDateSAEViolation;

    @Mock
    Concept treatmentInitiationTemplateConcept;

    @Mock
    Concept treatmentEligiblityConcept;


    @Before
    public void setUp(){
        initMocks(this);
    }

//    @Test
    public void ensureMissingEventBecameSeriousViolationsAreReturned() throws Exception {
        List<Episode> episodeList = new ArrayList<>();
        Set<Episode> episodeSet = new HashSet<>();

        when(conceptService.getConceptByName(EndTBConstants.FSN_TREATMENT_INITIATION_CONSENT_QUESTION)).thenReturn(treatmentInitiationTemplateConcept);
        when(dataintegrityRuleService.getEpisodeForEncountersWithDrugs(any(List.class))).thenReturn(episodeList);
        when(dataintegrityRuleService.filterEpisodesForObsWithSpecifiedValue(eq(episodeList), eq(treatmentInitiationTemplateConcept), any(List.class))).thenReturn(episodeSet);

        List<RuleResult<PatientProgram>> ruleResults = missingOnsetDateSAEViolation.evaluate();

        assertEquals(ruleResults.size(), 0);
    }

}