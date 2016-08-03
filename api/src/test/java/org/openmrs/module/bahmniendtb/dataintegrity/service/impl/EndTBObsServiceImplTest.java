package org.openmrs.module.bahmniendtb.dataintegrity.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.episodes.Episode;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class EndTBObsServiceImplTest {

    @Mock
    public ConceptService mockConceptService;

    @Mock
    private Concept treatmentInitiationTemplateConcept;

    @Mock
    private Concept treatmentEligibilityConcept;

    @Mock
    private Concept treatmentNewDrugConsentConcept;

    @InjectMocks
    EndTBObsService endTBObsService = new EndTBObsServiceImpl();

    @Before
    public void setup(){
        initMocks(this);
    }

    @Test
    public void ensureThatTreatmentInitiationObsIsReturned(){
        Obs expectedObs = new Obs();
        expectedObs.setConcept(treatmentInitiationTemplateConcept);

        Obs obs2 = new Obs();
        obs2.setConcept(new Concept());

        Obs obs3 = new Obs();
        obs3.setConcept(new Concept());

        Encounter encounter = new Encounter();
        encounter.setObs(Sets.newSet(expectedObs, obs2, obs3));

        when(mockConceptService.getConceptByName(EndTBConstants.FSN_TREATMENT_INITIATION_FORM)).thenReturn(treatmentInitiationTemplateConcept);

        Obs actualObs = endTBObsService.getTreatmentInitiationObsForEncounter(encounter);

        assertNotNull(actualObs);
        assertEquals(treatmentInitiationTemplateConcept, actualObs.getConcept());
        assertEquals(expectedObs,actualObs);
    }

    @Test
    public void ensureThatTreatmentInitiationObsIsNullWhenNotFound(){
        Obs obs2 = new Obs();
        obs2.setConcept(new Concept());

        Obs obs3 = new Obs();
        obs3.setConcept(new Concept());

        Encounter encounter = new Encounter();
        encounter.setObs(Sets.newSet(obs2, obs3));

        when(mockConceptService.getConceptByName(EndTBConstants.FSN_TREATMENT_INITIATION_FORM)).thenReturn(treatmentInitiationTemplateConcept);

        Obs actualObs = endTBObsService.getTreatmentInitiationObsForEncounter(encounter);

        assertNull(actualObs);
    }

    @Test
    public void ensureThatTIListIsReturnedForEpiosode(){
        Obs obs11 = new Obs();
        obs11.setConcept(treatmentInitiationTemplateConcept);

        Obs obs12 = new Obs();
        obs12.setConcept(new Concept());

        Encounter encounter1 = new Encounter();
        encounter1.setObs(Sets.newSet(obs11,obs12));

        Obs obs21 = new Obs();
        obs21.setConcept(new Concept());

        Obs obs22 = new Obs();
        obs22.setConcept(treatmentInitiationTemplateConcept);

        Encounter encounter2 = new Encounter();
        encounter2.setObs(Sets.newSet(obs22,obs21));

        Episode episode = new Episode();
        episode.addEncounter(encounter1);
        episode.addEncounter(encounter2);

        when(mockConceptService.getConceptByName(EndTBConstants.FSN_TREATMENT_INITIATION_FORM)).thenReturn(treatmentInitiationTemplateConcept);
        List<Obs> obsList = endTBObsService.getTreamentInitiationObsForEpisode(episode);

        assertNotNull(obsList);
        assertEquals(2, obsList.size());
        assertTrue(obsList.contains(obs11));
        assertTrue(obsList.contains(obs22));
    }

    @Test
    public void ensureThatTIListIsReturnedCorrectlyForEncountersWithoutTI(){

        Obs obs12 = new Obs();
        obs12.setConcept(new Concept());

        Encounter encounter1 = new Encounter();
        encounter1.setObs(Sets.newSet(obs12));

        Episode episode = new Episode();
        episode.addEncounter(encounter1);

        when(mockConceptService.getConceptByName(EndTBConstants.FSN_TREATMENT_INITIATION_FORM)).thenReturn(treatmentInitiationTemplateConcept);
        List<Obs> obsList = endTBObsService.getTreamentInitiationObsForEpisode(episode);

        assertNotNull(obsList);
        assertEquals(0, obsList.size());
    }

    @Test
    public void ensureThatChildObsIsReturnedByConcept(){
        Obs parentObs = createObs(treatmentInitiationTemplateConcept);
        Obs childObs1  = createObs(treatmentEligibilityConcept);
        Obs childObs2 = createObs(treatmentNewDrugConsentConcept);

        parentObs.addGroupMember(childObs1);
        childObs1.addGroupMember(childObs2);

        Obs actualObs = endTBObsService.getChildObsByConcept(parentObs, treatmentNewDrugConsentConcept);

        assertEquals(childObs2, actualObs);
    }

    @Test
    public void ensureThatChildObsIsReturnedByConceptInAnyLevel(){
        Obs parentObs = createObs(treatmentInitiationTemplateConcept);
        Obs childObs1  = createObs(treatmentEligibilityConcept);
        Obs childObs2 = createObs(treatmentNewDrugConsentConcept);

        parentObs.addGroupMember(childObs1);
        parentObs.addGroupMember(childObs2);

        Obs actualObs = endTBObsService.getChildObsByConcept(parentObs, treatmentNewDrugConsentConcept);

        assertEquals(childObs2, actualObs);
    }

    @Test
    public void ensureThatNullIsReturnedIfNoChildIsAvailable(){
        Obs parentObs = createObs(treatmentInitiationTemplateConcept);
        Obs childObs1  = createObs(treatmentEligibilityConcept);
        Obs childObs2 = createObs(new Concept());

        parentObs.addGroupMember(childObs1);
        parentObs.addGroupMember(childObs2);

        Obs actualObs = endTBObsService.getChildObsByConcept(parentObs, treatmentNewDrugConsentConcept);

        assertNull(actualObs);
    }


    private Obs createObs(Concept concept){
        Obs obs1 = new Obs();
        obs1.setConcept(concept);

        return  obs1;
    }


}