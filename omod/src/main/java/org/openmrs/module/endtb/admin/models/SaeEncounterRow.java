package org.openmrs.module.endtb.admin.models;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;
import org.bahmni.csv.annotation.CSVRepeatingHeaders;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.bahmni.module.admin.csv.utils.CSVUtils.getDateFromString;

public class SaeEncounterRow extends CSVEntity {

    @CSVHeader(name = "Registration number")
    public String registrationNumber;

    @CSVHeader(name = "SAE case Number")
    public String saeCaseNumber;

    @CSVHeader(name = "SAE term")
    public String saeTerm;

    @CSVHeader(name = "Other SAE term")
    public String otherSaeTerm;

    @CSVHeader(name = "Date of SAE onset")
    public String dateOfSaeOnset;

    @CSVHeader(name = "Date of SAE report")
    public String dateOfSaeReport;

    @CSVHeader(name = "Maximum severity of SAE")
    public String maxSeverityOfSae;

    @CSVHeader(name = "Date of SAE outcome")
    public String dateOfSaeOutcome;

    @CSVHeader(name = "SAE Outcome")
    public String saeOutcome;

    @CSVHeader(name = "SAE related to TB drugs?")
    public String saeRelatedTbDrug;

    @CSVRepeatingHeaders(names = {"TB drug", "TB drug:final action", "TB drug:related?"}, type = SaeTBDrugTreatmentRow.class)
    public List<SaeTBDrugTreatmentRow> saeTBDrugTreatmentRows;

    public String saeOtherCasualFactors;

    @CSVHeader(name = "Non-TB drug")
    public String nonTBdrug;

    @CSVHeader(name = "Co-morbidity")
    public String coMorbidity;

    @CSVHeader(name = "Other causal factor")
    public String otherCausalFactor;

    public Date getEncounterDate() throws ParseException {
        return getDateFromString(dateOfSaeReport);
    }
}

