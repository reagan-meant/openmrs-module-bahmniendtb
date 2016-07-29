package org.openmrs.module.endtb.admin.encounter;

import org.apache.commons.collections.CollectionUtils;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.openmrs.Patient;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.AdditionalBahmniObservationFields;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.endtb.admin.constants.SAETemplateConstants;
import org.openmrs.module.endtb.admin.models.SaeEncounterRow;
import org.openmrs.module.endtb.admin.observation.SaeObservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
public class BahmniSaeEncounterTransactionImportService {

    private SaeObservationMapper saeObservationMapper;
    private ETObsToBahmniObsMapper fromETObsToBahmniObs;
    private BahmniObsService bahmniObsService;

    @Autowired
    public BahmniSaeEncounterTransactionImportService(SaeObservationMapper saeObservationMapper, ETObsToBahmniObsMapper fromETObsToBahmniObs, BahmniObsService bahmniObsService) {
        this.saeObservationMapper = saeObservationMapper;
        this.fromETObsToBahmniObs = fromETObsToBahmniObs;
        this.bahmniObsService = bahmniObsService;
    }

    public BahmniEncounterTransaction getSaeEncounterTransaction(SaeEncounterRow saeEncounterRow, Patient patient, String patientProgramUuid) throws ParseException {
        if (saeEncounterRow == null) {
            return null;
        }
        Date encounterDateTime = null;
        BahmniEncounterTransaction bahmniEncounterTransaction = new BahmniEncounterTransaction();
        bahmniEncounterTransaction.setPatientUuid(patient.getUuid());
        BahmniObservation bahmniObservation = getBahmniObservation(saeEncounterRow, patientProgramUuid);
        if (bahmniObservation != null) {
            bahmniEncounterTransaction.setObservations(saeObservationMapper.update(saeEncounterRow, bahmniObservation, encounterDateTime));
            bahmniEncounterTransaction.setEncounterUuid(bahmniEncounterTransaction.getObservations().iterator().next().getEncounterUuid());
        } else {
            List<EncounterTransaction.Observation> allObservations = saeObservationMapper.create(saeEncounterRow, encounterDateTime);
            bahmniEncounterTransaction.setObservations(fromETObsToBahmniObs.create(allObservations, new AdditionalBahmniObservationFields(null, encounterDateTime, null, null)));
        }
        return bahmniEncounterTransaction;
    }

    private BahmniObservation getBahmniObservation(SaeEncounterRow saeEncounterRow, String patientProgramUuid) {
        BahmniObservation observation = null;
        Collection<BahmniObservation> bahmniObservations = bahmniObsService.getObservationsForPatientProgram(patientProgramUuid, Arrays.asList(SAETemplateConstants.SAE_TEMPLATE));
        if (CollectionUtils.isNotEmpty(bahmniObservations)) {
            for (BahmniObservation bahmniObservation : bahmniObservations) {
                Map<String, Object> groupMembers = getGroupMembersOfConceptNames(bahmniObservation);
                if (groupMembers.get(SAETemplateConstants.SAE_TERM) != null
                        && ((EncounterTransaction.Concept) groupMembers.get(SAETemplateConstants.SAE_TERM)).getName().equalsIgnoreCase(saeEncounterRow.saeTerm)
                        && groupMembers.get(SAETemplateConstants.SAE_EVENT_ONSET_DATE) != null
                        && groupMembers.get(SAETemplateConstants.SAE_EVENT_ONSET_DATE).equals(saeEncounterRow.dateOfSaeOnset)) {
                    observation = bahmniObservation;
                    break;
                }
            }
        }
        return observation;
    }

    private Map<String, Object> getGroupMembersOfConceptNames(BahmniObservation observation) {
        Map<String, Object> groupMembers = new HashMap<>();
        for (BahmniObservation groupMember : observation.getGroupMembers()) {
            if (groupMember.getConcept().getName().equals(SAETemplateConstants.SAE_TERM) || groupMember.getConcept().getName().equals(SAETemplateConstants.SAE_EVENT_ONSET_DATE)) {
                groupMembers.put(groupMember.getConcept().getName(), groupMember.getValue());
            }
        }
        return groupMembers;
    }
}
