package org.openmrs.module.endtb.flowsheet.service;

import org.openmrs.module.endtb.flowsheet.models.Flowsheet;

public interface PatientMonitoringFlowsheetService {
    Flowsheet getFlowsheetForPatientProgram(String patientProgramUuid, String configFilePath) throws Exception;
}
