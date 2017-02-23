package org.openmrs.module.endtb.admin.observation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmnicore.service.BahmniConceptService;
import org.openmrs.Concept;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.ETObsToBahmniObsMapper;
import org.openmrs.module.bahmniemrapi.encountertransaction.mapper.parameters.AdditionalBahmniObservationFields;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.encounter.exception.ConceptNotFoundException;
import org.openmrs.module.endtb.admin.constants.SAETemplateConstants;
import org.openmrs.module.endtb.admin.models.SaeEncounterRow;
import org.openmrs.module.endtb.admin.models.SaeTBDrugTreatmentRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(value = "saeObservationMapper")
public class SaeObservationMapper {

    private BahmniConceptService bahmniConceptService;
    private ETObsToBahmniObsMapper fromETObsToBahmniObs;

    @Autowired
    public SaeObservationMapper(BahmniConceptService bahmniConceptService, ETObsToBahmniObsMapper fromETObsToBahmniObs) {
        this.bahmniConceptService = bahmniConceptService;
        this.fromETObsToBahmniObs = fromETObsToBahmniObs;
    }

    public List<BahmniObservation> update(SaeEncounterRow saeEncounterRow, BahmniObservation bahmniObservation, Date encounterDateTime) throws ParseException {
        convertBahmniObservationsValueFromCodedToString(Arrays.asList(bahmniObservation));
        Map<String, Object> saeTemplateMap = getSaeTemplateMap(saeEncounterRow);
        updateObservation(saeTemplateMap, true, Arrays.asList(bahmniObservation), null, encounterDateTime);
        updateObservationToKeepOnlyCurrentTBDrugSections(saeTemplateMap, bahmniObservation);
        return Arrays.asList(bahmniObservation);
    }

    private void updateObservationToKeepOnlyCurrentTBDrugSections(Map<String, Object> saeTemplateMap, BahmniObservation SAEObservation) {
        BahmniObservation SAEOutcome = filterByConceptName(SAEObservation, SAETemplateConstants.SAE_OUTCOME_PV);
        List<BahmniObservation> SAETbTreatments = SAEOutcome.getGroupMembers()
                .stream()
                .filter(observation -> observation.getConcept().getName().equalsIgnoreCase(SAETemplateConstants.SAE_TB_DRUG_TREATMENT))
                .collect(Collectors.toList());
        Map<String, Object> SAEOutcomePV = (Map<String, Object>)((Map<String, Object>)saeTemplateMap.get(SAETemplateConstants.SAE_TEMPLATE)).get(SAETemplateConstants.SAE_OUTCOME_PV);
        List<String> SAETbTreatmentKeys = SAEOutcomePV.keySet().stream().filter(key -> getKeyWithoutIndex(key).equals(SAETemplateConstants.SAE_TB_DRUG_TREATMENT)).collect(Collectors.toList());

        for (BahmniObservation SAETbTreatmentSectionPV : SAETbTreatments) {
            boolean remove = true;
            BahmniObservation SAETbDrug = filterByConceptName(SAETbTreatmentSectionPV, SAETemplateConstants.SAE_TB_DRUG_NAME);
            for (String key : SAETbTreatmentKeys) {
                String importedDrugName = (String) ((Map<String, Object>) SAEOutcomePV.get(key)).get(SAETemplateConstants.SAE_TB_DRUG_NAME);
                Concept tbDrugConcept = bahmniConceptService.getConceptByFullySpecifiedName(importedDrugName);
                if (tbDrugConcept!= null && tbDrugConcept.getUuid().equals(SAETbDrug.getValueAsString())) {
                    remove = false;
                }
            }
            if (remove) {
                SAETbTreatmentSectionPV.setVoided(true);
                makeObservationsVoided(SAETbTreatmentSectionPV.getGroupMembers());
            }
        }

    }

