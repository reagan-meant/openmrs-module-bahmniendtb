package org.openmrs.module.bahmniendtb.dataintegrity.service;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.episodes.Episode;

import java.util.List;

public interface EndTBObsService {
    Obs getTreatmentInitiationObsForEncounter(Encounter encounter);

    List<Obs> getTreamentInitiationObsForEpisode(Episode episode);

    Obs getChildObsByConcept(Obs parentObs, Concept childConceptName);
}
