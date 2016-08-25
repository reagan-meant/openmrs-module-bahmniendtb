package org.openmrs.module.bahmniendtb;

public class EndTBConstants {
    private EndTBConstants() {
    }

    // Baseline
    public static final String BASELINE_FORM = "Baseline Template";
    public static final String BASELINE_CASEDEFINITION_WHO_GROUP = "Baseline, WHO registration group";
    public static final String BASELINE_CASEDEFINITION_DISEASE_SITE = "Baseline, Disease site";
    public static final String BASELINE_CASEDEFINITION_CONFIRMATION_METHOD = "Baseline, Method of MDR-TB confirmation";
    public static final String BASELINE_CASEDEFINITION_MDR_TB_DIAGNOSIS_METHOD = "Baseline, MDR-TB diagnosis method";
    public static final String BASELINE_DRUG_RESISTANCE = "Baseline, Drug resistance";
    public static final String BASELINE_CONFIRMED_DRUG_RESISTANT_TB = "Confirmed drug resistant TB";
    public static final String BASELINE_PROFILE_UNCONFIRMED="Profile unconfirmed";
    public static final String BASELINE_CONFIRMED_DRUG_SUSCEPTIBLE="Confirmed drug susceptible";

    //Treatment Initiation Template
    public static final String FSN_TREATMENT_INITIATION_FORM = "Treatment Initiation Template";
    public static final String FSN_TREATMENT_INITIATION_CONSENT_QUESTION = "TI, Has the Treatment with New Drugs Consent Form been explained and signed";
    public static final String FSN_TI_ENDTB_STUDY_CONSENT_QUESTION = "TI, Has the endTB Observational Study Consent Form been explained and signed";
    public static final String TI_IS_TREATMENT_START_DATE = "TI, Did the patient start treatment";
    public static final String TI_TREATMENT_START_DATE = "TUBERCULOSIS DRUG TREATMENT START DATE";

    //Outcome - End Of treatment Template
    public static final String EOT_TREATMENT_TEMPLATE = "Outcome End of Treatment Template";
    public static final String EOT_STOP_DATE = "Tuberculosis treatment end date";
    public static final String EOT_OUTCOME = "EOT, Outcome";

    //Adverse Event Template
    public static final String AE_ADVERSE_EVENT_TEMPLATE = "Adverse Events Template";
    public static final String AE_REPORTING_DATE = "AE Form, Date of AE report";
    public static final String AE_ONSET_DATE = "AE Form, Date of AE onset";

    //Serious Adverse Event Template
    public static final String SAE_ADVERSE_EVENT_TEMPLATE = "Serious Adverse Events Template";
    public static final String SAE_REPORTING_DATE = "SAE Form, Date of SAE report";
    public static final String SAE_ONSET_DATE = "SAE Form, Event onset date";
    public static final String SAE_EVENT_BECAME_SERIOUS_DATE = "SAE Form, Date event became serious";

    //Concept Name
    public static final String ALL_TB_DRUG = "All TB Drugs";
    public static final String DRUG_DELAMANID = "Delamanid";
    public static final String DRUG_BDQ = "Bedaquiline";
    public static final String FALSE = "False";
    public static final String UNKNOWN = "Unknown";
    public static final String TI_START_DATE = "TUBERCULOSIS DRUG TREATMENT START DATE";
    public static final String BACTERIOLOGICALLY_CONFIRMED = "Bacteriologically Confirmed";
    public static final int TWENTY_SEVEN_MONTHS_IN_DAYS = 821;

    //Bacteriology
    public static final String BACTERIOLOGY_SPECIMEN_COLLECTION_DATE = "Specimen Collection Date";
    public static final String BACTERIOLOGY_CULTURE_RESULTS = "Bacteriology, Culture results";
    public static final String BACTERIOLOGY_HAIN_ISONIAZID = "Bacteriology, Isoniazid";
    public static final String BACTERIOLOGY_ISONIAZID_2= "Bacteriology, Isoniazid 0.2 µg/ml result";
    public static final String SUSCEPTIBLE = "Susceptible";
    public static final String RESISTANT = "Resistant";
    public static final String BACTERIOLOGY_RIFAMPICIN = "Bacteriology, Rifampicin";
    public static final String BACTERIOLOGY_XPERT_RIFAMPICIN = "Bacteriology, RIF resistance result type";
    public static final String BACTERIOLOGY_DST_RIFAMPICIN = "Bacteriology, Rifampicin result";
    public static final String DETECTED = "Detected";
    public static final String NOT_DETECTED = "Not detected";


    //Default Comments
    public static final String TI_DEFAULT_COMMENT = "The Treatment Initiation form is not filled";
    public static final String EOT_DEFAULT_COMMENT = "The Outcome - End of Treatment form is not filled";
    public static final String AE_DEFAULT_COMMENT = "The Adverse Events form is not filled";
    public static final String SAE_DEFAULT_COMMENT = "The Serious Adverse Events form is not filled";
    public static final String BASELINE_DEFAULT_COMMENT = "The Baseline form is not filled";
    public static final String CULTURE_STATUS_MISSING_DEFAULT_COMMENT = "Their is no bacteriology culture for this patient in the first month";
    public static final String FIRST_LINE_RESISTANCE_RESULT_COMMENT = "No results by the end of first month of treatment.";
}
