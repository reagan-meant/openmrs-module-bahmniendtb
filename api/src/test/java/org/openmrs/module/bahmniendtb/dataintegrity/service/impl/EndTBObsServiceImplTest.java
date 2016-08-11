package org.openmrs.module.bahmniendtb.dataintegrity.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.episodes.Episode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.bahmniendtb.EndTBConstants.FSN_TREATMENT_INITIATION_FORM;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class EndTBObsServiceImplTest {

    @Mock
    public ConceptService mockConceptService;

    @Mock
    private Concept treatmentInitiationTemplateConcept;

    @Mock
    private Concept treatmentEligibilityConcept;

    @Mock
    private Concept treatmentNewDrugConsentConcept;

    @Mock
    private AdministrationService administrationService;

    @InjectMocks
    EndTBObsService endTBObsService = new EndTBObsServiceImpl();

    @Mock
    private User authenticatedUser;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(Context.class);
        Mockito.when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);
    }

    @Test
    public void ensureThatTreatmentInitiationObsIsReturned(){
        ConceptName conceptName1 = new ConceptName();
        conceptName1.setName(FSN_TREATMENT_INITIATION_FORM);
        Concept concept1 = PowerMockito.mock(Concept.class);
        concept1.setUuid("concept_uuid_1");
        Obs expectedObs = new Obs();
        expectedObs.setConcept(concept1);

        ConceptName conceptName2 = new ConceptName();
        conceptName2.setName("Dummy Concept Name 2");
        Concept concept2 = PowerMockito.mock(Concept.class);
        concept2.setUuid("concept_uuid_2");
        Obs obs2 = new Obs();
        obs2.setConcept(concept2);

        ConceptName conceptName3 = new ConceptName();
        conceptName3.setName("Dummy Concept Name 3");
        Concept concept3 = PowerMockito.mock(Concept.class);
        concept3.setUuid("concept_uuid_3");
        Obs obs3 = new Obs();
        obs3.setConcept(concept3);

        Encounter encounter = new Encounter();
        encounter.setObs(Sets.newSet(expectedObs, obs2, obs3));

        when(concept1.getName()).thenReturn(conceptName1);
        when(concept2.getName()).thenReturn(conceptName2);
        when(concept3.getName()).thenReturn(conceptName3);

        Obs actualObs = endTBObsService.getObsForEncounter(encounter, FSN_TREATMENT_INITIATION_FORM);

        assertNotNull(actualObs);
        assertEquals(FSN_TREATMENT_INITIATION_FORM, actualObs.getConcept().getName().getName());
        assertEquals(expectedObs,actualObs);
    }

    @Test
    public void ensureThatTreatmentInitiationObsIsNullWhenNotFound(){
        ConceptName conceptName1 = new ConceptName();
        conceptName1.setName("Dummy Concept Name 1");
        Concept concept1 = PowerMockito.mock(Concept.class);
        concept1.setUuid("concept_uuid_1");
        Obs obs1 = new Obs();
        obs1.setConcept(concept1);

        ConceptName conceptName2 = new ConceptName();
        conceptName2.setName("Dummy Concept Name 2");
        Concept concept2 = PowerMockito.mock(Concept.class);
        concept2.setUuid("concept_uuid_2");
        Obs obs2 = new Obs();
        obs2.setConcept(concept2);

        Encounter encounter = new Encounter();
        encounter.setObs(Sets.newSet(obs1, obs2));

        when(concept1.getName()).thenReturn(conceptName1);
        when(concept2.getName()).thenReturn(conceptName2);
        when(mockConceptService.getConceptByName(FSN_TREATMENT_INITIATION_FORM)).thenReturn(treatmentInitiationTemplateConcept);

        Obs actualObs = endTBObsService.getObsForEncounter(encounter, FSN_TREATMENT_INITIATION_FORM);

        assertNull(actualObs);
    }

    @Test
    public void ensureThatTIListIsReturnedForEpiosode(){
        ConceptName conceptName11 = new ConceptName();
        conceptName11.setName(FSN_TREATMENT_INITIATION_FORM);
        Concept concept11 = PowerMockito.mock(Concept.class);
        concept11.setUuid("concept_uuid_11");
        Obs obs11 = new Obs();
        obs11.setConcept(concept11);

        ConceptName conceptName12 = new ConceptName();
        conceptName12.setName("Dummy Concept Name 12");
        Concept concept12 = PowerMockito.mock(Concept.class);
        concept12.setUuid("concept_uuid_12");
        Obs obs12 = new Obs();
        obs12.setConcept(concept12);

        Encounter encounter1 = new Encounter();
        encounter1.setObs(Sets.newSet(obs11,obs12));

        ConceptName conceptName21 = new ConceptName();
        conceptName21.setName(FSN_TREATMENT_INITIATION_FORM);
        Concept concept21 = PowerMockito.mock(Concept.class);
        concept21.setUuid("concept_uuid_21");
        Obs obs21 = new Obs();
        obs21.setConcept(concept21);

        ConceptName conceptName22 = new ConceptName();
        conceptName22.setName("Dummy Concept Name 22");
        Concept concept22 = PowerMockito.mock(Concept.class);
        concept22.setUuid("concept_uuid_22");
        Obs obs22 = new Obs();
        obs22.setConcept(concept22);

        Encounter encounter2 = new Encounter();
        encounter2.setObs(Sets.newSet(obs22,obs21));

        Episode episode = new Episode();
        episode.addEncounter(encounter1);
        episode.addEncounter(encounter2);

        when(concept11.getName()).thenReturn(conceptName11);
        when(concept12.getName()).thenReturn(conceptName12);
        when(concept21.getName()).thenReturn(conceptName21);
        when(concept22.getName()).thenReturn(conceptName22);

        List<Obs> obsList = endTBObsService.getObsForEpisode(episode, FSN_TREATMENT_INITIATION_FORM);

        assertNotNull(obsList);
        assertEquals(2, obsList.size());
        assertTrue(obsList.contains(obs11));
        assertTrue(obsList.contains(obs21));
    }

    @Test
    public void ensureThatTIListIsReturnedCorrectlyForEncountersWithoutTI(){

        ConceptName conceptName = new ConceptName();
        conceptName.setName("Dummy Concept Name 1");
        Concept concept = PowerMockito.mock(Concept.class);
        concept.setUuid("concept_uuid");
        Obs obs = new Obs();
        obs.setConcept(concept);

        Encounter encounter = new Encounter();
        encounter.setObs(Sets.newSet(obs));

        Episode episode = new Episode();
        episode.addEncounter(encounter);

        when(concept.getName()).thenReturn(conceptName);
        List<Obs> obsList = endTBObsService.getObsForEpisode(episode, FSN_TREATMENT_INITIATION_FORM);

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