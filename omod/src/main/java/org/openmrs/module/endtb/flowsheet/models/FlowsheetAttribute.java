package org.openmrs.module.endtb.flowsheet.models;


import java.util.Date;

public class FlowsheetAttribute {

    private String patientEMRID;
    private String treatmentRegistrationNumber;
    private Date newDrugTreatmentStartDate;
    private Date mdrtbTreatmentStartDate;

    public String getPatientEMRID() {
        return patientEMRID;
    }

    public void setPatientEMRID(String patientEMRID) {
        this.patientEMRID = patientEMRID;
    }


    public Date getNewDrugTreatmentStartDate() {
        return newDrugTreatmentStartDate;
    }

    public void setNewDrugTreatmentStartDate(Date newDrugTreatmentStartDate) {
        this.newDrugTreatmentStartDate = newDrugTreatmentStartDate;
    }

    public Date getMdrtbTreatmentStartDate() {
        return mdrtbTreatmentStartDate;
    }

    public void setMdrtbTreatmentStartDate(Date mdrtbTreatmentStartDate) {
        this.mdrtbTreatmentStartDate = mdrtbTreatmentStartDate;
    }

    public String getTreatmentRegistrationNumber() {
        return treatmentRegistrationNumber;

    }

    public void setTreatmentRegistrationNumber(String treatmentRegistrationNumber) {
        this.treatmentRegistrationNumber = treatmentRegistrationNumber;
    }


}
