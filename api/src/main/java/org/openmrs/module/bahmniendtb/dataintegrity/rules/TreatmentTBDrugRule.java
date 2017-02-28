package org.openmrs.module.bahmniendtb.dataintegrity.rules;


import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.dataintegrity.rules.helper.EpisodeHelper;
import org.openmrs.module.dataintegrity.DataIntegrityRule;
import org.openmrs.module.dataintegrity.rule.RuleDefinition;
import org.openmrs.module.dataintegrity.rule.RuleResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.bahmniendtb.EndTBConstants.ALL_TB_DRUG;
import static org.openmrs.module.bahmniendtb.EndTBConstants.FALSE;
import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

public class TreatmentTBDrugRule implements RuleDefinition<PatientProgram> {

    private ConceptService conceptService;

    private EpisodeHelper episodeHelper;

    public TreatmentTBDrugRule() {
        conceptService = Context.getConceptService();
        episodeHelper = Context.getRegisteredComponent("episodeHelper", EpisodeHelper.class);
    }

    public TreatmentTBDrugRule(EpisodeHelper episodeHelper, ConceptService conceptService) {
        this.episodeHelper = episodeHelper;
        this.conceptService = conceptService;
    }

    @Override
    public List<RuleResult<PatientProgram>> evaluate() {
        List<Concept> unacceptableConsentResponses = new ArrayList<>();
        addConceptByNameToList(Arrays.asList(FALSE, UNKNOWN), unacceptableConsentResponses);
        List<Concept> allTbDrugConcepts = conceptService.getConcept(ALL_TB_DRUG).getSetMembers();
        return episodeHelper.getAllEpisodeWithDrugOrder(allTbDrugConcepts);
    }

    @Override
    public DataIntegrityRule getRule() {
        return null;
    }


    private void addConceptByNameToList(List<String> conceptNames, List<Concept> listToAdd) {
        for (String concept : conceptNames) {
            listToAdd.add(conceptService.getConceptByName(concept));
        }
    }
}
