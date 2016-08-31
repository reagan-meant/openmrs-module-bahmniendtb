package org.openmrs.module.endtb.flowsheet;

import org.apache.log4j.Logger;
import org.openmrs.module.endtb.flowsheet.models.FlowSheet;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientMonitoringFlowsheet extends BaseRestController {

    private static Logger logger = Logger.getLogger(PatientMonitoringFlowsheet.class);
    private static final String ENDTB_EXPORTS_LOCATION = "endtb.exports.location";

    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/endtb";

    @RequestMapping(value = baseUrl + "/patientFlowsheet", method = RequestMethod.GET)
    @ResponseBody
    public FlowSheet retrievePatientFlowSheet(@RequestParam("programUuid") String programUuid) {
        FlowSheet flowSheet = new FlowSheet();

        //add dummy flowsheet header
        flowSheet.addFlowSheetHeader("Milestone 1");
        flowSheet.addFlowSheetHeader("Milestone 2");
        flowSheet.addFlowSheetHeader("Milestone 3");
        flowSheet.addFlowSheetHeader("Milestone 4");
        flowSheet.addFlowSheetHeader("Milestone 5");
        flowSheet.addFlowSheetHeader("Milestone 6");
        flowSheet.addFlowSheetHeader("Milestone 7");
        flowSheet.addFlowSheetHeader("Milestone 8");

        //add dummy flowsheet data
        flowSheet.addFlowSheetData("Weight", "Y");
        flowSheet.addFlowSheetData("Weight", "N");
        flowSheet.addFlowSheetData("Weight", "Y");
        flowSheet.addFlowSheetData("Weight", "N");

        flowSheet.addFlowSheetData("Height", "N");
        flowSheet.addFlowSheetData("Height", "Y");
        flowSheet.addFlowSheetData("Height", "Y");
        flowSheet.addFlowSheetData("Height", "Y");

        flowSheet.addFlowSheetData("BMI", "N");
        flowSheet.addFlowSheetData("BMI", "N");
        flowSheet.addFlowSheetData("BMI", "Y");
        flowSheet.addFlowSheetData("BMI", "N");

        return flowSheet;
    }
}