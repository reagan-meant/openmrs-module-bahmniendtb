package org.openmrs.module.endtb.flowsheet.service.impl;


import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Concept;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetAttribute;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.bahmni.module.bahmnicore.mapper.PatientIdentifierMapper.EMR_PRIMARY_IDENTIFIER_TYPE;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class PatientMonitoringFlowsheetServiceImplIT extends BaseModuleContextSensitiveTest {


    private BahmniProgramWorkflowService bahmniProgramWorkflowService;

    private PatientService patientService;

    private AdministrationService administrationService;

    private org.openmrs.api.OrderService orderService;

    private ConceptService conceptService;

    @Autowired
    PatientMonitoringFlowsheetService patientMonitoringFlowsheetService;

    @Before
    public void setUp() throws Exception {
        executeDataSet("patientProgramTestData.xml");
        bahmniProgramWorkflowService = Context.getService(BahmniProgramWorkflowService.class);
        patientService = Context.getPatientService();
        administrationService = Context.getAdministrationService();
        orderService = Context.getOrderService();
        conceptService = Context.getConceptService();
    }

    @Test
    public void shouldSetFlowsheetAttributes() {
        BahmniPatientProgram bahmniPatientProgram = (BahmniPatientProgram) bahmniProgramWorkflowService.getPatientProgramByUuid("dfdfoifo-dkcd-475d-b939-6d82327f36a3");
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid(administrationService.getGlobalProperty(EMR_PRIMARY_IDENTIFIER_TYPE));
        OrderType orderType = orderService.getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
        Set<Concept> conceptsForDrugs = new HashSet<>();
        conceptsForDrugs.add(conceptService.getConceptByName(EndTBConstants.DRUG_BDQ));
        conceptsForDrugs.add(conceptService.getConceptByName(EndTBConstants.DRUG_DELAMANID));

        FlowsheetAttribute flowsheetAttribute = patientMonitoringFlowsheetService.getFlowsheetAttributesForPatientProgram(bahmniPatientProgram, patientIdentifierType, orderType, conceptsForDrugs);

        assertEquals("ARM10021", flowsheetAttribute.getPatientEMRID());
        assertEquals("2016-09-16 00:00:00.0", flowsheetAttribute.getMdrtbTreatmentStartDate().toString());
        assertEquals("2016-01-27 00:30:00.0", flowsheetAttribute.getNewDrugTreatmentStartDate().toString());
        assertEquals("REG12345", flowsheetAttribute.getTreatmentRegistrationNumber());
    }


}
