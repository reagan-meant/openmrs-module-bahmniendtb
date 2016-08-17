package org.openmrs.module.bahmniendtb.dataintegrity.service;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.episodes.Episode;

import java.util.List;

public interface EndTBObsService {
    Obs getObsForEncounter(Encounter encounter, String conceptName);

    List<Obs> getObsForEpisode(Episode episode, String concepTname);

    Obs getChildObsByConcept(Obs parentObs, Concept childConceptName);

    List<Obs> getChildObsByConcepts(Obs parentObs, List<Concept> childConcept);
}
