package org.openmrs.module.endtb.flowsheet.models;


import java.util.Date;

public class FlowsheetAttribute {

    private String patientEMRID;
    private String treatmentRegistrationNumber;
    private Date newDrugTreatmentStartDate;
    private Date mdrtbTreatmentStartDate;
    private String consentForEndtbStudy;
    private String hivStatus;
    private String baselineXRayStatus;

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

    public String getConsentForEndtbStudy() {
        return consentForEndtbStudy;
    }

    public void setConsentForEndtbStudy(String consentForEndtbStudy) {
        this.consentForEndtbStudy = consentForEndtbStudy;
    }

    public String getHivStatus() {
        return hivStatus;
    }

    public void setHivStatus(String hivStatus) {
        this.hivStatus = hivStatus;
    }

    public String getBaselineXRayStatus() {
        return baselineXRayStatus;
    }

    public void setBaselineXRayStatus(String baselineXRayStatus) {
        this.baselineXRayStatus = baselineXRayStatus;
    }
}
