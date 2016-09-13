package org.openmrs.module.bahmniendtb.dataintegrity.rules.helper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.DataintegrityRuleService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.openmrs.module.episodes.Episode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MissingValuesHelperTest {

    MissingValuesHelper missingValuesHelper;
    @Mock
    Obs obs;
    @Mock
    Concept concept1;
    @Mock
    Concept concept2;
    @Mock
    Episode episode;
    @Mock
    PatientProgram patientProgram;
    @Mock
    Program program;
    @Mock
    ConceptService conceptService;
    @Mock
    EndTBObsService endTBObsService;
    @Mock
    DataintegrityRuleService dataintegrityRuleService;

    @Before
    public void setUp(){
        initMocks(this);
        missingValuesHelper =
                new MissingValuesHelper(conceptService, dataintegrityRuleService, endTBObsService);
    }

    @Test
    public void shouldReturnEmptySetIfObsAreNotMissing() throws Exception {

        when(dataintegrityRuleService.getEpisodesWithRequiredObs(Arrays.asList(concept1,  concept2)))
                .thenReturn(new HashSet<Episode>());

        List<RuleResult<PatientProgram>> result =
                missingValuesHelper.getMissingObsInObsSetViolations(
                        "ParentConcept", "TargetQuestion", Arrays.asList(concept1,  concept2));

        assertEquals(0, result.size());
    }

    @Test
    public void shouldReturnNonEmptySetIfObsAreMissingInSet() throws Exception {
        Set<Episode> episodeSet = new HashSet<>();
        episodeSet.add(episode);
        when(patientProgram.getPatient()).thenReturn(new Patient(0));
        when(patientProgram.getProgram()).thenReturn(program);
        when(program.getUuid()).thenReturn("programUuid");
        when(patientProgram.getUuid()).thenReturn("enrollemntUuid");
        when(conceptService.getConcept(eq("TargetQuestion"))).thenReturn(concept1);
        when(dataintegrityRuleService.getEpisodesWithRequiredObs(Arrays.asList(concept1,  concept2))).thenReturn(episodeSet);

        when(endTBObsService.getAllObsForEpisode(eq(episode), any(String.class))).thenReturn(Arrays.asList(obs));

        when(endTBObsService.getChildObsByConcepts(any(Obs.class), any(List.class))).thenReturn(Arrays.asList(obs));

        when(endTBObsService.getChildObsByConcept(any(Obs.class), any(Concept.class))).thenReturn(obs);

        when(episode.getPatientPrograms()).thenReturn(new HashSet<>(Arrays.asList(patientProgram)));

        List<RuleResult<PatientProgram>> result =
                missingValuesHelper.getMissingObsInObsSetViolations(
                        "ParentConcept", "TargetQuestion", Arrays.asList(concept1,  concept2));

        assertEquals(1, result.size());
    }

    @Test
    public void shouldReturnMultipleObsIfObsAreMissingInSet() throws Exception {
        Set<Episode> episodeSet = new HashSet<>();
        episodeSet.add(episode);
        when(patientProgram.getPatient()).thenReturn(new Patient(0));
        when(patientProgram.getProgram()).thenReturn(program);
        when(program.getUuid()).thenReturn("programUuid");
        when(patientProgram.getUuid()).thenReturn("enrollemntUuid");
        when(conceptService.getConcept(eq("TargetQuestion"))).thenReturn(concept1);
        when(dataintegrityRuleService.getEpisodesWithRequiredObs(Arrays.asList(concept1,  concept2))).thenReturn(episodeSet);

        when(endTBObsService.getAllObsForEpisode(eq(episode), any(String.class))).thenReturn(Arrays.asList(obs, obs, obs, obs));

        when(endTBObsService.getChildObsByConcepts(any(Obs.class), any(List.class))).thenReturn(Arrays.asList(obs));

        when(endTBObsService.getChildObsByConcept(any(Obs.class), any(Concept.class))).thenReturn(obs);

        when(episode.getPatientPrograms()).thenReturn(new HashSet<>(Arrays.asList(patientProgram)));

        List<RuleResult<PatientProgram>> result =
                missingValuesHelper.getMissingObsInObsSetViolations(
                        "ParentConcept", "TargetQuestion", Arrays.asList(concept1,  concept2));

        assertEquals(4, result.size());
    }
}