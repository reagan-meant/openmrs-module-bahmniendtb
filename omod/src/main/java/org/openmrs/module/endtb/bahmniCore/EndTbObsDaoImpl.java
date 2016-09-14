package org.openmrs.module.endtb.bahmniCore;


import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Delete this class when we upgrade bahmni-core-0.85
 * and use getObsByPatientProgramUuidAndConceptNames() method of ObsDao.class
 */
@Repository
public class EndTbObsDaoImpl {

    @Autowired
    private SessionFactory sessionFactory;

    public enum OrderBy {ASC, DESC}

    public List<Obs> getObsByPatientProgramUuidAndConceptNames(String patientProgramUuid, List<String> conceptNames, Integer limit, OrderBy sortOrder, Date startDate, Date endDate) {
        StringBuilder queryString = new StringBuilder("SELECT o.* " +
                "FROM patient_program pp " +
                "INNER JOIN episode_patient_program epp " +
                "ON pp.patient_program_id = epp.patient_program_id\n " +
                "INNER JOIN episode_encounter ee " +
                "ON epp.episode_id = ee.episode_id\n " +
                "INNER JOIN obs o " +
                "ON o.encounter_id = ee.encounter_id\n " +
                "INNER JOIN concept_name cn on o.concept_id = cn.concept_id\n " +
                "WHERE pp.uuid = (:patientProgramUuid) " +
                "AND o.voided = false " +
                "AND cn.concept_name_type='FULLY_SPECIFIED' " +
                "AND cn.name IN (:conceptNames)");
        if(null != startDate) {
            queryString.append(" AND o.obs_datetime >= STR_TO_DATE(:startDate, '%Y-%m-%d')");
        }
        if(null != endDate) {
            queryString.append(" AND o.obs_datetime <= STR_TO_DATE(:endDate, '%Y-%m-%d')");
        }
        if (sortOrder == OrderBy.ASC) {
            queryString.append(" ORDER by o.obs_datetime asc");
        } else {
            queryString.append(" ORDER by o.obs_datetime desc");
        }
        if (limit != null) {
            queryString.append(" limit " + limit);
        }
        Query queryToGetObs = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString()).addEntity(Obs.class);
        queryToGetObs.setParameterList("conceptNames", conceptNames);
        queryToGetObs.setString("patientProgramUuid", patientProgramUuid);
        if(null != startDate) {
            queryToGetObs.setString("startDate", startDate.toString());
        }
        if(null != endDate) {
            queryToGetObs.setString("endDate", endDate.toString());
        }

        return queryToGetObs.list();
    }
}
