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
        return patientMonitoringFlowsheetService.getFlowsheetForPatientProgram(programUuid, "/var/www/bahmni_config/openmrs/apps/clinical/patientMonitoringConf.json");
    }
}