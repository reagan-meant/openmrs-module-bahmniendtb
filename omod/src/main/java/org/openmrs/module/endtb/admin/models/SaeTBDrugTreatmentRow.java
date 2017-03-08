package org.openmrs.module.endtb.admin.models;

import org.bahmni.csv.annotation.CSVHeader;

public class SaeTBDrugTreatmentRow {

    @CSVHeader(name = "TB drug")
    public String tbDrug;

    @CSVHeader(name = "TB drug:final action")
    public String tbDrugFinalAction;

    @CSVHeader(name = "TB drug:related?")
    public String tbDrugRelated;

}
