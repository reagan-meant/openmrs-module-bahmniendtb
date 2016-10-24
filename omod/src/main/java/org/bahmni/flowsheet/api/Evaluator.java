package org.bahmni.flowsheet.api;


import org.bahmni.flowsheet.api.models.Result;
import org.openmrs.Concept;

import java.util.Date;
import java.util.Set;

public interface Evaluator {
    Result evaluate(Set<Concept> concepts, Date startDate, Date endDate);
}
