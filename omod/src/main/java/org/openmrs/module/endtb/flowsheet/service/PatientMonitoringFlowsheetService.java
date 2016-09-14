package org.openmrs.module.endtb.flowsheet.service;

import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.BahmniPatientProgram;
import org.openmrs.Concept;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.endtb.flowsheet.models.Flowsheet;
import org.openmrs.module.endtb.flowsheet.models.FlowsheetAttribute;

import java.util.Set;

public interface PatientMonitoringFlowsheetService {
    Flowsheet getFlowsheetForPatientProgram(String patientUuid, String patientProgramUuid, String configFilePath) throws Exception;

    FlowsheetAttribute getFlowsheetAttributesForPatientProgram(BahmniPatientProgram bahmniPatientProgram, PatientIdentifierType patientIdentifierType, OrderType orderType, Set<Concept> concepts);
}
