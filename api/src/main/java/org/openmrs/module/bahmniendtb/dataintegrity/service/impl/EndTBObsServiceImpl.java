package org.openmrs.module.bahmniendtb.dataintegrity.service.impl;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.bahmniendtb.EndTBConstants.*;

@Component
public class EndTBObsServiceImpl implements EndTBObsService {

    @Override
    public Obs getObsForEncounter(Encounter encounter, String conceptName) {
        Set<Obs> obsList = encounter.getObsAtTopLevel(false);
        for (Obs obs : obsList) {
            if (obs.getConcept().getName().getName().equals(conceptName)) {
                return obs;
            }
        }
        return null;
    }

    @Override
    public List<Obs> getObsForEpisode(Episode episode, String conceptName) {
        List<Obs> obsList = new ArrayList<>();
        for (Encounter encounter : episode.getEncounters()) {
            Obs treatmentInitiationObsForEncounter = getObsForEncounter(encounter, conceptName);
            if (treatmentInitiationObsForEncounter != null) {
                obsList.add(treatmentInitiationObsForEncounter);
            }
        }
        return obsList;
    }

    @Override
    public Obs getChildObsByConcept(Obs parentObs, Concept childConcept) {
        Set<Obs> groupMembers = parentObs.getGroupMembers();
        if (groupMembers == null) return null;
        for (Obs obs : groupMembers) {
            if (obs.getConcept().equals(childConcept)) {
                return obs;
            }
            Obs childObs = getChildObsByConcept(obs, childConcept);
            if (childObs != null) {
                return childObs;
            }
        }
        return null;
    }

    @Override
    public List<Obs> getChildObsByConcepts(Obs parentObs, List<Concept> childConcepts) {
        List<Obs> childObs = new ArrayList<>();
        Set<Obs> groupMembers = parentObs.getGroupMembers();

        if (groupMembers == null) return childObs;

        for (Obs obs : groupMembers) {
            if (childConcepts.contains(obs.getConcept())) {
                childObs.add(obs);
                continue;
            }
            childObs.addAll(getChildObsByConcepts(obs, childConcepts));
        }
        return childObs;
    }

}
