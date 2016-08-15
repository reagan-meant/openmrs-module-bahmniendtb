package org.openmrs.module.bahmniendtb;

public class EndTBConstants {
    private EndTBConstants() {
    }

    // Baseline
    public static final String BASELINE_FORM = "Baseline Template";
    public static final String BASELINE_CASEDEFINITION_WHO_GROUP = "Baseline, WHO registration group";
    public static final String BASELINE_CASEDEFINITION_DISEASE_SITE = "Baseline, Disease site";
    public static final String BASELINE_CASEDEFINITION_CONFIRMATION_METHOD = "Baseline, Method of MDR-TB confirmation";

    //Treatment Initiation Template
    public static final String FSN_TREATMENT_INITIATION_FORM = "Treatment Initiation Template";
    public static final String FSN_TREATMENT_INITIATION_CONSENT_QUESTION = "TI, Has the Treatment with New Drugs Consent Form been explained and signed";
    public static final String FSN_TI_ENDTB_STUDY_CONSENT_QUESTION = "TI, Has the endTB Observational Study Consent Form been explained and signed";
    public static final String TI_IS_TREATMENT_START_DATE = "TI, Did the patient start treatment";

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
    public static final int TWENTY_SEVEN_MONTHS_IN_DAYS = 821;

    //Default Comments
    public static final String TI_DEFAULT_COMMENT = "The Treatment Initiation form is not filled";
    public static final String EOT_DEFAULT_COMMENT = "The Outcome - End of Treatment form is not filled";
    public static final String AE_DEFAULT_COMMENT = "The Adverse Events form is not filled";
    public static final String SAE_DEFAULT_COMMENT = "The Serious Adverse Events form is not filled";
    public static final String BASELINE_DEFAULT_COMMENT = "The Baseline form is not filled";
}
