package org.openmrs.module.bahmniendtb.formconditions;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Component
public class DateConditionsForFilledForms implements EncounterDataPreSaveCommand {

    public static final String ADVERSE_EVENTS_TEMPLATE = "Adverse Events Template";
    public static final String SERIOUS_ADVERSE_EVENTS_TEMPLATE = "Serious Adverse Events Template";
    public static final String ADVERSE_EVENTS_DATE_OF_ONSET = "AE Form, Date of AE onset";
    public static final String ADVERSE_EVENTS_DATE_OF_REPORT = "AE Form, Date of AE report";
    public static final String SERIOUS_ADVERSE_EVENTS_DATE_OF_ONSET = "SAE Form, Event onset date";
    public static final String SERIOUS_ADVERSE_EVENTS_DATE_OF_REPORT = "SAE Form, Date of SAE report";
    public static final SimpleDateFormat clientSideDateFormat = new SimpleDateFormat("dd MMM yy");

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        checkDateOfOnSetReportForTemplates(bahmniEncounterTransaction, ADVERSE_EVENTS_TEMPLATE, ADVERSE_EVENTS_DATE_OF_ONSET, ADVERSE_EVENTS_DATE_OF_REPORT);
//        checkDateOfOnSetReportForTemplates(bahmniEncounterTransaction, SERIOUS_ADVERSE_EVENTS_TEMPLATE, SERIOUS_ADVERSE_EVENTS_DATE_OF_ONSET, SERIOUS_ADVERSE_EVENTS_DATE_OF_REPORT);
        return bahmniEncounterTransaction;
    }

    private void checkDateOfOnSetReportForTemplates(BahmniEncounterTransaction bahmniEncounterTransaction, String templateConceptName, String dateOfOnsetConceptName, String dateOfReportConceptName) {
        for (BahmniObservation observation : bahmniEncounterTransaction.getObservations()) {
            if (templateConceptName.equals(observation.getConcept().getName())) {
                BahmniObservation dateOfOnset = getObservationFor(observation.getGroupMembers(), dateOfOnsetConceptName);
                BahmniObservation dateOfReport = getObservationFor(observation.getGroupMembers(), dateOfReportConceptName);

                Date onSetDate = (dateOfOnset != null && StringUtils.isNotEmpty(dateOfOnset.getValueAsString())) ? getDate(dateOfOnset.getValue()) : null;
                Date reportDate = (dateOfReport != null && StringUtils.isNotEmpty(dateOfReport.getValueAsString())) ? getDate(dateOfReport.getValue()) : null;

                if (onSetDate!= null && reportDate!= null && onSetDate.after(reportDate)) {
                    throw new RuntimeException("Date of onset " + "(" + clientSideDateFormat.format(onSetDate) + ")" + " should be before Date of report " + "(" + clientSideDateFormat.format(reportDate) + ")" + " on " + templateConceptName);
                }
            }
        }
    }






    private Date getDate(Object value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
        try {
            return simpleDateFormat.parse((String) value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private BahmniObservation getObservationFor(Collection<BahmniObservation> groupMembers, String conceptName) {
        BahmniObservation bahmniObservation = null;
        for (BahmniObservation groupMember : groupMembers) {
            if (groupMember.getConcept().isSet() && groupMember.getGroupMembers() != null) {
                bahmniObservation = getObservationFor(groupMember.getGroupMembers(), conceptName);
            } else if (conceptName.equals(groupMember.getConcept().getName())) {
                bahmniObservation = groupMember;
            }
            if (bahmniObservation != null) {
                return bahmniObservation;
            }
        }
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}
