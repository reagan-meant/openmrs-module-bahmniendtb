package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.module.dataintegrity.rule.RuleResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.MissingValuesHelper;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.bahmniendtb.EndTBConstants.SAE_ADVERSE_EVENT_TEMPLATE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.SAE_DEFAULT_COMMENT;
import static org.openmrs.module.bahmniendtb.EndTBConstants.SAE_EVENT_BECAME_SERIOUS_DATE;


public class MissingEventBecameSeriousSAEViolationTest {

    @Mock
    ConceptService conceptService;

    @Mock
    MissingValuesHelper missingValuesHelper;

    @InjectMocks
    MissingEventBecameSeriousSAEViolation missingEventBecameSeriousSAEViolation;

    @Mock
    Concept saeReportingDateConcept;

    @Mock
    Concept saeOnsetDateConcept;


    @Before
    public void setUp(){
        initMocks(this);
    }

    @Test
    public void ensureMissingEventBecameSeriousViolationsAreReturned() throws Exception {
        List<RuleResult<PatientProgram>> outputMock = new ArrayList<>();
        outputMock.add(new RuleResult<PatientProgram>());

        when(conceptService.getConceptByName(EndTBConstants.SAE_REPORTING_DATE)).thenReturn(saeReportingDateConcept);
        when(conceptService.getConceptByName(EndTBConstants.SAE_ONSET_DATE)).thenReturn(saeOnsetDateConcept);
        when(missingValuesHelper
                .getMissingObsInObsSetViolations(any(String.class), any(String.class), any(String.class), any(List.class)))
                .thenReturn(outputMock);

        List<RuleResult<PatientProgram>> result = missingEventBecameSeriousSAEViolation.evaluate();

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(missingValuesHelper)
                .getMissingObsInObsSetViolations(   eq(SAE_ADVERSE_EVENT_TEMPLATE),
                                                    eq(SAE_EVENT_BECAME_SERIOUS_DATE),
                                                    eq(SAE_DEFAULT_COMMENT),
                                                    argument.capture());
        assertEquals(2, argument.getValue().size());
        assertEquals(1, result.size());
    }

}