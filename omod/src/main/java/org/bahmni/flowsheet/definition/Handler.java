package org.bahmni.flowsheet.definition;


import org.openmrs.PatientProgram;

import java.util.Date;

public interface Handler {
    Date getDate(PatientProgram patientProgram);
}
