package org.bahmni.flowsheet.api.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.flowsheet.api.Evaluator;
import org.bahmni.flowsheet.api.Status;
import org.bahmni.flowsheet.api.models.Result;
import org.openmrs.Concept;
import org.openmrs.Obs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class ObsEvaluator implements Evaluator {

    List<Obs> obsList = new ArrayList<>();

    public void setObsList(List<Obs> obsList) {
        if (obsList != null) {
            this.obsList = obsList;
        }
    }

    public List<Obs> getObsList() {
        return obsList;
    }

    public Result evaluate(Set<Concept> conceptSet, Date startDate, Date endDate) {
        for (Concept concept : conceptSet) {
            Result result = getResultForConcept(startDate, endDate, concept);
            if (!result.getStatus().equals(Status.DATA_ADDED)) {
                return result;
            }
        }
        return new Result(Status.DATA_ADDED);
    }

    private Result getResultForConcept(Date startDate, Date endDate, Concept concept) {
        List<Obs> obsList = getObsForConcept(concept);
        for (Obs obs : obsList) {

            Date obsDate = obs.getObsDatetime();
            if (((obsDate.after(startDate)) || DateUtils.isSameDay(obsDate, startDate))
                    && ((obsDate.before(endDate)) || DateUtils.isSameDay(obsDate, endDate))) {
                return new Result(Status.DATA_ADDED);
            }
        }
        if (endDate.before(new Date())) {
            return new Result(Status.PENDING);
        }
        return new Result(Status.PLANNED);
    }

    private List<Obs> getObsForConcept(Concept concept) {
        List<Obs> obsForConcept = new ArrayList<>();
        for (Obs obs : obsList) {
            if (obs.getConcept().equals(concept)) {
                obsForConcept.add(obs);
            }
        }
        return obsForConcept;
    }
}
