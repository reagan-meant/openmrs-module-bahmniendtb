package org.openmrs.module.endtb.admin.observation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.admin.observation.ConceptCache;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.AdditionalBahmniObservationFields;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.exception.ConceptNotFoundException;
import org.openmrs.module.endtb.admin.constants.SAETemplateConstants;
import org.openmrs.module.endtb.admin.models.SaeEncounterRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component(value = "saeObservationMapper")
public class SaeObservationMapper {

    private final ConceptCache conceptCache;
    private ConceptService conceptService;
    private ETObsToBahmniObsMapper fromETObsToBahmniObs;

    @Autowired
    public SaeObservationMapper(ConceptService conceptService, ETObsToBahmniObsMapper fromETObsToBahmniObs) {
        this.conceptCache = new ConceptCache(conceptService);
        this.conceptService = conceptService;
        this.fromETObsToBahmniObs = fromETObsToBahmniObs;
    }

    public List<BahmniObservation> update(SaeEncounterRow saeEncounterRow, BahmniObservation bahmniObservation, Date encounterDateTime) throws ParseException {
        convertBahmniObservationsValueFromCodedToString(Arrays.asList(bahmniObservation));
        updateObservation(getSaeTemplateMap(saeEncounterRow), Arrays.asList(bahmniObservation), null, encounterDateTime);
        return Arrays.asList(bahmniObservation);
    }

