package org.openmrs.module.bahmniendtb.dataintegrity.rules;

import org.openmrs.module.dataintegrity.rule.RuleResult;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

public class MissingOnsetDateAEViolationTest {

    @Mock
    ConceptService conceptService;

    @Mock
    MissingValuesHelper missingValuesHelper;

    @InjectMocks
    MissingOnsetDateAEViolation missingOnsetDateAEViolation;

    @Mock
    Concept aeReportingDateConcept;

    @Mock
    Concept aeOnsetDateConcept;

    @Before
    public void setUp(){
        initMocks(this);
    }

    @Test
    public void ensureMissingEventBecameSeriousViolationsAreReturned() throws Exception {
        List<RuleResult<PatientProgram>> outputMock = new ArrayList<>();
        outputMock.add(new RuleResult<PatientProgram>());

        when(conceptService.getConceptByName(EndTBConstants.AE_REPORTING_DATE)).thenReturn(aeReportingDateConcept);
        when(conceptService.getConceptByName(EndTBConstants.AE_ONSET_DATE)).thenReturn(aeOnsetDateConcept);
        when(missingValuesHelper
                .getMissingObsInObsSetViolations(any(String.class),  any(String.class), any(List.class)))
                .thenReturn(outputMock);

        List<RuleResult<PatientProgram>> result = missingOnsetDateAEViolation.evaluate();

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(missingValuesHelper)
                .getMissingObsInObsSetViolations(   eq(AE_ADVERSE_EVENT_TEMPLATE),
                                                    eq(AE_REPORTING_DATE),
                                                    argument.capture());
        assertEquals(2, argument.getValue().size());
        assertEquals(1, result.size());
    }
}