    private List<EncounterTransaction.Observation> createNewObservation(Map<String, Object> map, Date encounterDate) throws ParseException {
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = getKeyWithoutIndex(entry.getKey());

            EncounterTransaction.Observation observation = createEncounterTransactionObservation(key, encounterDate);
            if (entry.getValue() instanceof String) {
                if (StringUtils.isEmpty(entry.getValue().toString())) continue;
                if (((String)entry.getValue()).contains("|")) {
                    observations.addAll(multiSelectObservation(entry, encounterDate));
                    continue;
                }
                observation.setValue(getValue((String) entry.getValue(), key));
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
        String key = getKeyWithoutIndex(entry.getKey());
        List<EncounterTransaction.Observation> observations = new ArrayList<>();
        for (String split: ((String)entry.getValue()).split("\\|")){
            EncounterTransaction.Observation observation = createEncounterTransactionObservation(key, encounterDate);
            observation.setValue(getValue(split, key));
            observations.add(observation);
        }
        return observations;
    }

    private void updateObservation(Map<String, Object> saeObservationMap, boolean overwrite, Collection<BahmniObservation> bahmniObservations, BahmniObservation parentObs, Date encounterDateTime) throws ParseException {
        for (final Map.Entry<String, Object> entry : saeObservationMap.entrySet()) {
            boolean isObservationPresent = false;
            String key = getKeyWithoutIndex(entry.getKey());

            for (BahmniObservation observation : bahmniObservations) {
                if (observation.getConcept().getName().equals(key) && !observation.getVoided()) {
                    isObservationPresent = true;
                    if (key.equals(SAETemplateConstants.SAE_TB_DRUG_TREATMENT)) {
                        isObservationPresent = checkIfSAEFormTBDrugTreatmentIsAlreadyPresent(observation.getGroupMembers(), (Map<String, Object>)entry.getValue());
                    }
                    else if(key.equals(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS_PV)) {
                        //TODO functionality is not clear. Why are observations voided everytime for otherCasualFactors?
                        makeObservationsVoided(observation.getGroupMembers());
                    }
                    if (overwrite && entry.getValue() instanceof String) {
                        if (StringUtils.isEmpty((String) entry.getValue())) {
                            observation.setVoided(true);
                        } else {
                            observation.setValue(getValue(entry.getValue().toString(), key));
                        }
                    } else if(overwrite){
                        updateObservation((Map<String, Object>) entry.getValue(), isObservationPresent, observation.getGroupMembers(), observation, encounterDateTime);
                    }
                }
            }
            if (!isObservationPresent && !StringUtils.isEmpty(entry.getValue().toString())) {
                addGroupMemberToParentObs(parentObs, encounterDateTime, entry);
            }
        }
    }

    private void makeObservationsVoided(Collection<BahmniObservation> observations) {
        for(BahmniObservation observation : observations) {
            observation.setVoided(true);
            observation.setVoidReason("SAE PV Unit Import");
        }
    }

    private void addGroupMemberToParentObs(BahmniObservation parentObs, Date encounterDateTime, Map.Entry<String, Object> entry) throws ParseException {
        Map<String, Object> observationMap = new HashMap<>();
        String key = getKeyWithoutIndex(entry.getKey());
        observationMap.put(key, entry.getValue());
        List<EncounterTransaction.Observation> observation = createNewObservation(observationMap, encounterDateTime);
        List<BahmniObservation> newlyCreatedObs = fromETObsToBahmniObs.create(observation, new AdditionalBahmniObservationFields(null, encounterDateTime, null, null));
        if (CollectionUtils.isNotEmpty(newlyCreatedObs)) {
            for (BahmniObservation bahmniObservation : newlyCreatedObs) {
                parentObs.addGroupMember(bahmniObservation);
            }
        }
    }

    private String getKeyWithoutIndex(String key) {
        if(key.startsWith(SAETemplateConstants.SAE_TB_DRUG_TREATMENT))
            return SAETemplateConstants.SAE_TB_DRUG_TREATMENT;
        return key;
    }

    private boolean checkIfSAEFormTBDrugTreatmentIsAlreadyPresent(Collection<BahmniObservation> tbDrugTreatmentMembers, Map<String, Object> importedTbDrugTreatment) {
        BahmniObservation tbDrugNameObservation = tbDrugTreatmentMembers
                .stream()
                .filter(observation -> observation.getConcept().getName().equalsIgnoreCase(SAETemplateConstants.SAE_TB_DRUG_NAME))
                .findFirst()
                .get();
        String importedDrugName = (String) importedTbDrugTreatment.get(SAETemplateConstants.SAE_TB_DRUG_NAME);
        Concept tbDrugConcept = bahmniConceptService.getConceptByFullySpecifiedName(importedDrugName);
        return tbDrugConcept != null && tbDrugConcept.getUuid().equals(tbDrugNameObservation.getValue());
    }

    private void convertBahmniObservationsValueFromCodedToString(Collection<BahmniObservation> bahmniObservations) {
        for (BahmniObservation bahmniObservation: bahmniObservations) {
            if(CollectionUtils.isNotEmpty(bahmniObservation.getGroupMembers())) {
                convertBahmniObservationsValueFromCodedToString(bahmniObservation.getGroupMembers());
            }
            if(bahmniObservation.getValue() instanceof EncounterTransaction.Concept) {
                bahmniObservation.setValue(((EncounterTransaction.Concept) bahmniObservation.getValue()).getUuid());
            }
        }
    }

    private EncounterTransaction.Observation createEncounterTransactionObservation(String conceptName, Date encounterDateTime) {
        EncounterTransaction.Observation observation = new EncounterTransaction.Observation();
        Concept obsConcept = bahmniConceptService.getConceptByFullySpecifiedName(conceptName);
        EncounterTransaction.Concept concept = new EncounterTransaction.Concept(obsConcept.getUuid(), obsConcept.getName().getName());
        observation.setConcept(concept);
        observation.setObservationDateTime(encounterDateTime);
        return observation;
    }

    private String getValue(String conceptValue, String conceptName) throws ParseException {
        Concept obsConcept = bahmniConceptService.getConceptByFullySpecifiedName(conceptName);
        if (StringUtils.isNotEmpty(conceptValue) && obsConcept.getDatatype().isCoded()) {
            Concept valueConcept = bahmniConceptService.getConceptByFullySpecifiedName(conceptValue);
            if (valueConcept == null)
                throw new ConceptNotFoundException(conceptValue + " not found");
            return valueConcept.getUuid();
        }
        return conceptValue;
    }

    private Map<String, Object> getSaeTemplateMap(SaeEncounterRow saeEncounterRow) {
        Map<String, Object> saeOutcomePVGroupMembers = new HashMap<>();
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_EVENT_END_DATE, saeEncounterRow.dateOfSaeOutcome);
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_OUTCOME, saeEncounterRow.saeOutcome);
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_MAXIMUM_SEVERITY_GRADE, saeEncounterRow.maxSeverityOfSae);
        saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_RELATED_TO_TB_DRUGS, saeEncounterRow.saeRelatedTbDrug);

        int index = 0;
        for (SaeTBDrugTreatmentRow tbDrugTreatmentRow : saeEncounterRow.saeTBDrugTreatmentRows) {
            Map<String, Object> saeTBDrugTreatmentGroupMembers = new HashMap<>();
            saeTBDrugTreatmentGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_NAME, tbDrugTreatmentRow.tbDrug);
            saeTBDrugTreatmentGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_FINAL_ACTION, tbDrugTreatmentRow.tbDrugFinalAction);
            saeTBDrugTreatmentGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_RELATED, tbDrugTreatmentRow.tbDrugRelated);
            saeOutcomePVGroupMembers.put(SAETemplateConstants.SAE_TB_DRUG_TREATMENT + "." + index++, saeTBDrugTreatmentGroupMembers);
        }

        Map<String, Object> saeOtherCasualFactorsPVGroupMembers = new HashMap<>();
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS_RELATED_TO_SAE, saeEncounterRow.saeOtherCasualFactors);
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_NON_TB_DRUG, saeEncounterRow.nonTBdrug);
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_COMORBIDITY, saeEncounterRow.coMorbidity);
        saeOtherCasualFactorsPVGroupMembers.put(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS, saeEncounterRow.otherCausalFactor);

        Map<String, Object> saeTemplateGroupMembers = new HashMap<>();
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_TERM, saeEncounterRow.saeTerm);
        saeTemplateGroupMembers.put(SAETemplateConstants.OTHER_SAE_TERM, saeEncounterRow.otherSaeTerm);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_CASE_NUMBER, saeEncounterRow.saeCaseNumber);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_EVENT_ONSET_DATE, saeEncounterRow.dateOfSaeOnset);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_REPORT_DATE, saeEncounterRow.dateOfSaeReport);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_OUTCOME_PV, saeOutcomePVGroupMembers);
        saeTemplateGroupMembers.put(SAETemplateConstants.SAE_OTHER_CASUAL_FACTORS_PV, saeOtherCasualFactorsPVGroupMembers);

        Map<String, Object> saeTemplateMap = new HashMap<>();
        saeTemplateMap.put(SAETemplateConstants.SAE_TEMPLATE, saeTemplateGroupMembers);

        return saeTemplateMap;
    }

    private BahmniObservation filterByConceptName(BahmniObservation parentObservation, String conceptName) {
        return parentObservation.getGroupMembers()
                .stream()
                .filter(observation -> observation.getConcept().getName().equalsIgnoreCase(conceptName))
                .findFirst()
                .get();
    }

}