    private List<EncounterTransaction.Observation> createNewObservation(Map<String, Object> map, Date encounterDate) throws ParseException {
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            EncounterTransaction.Observation observation = createEncounterTransactionObservation(entry.getKey(), encounterDate);
            if (entry.getValue() instanceof String) {
                if (StringUtils.isEmpty(entry.getValue().toString())) continue;
                if (((String)entry.getValue()).contains("|")) {
                    observations.addAll(multiSelectObservation(entry, encounterDate));
                    continue;
                }
                observation.setValue(getValue((String) entry.getValue(), entry.getKey()));
            } else {
                List<EncounterTransaction.Observation> obs = createNewObservation((Map<String, Object>) entry.getValue(), encounterDate);
                if (CollectionUtils.isEmpty(obs)) continue;
                observation.setGroupMembers(obs);
            }
            observations.add(observation);
        }
        return observations;
    }

    private List<EncounterTransaction.Observation> multiSelectObservation(Map.Entry<String, Object> entry, Date encounterDate) throws ParseException {
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        for (String split: ((String)entry.getValue()).split("\\|")){
            EncounterTransaction.Observation observation = createEncounterTransactionObservation(entry.getKey(), encounterDate);
            observation.setValue(getValue(split, entry.getKey()));
            observations.add(observation);
        }
        return observations;
    }

    private void updateObservation(Map<String, Object> saeObservationMap, Collection<BahmniObservation> bahmniObservations, BahmniObservation parentObs, Date encounterDateTime) throws ParseException {
        for (final Map.Entry<String, Object> entry : saeObservationMap.entrySet()) {
            boolean isObservationPresent = false;
            for (BahmniObservation observation : bahmniObservations) {
                if (observation.getConcept().getName().equals(entry.getKey()) && !observation.getVoided()) {
                    if (entry.getKey().equals(SAETemplateConstants.SAE_TB_DRUG_TREATMENT)) {
                        isObservationPresent = isObservationPresent || checkIfSAEFormTBDrugTreatmentIsAlreadyPresent(observation.getGroupMembers(), (Map<String, Object>)entry.getValue());
                        continue;
                    }
                    if(entry.getKey().equals(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS_PV)) {
                        makeObservationsVoided(observation.getGroupMembers());
                    }
                    if (entry.getValue() instanceof String) {
                        if (StringUtils.isEmpty((String) entry.getValue())) {
                            observation.setVoided(true);
                        } else {
                            observation.setValue(getValue(entry.getValue().toString(), entry.getKey()));
                        }
                    } else {
                        updateObservation((Map<String, Object>) entry.getValue(), observation.getGroupMembers(), observation, encounterDateTime);
                    }
                    isObservationPresent = true;
                }
            }
            if (!isObservationPresent) {
                addGroupMemberToParentObs(parentObs, encounterDateTime, entry);
            }
        }
    }

    private void makeObservationsVoided(Collection<BahmniObservation> observations) {
        for(BahmniObservation observation : observations) {
            observation.setVoided(true);
        }
    }

    private void addGroupMemberToParentObs(BahmniObservation parentObs, Date encounterDateTime, Map.Entry<String, Object> entry) throws ParseException {
        Map<String, Object> observationMap = new HashMap<>();
        observationMap.put(entry.getKey(), entry.getValue());
        List<EncounterTransaction.Observation> observation = createNewObservation(observationMap, encounterDateTime);
        List<BahmniObservation> newlyCreatedObs = fromETObsToBahmniObs.create(observation, new AdditionalBahmniObservationFields(null, encounterDateTime, null, null));
        if (CollectionUtils.isNotEmpty(newlyCreatedObs)) {
            for (BahmniObservation bahmniObservation : newlyCreatedObs) {
                parentObs.addGroupMember(bahmniObservation);
            }
        }
    }

    private boolean checkIfSAEFormTBDrugTreatmentIsAlreadyPresent(Collection<BahmniObservation> groupMembers, Map<String, Object> tbDrugTreatmentMap) {
        for (BahmniObservation bahmniObservation : groupMembers) {
            String value = (String) tbDrugTreatmentMap.get(bahmniObservation.getConcept().getName());
            if(bahmniObservation.getValue() instanceof EncounterTransaction.Concept) {
                if(!value.equalsIgnoreCase(((EncounterTransaction.Concept) bahmniObservation.getValue()).getName())) {
                    return false;
                }
            } else if (!value.equalsIgnoreCase(bahmniObservation.getValue().toString())) {
                return false;
            }
        }
        return true;
    }

    private void convertBahmniObservationsValueFromCodedToString(Collection<BahmniObservation> bahmniObservations) {
        for (BahmniObservation bahmniObservation: bahmniObservations) {
            if(bahmniObservation.getGroupMembers().size() > 0) {
                convertBahmniObservationsValueFromCodedToString(bahmniObservation.getGroupMembers());
            }
            if(bahmniObservation.getValue() instanceof EncounterTransaction.Concept) {
                bahmniObservation.setValue(((EncounterTransaction.Concept) bahmniObservation.getValue()).getUuid());
            }
        }
    }

    private EncounterTransaction.Observation createEncounterTransactionObservation(String conceptName, Date encounterDateTime) {
        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        Concept obsConcept = conceptCache.getConcept(conceptName);
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept(obsConcept.getUuid(), obsConcept.getName().getName());
        observation.setConcept(concept);
        observation.setObservationDateTime(encounterDateTime);
        return observation;
    }

    private String getValue(String conceptValue, String conceptName) throws ParseException {
        Concept obsConcept = conceptCache.getConcept(conceptName);
        if (obsConcept.getDatatype().isCoded() && conceptValue != null && conceptValue.length() > 0) {
            List<Concept> valueConcepts = conceptService.getConceptsByName(conceptValue);
            Concept valueConcept = null;
            for (Concept concept : valueConcepts) {
                ConceptName name = concept.getFullySpecifiedName(Context.getLocale()) != null ? concept.getFullySpecifiedName(Context.getLocale()) : concept.getName();
                if (name.getName().equalsIgnoreCase(conceptValue)) {
                    valueConcept = concept;
                    break;
                }
            }
            if (valueConcept == null)
                throw new ConceptNotFoundException(conceptValue + " not found");
            return valueConcept.getUuid();
        }
        return conceptValue;
    }

    private Map<String, Object> getSaeTemplateMap(SaeEncounterRow saeEncounterRow) {
        Map<String, Object> saeTemplateMap = new HashMap<>();

        Map<String, Object> saeTBDrugTreatmentGroupMembers = new HashMap<>();
        saeTBDrugTreatmentGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_NAME, saeEncounterRow.tbDrug);
        saeTBDrugTreatmentGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_FINAL_ACTION, saeEncounterRow.tbDrugFinalAction);
        saeTBDrugTreatmentGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_RELATED, saeEncounterRow.tbDrugRelated);

        Map<String, Object> saeOutcomePVGroupMembers = new HashMap<>();
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_EVENT_END_DATE, saeEncounterRow.dateOfSaeOutcome);
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_OUTCOME, saeEncounterRow.saeOutcome);
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_MAXIMUM_SEVERITY_GRADE, saeEncounterRow.maxSeverityOfSae);
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_RELATED_TO_TB_DRUGS, saeEncounterRow.saeRelatedTbDrug);
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_TREATMENT, saeTBDrugTreatmentGroupMembers);

        Map<String, Object> saeOtherCasualFactorsPVGroupMembers = new HashMap<>();
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS_RELATED_TO_SAE, saeEncounterRow.saeOtherCasualFactors);
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_NON_TB_DRUG, saeEncounterRow.nonTBdrug);
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_COMORBIDITY, saeEncounterRow.coMorbidity);
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS, saeEncounterRow.otherCasualFactor);

        Map<String, Object> saeTemplateGroupMembers = new HashMap<>();
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_TERM, saeEncounterRow.saeTerm);
        saeTemplateGroupMembers.put(SAETemplateConstants.OTHER_SAE_TERM, saeEncounterRow.otherSaeTerm);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_CASE_NUMBER, saeEncounterRow.saeCaseNumber);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_EVENT_ONSET_DATE, saeEncounterRow.dateOfSaeOnset);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_REPORT_DATE, saeEncounterRow.dateOfSaeReport);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_OUTCOME_PV, saeOutcomePVGroupMembers);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS_PV, saeOtherCasualFactorsPVGroupMembers);

        saeTemplateMap.put(SAETemplateConstants.SAE_TEMPLATE, saeTemplateGroupMembers);

        return saeTemplateMap;
    }


}
