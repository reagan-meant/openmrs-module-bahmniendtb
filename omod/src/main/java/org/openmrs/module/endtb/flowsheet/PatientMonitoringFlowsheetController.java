package org.openmrs.module.endtb.flowsheet;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.openmrs.Concept;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetAttribute;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
public class PatientMonitoringFlowsheetController extends BaseRestController {

    private static Logger logger = Logger.getLogger(PatientMonitoringFlowsheetController.class);
    private static final String PATIENT_MONITORING_CONFIG_LOCATION = "endtb.patientMonitoring.configLocation";
    private static final String EMR_PRIMARY_IDENTIFIER_TYPE = "emr.primaryIdentifierType";

    @Autowired
    private PatientMonitoringFlowsheetService patientMonitoringFlowsheetService;

    @Autowired
    PatientService patientService;

    @Autowired
    OrderService orderService;

    @Autowired
    ConceptService conceptService;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/endtb";

    @RequestMapping(value = baseUrl + "/patientFlowsheet", method = RequestMethod.GET)
    @ResponseBody
    public Flowsheet retrievePatientFlowSheet(@RequestParam("patientUuid") String patientUuid,
                                              @RequestParam("programUuid") String programUuid,
                                              @RequestParam(value="startDate") String startDate,
                                              @RequestParam(value="stopDate", required = false) String stopDate) throws Exception {
        return patientMonitoringFlowsheetService.getFlowsheetForPatientProgram(patientUuid, programUuid, startDate, stopDate, Context.getAdministrationService().getGlobalProperty(PATIENT_MONITORING_CONFIG_LOCATION));
    }

    @RequestMapping(value= baseUrl + "/patientFlowsheetAttributes", method = RequestMethod.GET)
    @ResponseBody
    public FlowsheetAttribute retrieveFlowsheetAttributes(@RequestParam("patientProgramUuid") String patientProgramUuid) throws Exception {

        PatientIdentifierType primaryIdentifierType = patientService.getPatientIdentifierTypeByUuid(administrationService.getGlobalProperty(EMR_PRIMARY_IDENTIFIER_TYPE));
        OrderType orderType = orderService.getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
        BahmniPatientProgram bahmniPatientProgram = (BahmniPatientProgram) Context.getService(BahmniProgramWorkflowService.class).getPatientProgramByUuid(patientProgramUuid);

        Set<Concept> conceptsForDrugs = new HashSet<>();
        conceptsForDrugs.add(conceptService.getConceptByName(EndTBConstants.DRUG_BDQ));
        conceptsForDrugs.add(conceptService.getConceptByName(EndTBConstants.DRUG_DELAMANID));

        return patientMonitoringFlowsheetService.getFlowsheetAttributesForPatientProgram(bahmniPatientProgram, primaryIdentifierType, orderType, conceptsForDrugs);
    }

    @RequestMapping(value= baseUrl + "/startDateForDrugs", method = RequestMethod.GET)
    @ResponseBody
    public Date getStartDateForDrugConcepts(@RequestParam("patientProgramUuid") String patientProgramUuid, @RequestParam("drugConcepts") Set<String> drugConcepts) throws Exception {
        return patientMonitoringFlowsheetService.getStartDateForDrugConcepts(patientProgramUuid, drugConcepts);
    }

}