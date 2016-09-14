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

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        if (isDateOfOnsetAfterDateOfReport(bahmniEncounterTransaction.getObservations(), ADVERSE_EVENTS_TEMPLATE, ADVERSE_EVENTS_DATE_OF_ONSET, ADVERSE_EVENTS_DATE_OF_REPORT)) {
            throw new RuntimeException("\"Date of onset\" should be before \"Date of report\" on adverse events form.");
        }
        if (isDateOfOnsetAfterDateOfReport(bahmniEncounterTransaction.getObservations(), SERIOUS_ADVERSE_EVENTS_TEMPLATE, SERIOUS_ADVERSE_EVENTS_DATE_OF_ONSET, SERIOUS_ADVERSE_EVENTS_DATE_OF_REPORT)) {
            throw new RuntimeException("\"Date of onset\" should be before \"Date of report\" on serious adverse events form.");
        }
        return bahmniEncounterTransaction;
    }

    private boolean isDateOfOnsetAfterDateOfReport(Collection<BahmniObservation> observations ,String templateConceptName, String dateOfOnsetConceptName, String dateOfReportConceptName) {
        BahmniObservation dateOfOnset = getObservationFromTemplate(observations, templateConceptName, dateOfOnsetConceptName);
        BahmniObservation dateOfReport = getObservationFromTemplate(observations, templateConceptName, dateOfReportConceptName);

        Date onsetDate = (dateOfOnset != null && StringUtils.isNotEmpty(dateOfOnset.getValueAsString())) ? getDate(dateOfOnset.getValue()) : null;
        Date reportDate = (dateOfReport != null && StringUtils.isNotEmpty(dateOfReport.getValueAsString())) ? getDate(dateOfReport.getValue()) : null;

        return dateOfOnset != null && dateOfReport != null && onsetDate!= null && reportDate!= null && onsetDate.after(reportDate);
    }

    private Date getDate(Object value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
        try {
            return simpleDateFormat.parse((String) value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private BahmniObservation getObservationFromTemplate(Collection<BahmniObservation> observations, String templateConceptName, String conceptName) {
        for (BahmniObservation observation : observations) {
            if (templateConceptName.equals(observation.getConcept().getName())) {
                return getObservationFor(observation.getGroupMembers(), conceptName);
            }
        }
        return null;
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
