package org.openmrs.module.endtb.flowsheet;

import org.apache.log4j.Logger;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.service.PatientMonitoringFlowsheetService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientMonitoringFlowsheet extends BaseRestController {

    private static Logger logger = Logger.getLogger(PatientMonitoringFlowsheet.class);
    private static final String ENDTB_EXPORTS_LOCATION = "endtb.exports.location";

    @Autowired
    private PatientMonitoringFlowsheetService patientMonitoringFlowsheetService;

    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/endtb";

    @RequestMapping(value = baseUrl + "/patientFlowsheet", method = RequestMethod.GET)
    @ResponseBody
    public Flowsheet retrievePatientFlowSheet(@RequestParam("programUuid") String programUuid) throws Exception {
//        return patientMonitoringFlowsheetService.getFlowsheetForPatientProgram(programUuid, "/var/www/bahmni_config/openmrs/apps/clinical/patientMonitoringConf.json");

        Flowsheet flowSheet = new Flowsheet();

        //add dummy flowsheet header
        flowSheet.addFlowSheetHeader("M1");
        flowSheet.addFlowSheetHeader("M2");
        flowSheet.addFlowSheetHeader("M3");
        flowSheet.addFlowSheetHeader("M4");
        flowSheet.addFlowSheetHeader("M5");
        flowSheet.addFlowSheetHeader("M6");
        flowSheet.addFlowSheetHeader("M7");
        flowSheet.addFlowSheetHeader("M8");
        flowSheet.addFlowSheetHeader("M9");
        flowSheet.addFlowSheetHeader("M10");
        flowSheet.addFlowSheetHeader("M11");
        flowSheet.addFlowSheetHeader("M12");
        flowSheet.addFlowSheetHeader("M13");
        flowSheet.addFlowSheetHeader("M14");
        flowSheet.addFlowSheetHeader("M15");
        flowSheet.addFlowSheetHeader("M16");
        flowSheet.addFlowSheetHeader("M17");
        flowSheet.addFlowSheetHeader("M18");
        flowSheet.addFlowSheetHeader("M19");
        flowSheet.addFlowSheetHeader("M20");
        flowSheet.addFlowSheetHeader("M21");
        flowSheet.addFlowSheetHeader("M22");
        flowSheet.addFlowSheetHeader("M23");
        flowSheet.addFlowSheetHeader("M24");
        flowSheet.addFlowSheetHeader("ET");
        flowSheet.addFlowSheetHeader("PT");

        //add dummy flowsheet data
        flowSheet.addFlowSheetData("Weight", "green");
        flowSheet.addFlowSheetData("Weight", "purple");
        flowSheet.addFlowSheetData("Weight", "green");
        flowSheet.addFlowSheetData("Weight", "grey");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "grey");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "grey");
        flowSheet.addFlowSheetData("Weight", "grey");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");
        flowSheet.addFlowSheetData("Weight", "yellow");

        flowSheet.addFlowSheetData("Height", "purple");
        flowSheet.addFlowSheetData("Height", "green");
        flowSheet.addFlowSheetData("Height", "green");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "yellow");
        flowSheet.addFlowSheetData("Height", "yellow");
        flowSheet.addFlowSheetData("Height", "yellow");
        flowSheet.addFlowSheetData("Height", "yellow");
        flowSheet.addFlowSheetData("Height", "yellow");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "yellow");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "grey");
        flowSheet.addFlowSheetData("Height", "yellow");

        flowSheet.addFlowSheetData("BMI", "purple");
        flowSheet.addFlowSheetData("BMI", "purple");
        flowSheet.addFlowSheetData("BMI", "green");
        flowSheet.addFlowSheetData("BMI", "green");
        flowSheet.addFlowSheetData("BMI", "green");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "yellow");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "grey");
        flowSheet.addFlowSheetData("BMI", "yellow");

        return flowSheet;
    }
}