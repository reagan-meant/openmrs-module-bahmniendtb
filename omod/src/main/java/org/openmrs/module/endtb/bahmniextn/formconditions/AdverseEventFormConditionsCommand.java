package org.openmrs.module.endtb.bahmniextn.formconditions;

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
public class AdverseEventFormConditionsCommand implements EncounterDataPreSaveCommand {

    public static final String ADVERSE_EVENTS_TEMPLATE = "Adverse Events Template";

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        BahmniObservation dateOfOnsetObservation = getObservationFromAdverseTemplate(bahmniEncounterTransaction.getObservations(), "AE Form, Date of AE onset");
        BahmniObservation dateOfReportObservation = getObservationFromAdverseTemplate(bahmniEncounterTransaction.getObservations(), "AE Form, Date of AE report");

        Date dateOfOnset = dateOfOnsetObservation != null ? getDate(dateOfOnsetObservation.getValue()) : null;
        Date dateOfReport = dateOfReportObservation != null ? getDate(dateOfReportObservation.getValue()) : null;

        if (dateOfOnset != null && dateOfReport != null && dateOfOnset.after(dateOfReport)) {
            throw new RuntimeException("\"Date of onset\" should be before \"Date of report\" on adverse events form.");
        }
        return bahmniEncounterTransaction;
    }

    private Date getDate(Object value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
        try {
            return simpleDateFormat.parse((String) value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private BahmniObservation getObservationFromAdverseTemplate(Collection<BahmniObservation> observations, String conceptName) {
        for (BahmniObservation observation : observations) {
            if (ADVERSE_EVENTS_TEMPLATE.equals(observation.getConcept().getName())) {
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
