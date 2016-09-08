package org.openmrs.module.endtb.flowsheet.service;

import org.openmrs.module.endtb.flowsheet.models.Flowsheet;

public interface PatientMonitoringFlowsheetService {
    Flowsheet getFlowsheetForPatientProgram(String patientUuid, String patientProgramUuid, String configFilePath) throws Exception;
}
