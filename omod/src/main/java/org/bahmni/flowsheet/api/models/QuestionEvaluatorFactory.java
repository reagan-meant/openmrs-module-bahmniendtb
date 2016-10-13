package org.bahmni.flowsheet.api.models;

import org.bahmni.flowsheet.api.Evaluator;
import org.bahmni.flowsheet.api.QuestionType;
import org.bahmni.flowsheet.api.impl.DrugEvaluator;
import org.bahmni.flowsheet.api.impl.ObsEvaluator;
import org.bahmni.module.bahmnicore.dao.ObsDao;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
public class QuestionEvaluatorFactory {

    private List<BahmniDrugOrder> bahmniDrugOrders;
    private List<Obs> obsList;

    private DrugEvaluator drugEvaluator;

    private ObsEvaluator obsEvaluator;

    private BahmniDrugOrderService bahmniDrugOrderService;

    private ObsDao obsDao;

    @Autowired
    public QuestionEvaluatorFactory(BahmniDrugOrderService bahmniDrugOrderService, ObsDao obsDao) {
        this.bahmniDrugOrderService = bahmniDrugOrderService;
        this.obsDao = obsDao;
        this.obsEvaluator = new ObsEvaluator();
        this.drugEvaluator = new DrugEvaluator();
    }

    public void init(PatientProgram patientProgram, Set<Concept> obsConcepts) throws ParseException {
        List<String> conceptNames = new ArrayList<>();
        for (Concept obsConcept : obsConcepts) {
            conceptNames.add(obsConcept.getName().getName());
        }
        obsList = obsDao.getObsByPatientProgramUuidAndConceptNames(patientProgram.getUuid(), conceptNames, null, null, null, null);
        obsEvaluator.setObsList(obsList);

        bahmniDrugOrders = bahmniDrugOrderService.getDrugOrders(patientProgram.getPatient().getUuid(), null, null, null, patientProgram.getUuid());
        drugEvaluator.setBahmniDrugOrders(bahmniDrugOrders);
    }

    public Evaluator getEvaluator(QuestionType type) {
        if (type.equals(QuestionType.DRUG)) {
            return drugEvaluator;
        } else {
            return obsEvaluator;
        }

    }

}
