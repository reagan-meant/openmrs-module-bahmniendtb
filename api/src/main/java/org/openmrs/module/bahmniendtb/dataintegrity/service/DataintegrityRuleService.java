package org.openmrs.module.bahmniendtb.dataintegrity.service;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.dataintegrity.rule.RuleResult;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.openmrs.PatientProgram;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataintegrityRuleService {

    private PatientProgramResultsMapper patientProgramResultsMapper;
    private SessionFactory sessionFactory;

    public DataintegrityRuleService(SessionFactory sessionFactory,
                                    PatientProgramResultsMapper patientProgramResultsMapper) {
        this.sessionFactory = sessionFactory;
        this.patientProgramResultsMapper = patientProgramResultsMapper;
    }

    public List<RuleResult<PatientProgram>> getAllByObsAndDrugs(List<String> drugsList, Map<String, List<String>> codedObs) {

        StringBuilder queryString =
                new StringBuilder("SELECT epp.patient_program_id as entity");

        queryString.append(", obs1.comments  AS notes ");
        queryString.append( ", CONCAT('#/default/patient/',p.uuid, '/dashboard/observation/',obs1.uuid) AS actionUrl ");

        queryString.append(
                " FROM " +
                "    episode_patient_program epp " +
                "    JOIN episode_encounter ee ON ee.episode_id = epp.episode_id" );

        if(drugsList!=null && drugsList.size() > 0)
            queryString.append(
                "    JOIN orders     ON ee.encounter_id = orders.encounter_id AND orders.order_action != 'DISCONTINUE' AND orders.voided = 0 " +
                "    JOIN drug_order ON orders.order_id = drug_order.order_id " +
                "    JOIN drug       ON drug_order.drug_inventory_id = drug.drug_id " +
                "    JOIN concept_name cn ON 	drug.concept_id = cn.concept_id AND cn.concept_name_type = 'FULLY_SPECIFIED' AND drug.retired = 0 AND " +
                "						        cn.name IN (:drugs)" );

        if(codedObs!=null)
            for( Map.Entry<String, List<String>> obsConcept : codedObs.entrySet())
                queryString.append(getObsQuery(obsConcept));

        queryString.append(" GROUP BY epp.patient_program_id");
        Query queryToGetObs = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString());

        if(drugsList!=null && drugsList.size() > 0)
            queryToGetObs.setParameterList("drugs", drugsList);

        List<RuleResult> result = queryToGetObs.setResultTransformer(
                                    new AliasToBeanResultTransformer(RuleResult.class)).list();
        return patientProgramResultsMapper.getPatientProgramResults(result);
    }

    private String getObsQuery(Map.Entry<String, List<String>> obsConcept) {
        String concpetValues = StringUtils.join(obsConcept.getValue(), "','");
        return "   INNER JOIN ( " +
                "   SELECT  o.comments, ee.episode_id, o.uuid  " +
                "   FROM    obs o " +
                "           JOIN concept_view cv ON     o.concept_id = cv.concept_id AND o.voided = 0 AND " +
                "                                       cv.concept_full_name = ' " + obsConcept.getKey() + "' " +
                "           JOIN episode_encounter ee ON   o.encounter_id = ee.encounter_id  " +
                "           JOIN concept_view cv_value ON   o.value_coded = cv_value.concept_id  AND  " +
                "                                           cv_value.concept_full_name IN (' " + concpetValues + "')  " +
                "   ) obs1 ON obs1.episode_id = ee.episode_id"; //TODO: modify for multiple obs
    }

    public List<RuleResult<PatientProgram>> getDrugConsentResults() {

        StringBuilder queryString =
                new StringBuilder(
                        " SELECT " +
                        "   epp.patient_program_id AS entity " +
                        "   , " +
                        "   obs1.comments          AS notes, " +
                        "   CONCAT('#/default/patient/',p.uuid, '/dashboard/observation/',obs1.uuid)                     AS actionUrl " +

                        "  FROM episode_patient_program epp JOIN episode_encounter ee ON ee.episode_id = epp.episode_id " +
                        "   JOIN orders ON ee.encounter_id = orders.encounter_id AND orders.order_action != 'DISCONTINUE' AND orders.voided = 0 " +
                        "   JOIN drug_order ON orders.order_id = drug_order.order_id " +
                        "   JOIN drug ON drug_order.drug_inventory_id = drug.drug_id " +
                        "   JOIN patient_program pp ON epp.patient_program_id = pp.patient_program_id " +
                        "   JOIN person p ON p.person_id = pp.patient_id " +
                        "   JOIN concept_name cn " +
                        "     ON drug.concept_id = cn.concept_id AND cn.concept_name_type = 'FULLY_SPECIFIED' AND drug.retired = 0 AND " +
                        "        cn.name IN ('Bedaquiline', 'Delamanid') " +
                        "   LEFT JOIN (SELECT ee.episode_id " +
                        "                , o1.uuid " +
                        "                ,o3.comments, " +
                        "                cv_value.concept_full_name " +
                        "              from  episode_encounter ee " +
                        "                JOIN obs o1 ON o1.encounter_id = ee.encounter_id AND o1.voided =0 " +
                        "                JOIN concept_view cv1 ON cv1.concept_id = o1.concept_id AND cv1.concept_full_name = 'Treatment Initiation Template' " +
                        "                JOIN concept_view cv2 ON cv2.concept_full_name = 'TI, New treatment eligibility' " +
                        "                JOIN concept_view cv3 ON cv3.concept_full_name = 'TI, Has the Treatment with New Drugs Consent Form been explained and signed' " +
                        "                LEFT JOIN obs o2 ON o2.encounter_id = ee.encounter_id AND  cv2.concept_id = o2.concept_id AND  o2.obs_group_id = o1.obs_id AND o2.voided=0 " +
                        "                LEFT JOIN obs o3 ON o3.encounter_id = ee.encounter_id AND o3.obs_group_id = o2.obs_id  AND o3.voided=0 AND cv3.concept_id = o3.concept_id " +
                        "                LEFT JOIN concept_view cv_value ON o3.value_coded = cv_value.concept_id " +
                        "             ) obs1 ON obs1.episode_id = ee.episode_id " +
                        "  WHERE obs1.concept_full_name IN ('False', 'Unknown') OR obs1.concept_full_name IS NULL " +
                        "  GROUP BY epp.patient_program_id ;");

        Query queryToGetObs = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString());
        List<RuleResult> result = queryToGetObs.setResultTransformer(
                new AliasToBeanResultTransformer(RuleResult.class)).list();
        return patientProgramResultsMapper.getPatientProgramResults(result);
    }
}

