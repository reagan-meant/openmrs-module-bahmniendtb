package org.bahmni.flowsheet.api.impl;

import org.bahmni.flowsheet.api.Evaluator;
import org.bahmni.flowsheet.api.Status;
import org.bahmni.flowsheet.api.models.Result;
import org.openmrs.Concept;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class DrugEvaluator implements Evaluator {


    List<BahmniDrugOrder> bahmniDrugOrders = new ArrayList<>();

    public void setBahmniDrugOrders(List<BahmniDrugOrder> bahmniDrugOrders) {
        if (bahmniDrugOrders != null) {
            this.bahmniDrugOrders = bahmniDrugOrders;
        }
    }

    public List<BahmniDrugOrder> getBahmniDrugOrders() {
        return bahmniDrugOrders;
    }

    @Override
    public Result evaluate(Set<Concept> concepts, Date startDate, Date endDate) {

        try {
            for (Concept concept : concepts) {
                Result result = getResultForDrug(startDate, endDate, concept);
                if (!result.getStatus().equals(Status.DATA_ADDED)) {
                    return result;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return new Result(Status.DATA_ADDED);
    }

    private Result getResultForDrug(Date startDate, Date endDate, Concept concept) throws ParseException {
        List<BahmniDrugOrder> bahmniDrugOrders = getDrugOrdersForConcept(concept);
        if (bahmniDrugOrders != null) {
            for (BahmniDrugOrder drug : bahmniDrugOrders) {
                Date drugStartDate = drug.getEffectiveStartDate();
                Date drugStopDate = drug.getEffectiveStopDate() != null ? drug.getEffectiveStopDate() : new Date();
                if ((startDate.before(drugStopDate) || startDate.equals(drugStopDate)) &&
                        (endDate.after(drugStartDate) || endDate.equals(drugStartDate))) {
                    return new Result(Status.DATA_ADDED);
                }
            }
        }
        if (endDate.before(new Date())) {
            return new Result(Status.PENDING);
        }
        return new Result(Status.PLANNED);
    }


    private List<BahmniDrugOrder> getDrugOrdersForConcept(Concept concept) {
        List<BahmniDrugOrder> bahmniDrugOrdersForConcept = new ArrayList<>();
        for (BahmniDrugOrder bahmniDrugOrder : bahmniDrugOrders) {
            if (bahmniDrugOrder.getConcept().getName().equals(concept.getName().getName())) {
                bahmniDrugOrdersForConcept.add(bahmniDrugOrder);
            }
        }
        return bahmniDrugOrdersForConcept;
    }
}
