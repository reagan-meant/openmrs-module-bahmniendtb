package org.openmrs.module.bahmniendtb.dataintegrity.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.module.episodes.Episode;
import org.openmrs.module.episodes.dao.impl.EpisodeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataintegrityRuleService {

    private PatientProgramResultsMapper patientProgramResultsMapper;
    private SessionFactory sessionFactory;
    private ObsService obsService;
    private EpisodeDAO episodeDAO;

    @Autowired
    public DataintegrityRuleService(SessionFactory sessionFactory,
                                    PatientProgramResultsMapper patientProgramResultsMapper,
                                    ObsService obsService, EpisodeDAO episodeDAO) {
        this.sessionFactory = sessionFactory;
        this.patientProgramResultsMapper = patientProgramResultsMapper;
        this.obsService = obsService;
        this.episodeDAO = episodeDAO;
    }

    public List<Episode> getEpisodeForEncountersWithDrugs(List<Concept> conceptsForDrugs) {
        StringBuilder queryString = new StringBuilder("select episode\n" +
                "from Episode as episode\n" +
                "    join episode.patientPrograms as patientProgram\n" +
                "    join episode.encounters as encounter\n" +
                "    join encounter.orders as order\n" +
                "    join episode.patientPrograms as patientProgram\n" +
                "where order.voided = false and order.action != 'DISCONTINUE' and patientProgram.voided = false");
        if (CollectionUtils.isNotEmpty(conceptsForDrugs)) {
            queryString.append(" and order.concept in :conceptsForDrugs ");
        }
        Query query = sessionFactory.getCurrentSession().createQuery(queryString.toString());

        if (CollectionUtils.isNotEmpty(conceptsForDrugs)) {
            query.setParameterList("conceptsForDrugs", conceptsForDrugs);
        }
        return query.list();
    }

    public Set<Episode> filterEpisodesForObsWithSpecifiedValue(List<Episode> episodes, Concept questionConcept, List<Concept> valueCodedAnswers) {
        if(episodes.isEmpty()){
            return new HashSet<Episode>();
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Episode.class, "episodes");
        criteria.createAlias("episodes.patientPrograms", "patientProgram")
                .createAlias("episodes.encounters", "encounters")
                .createAlias("encounters.obs", "parentObs")
                .add(Restrictions.in("episodes.episodeId", getEpisodeIds(episodes)))
                .add(Restrictions.eq("patientProgram.voided", false))
                .add(Restrictions.eq("parentObs.concept", questionConcept))
                .add(Restrictions.eq("parentObs.voided", false))
                .add(Restrictions.not(Restrictions.in("parentObs.valueCoded", valueCodedAnswers)));

        List<Episode> consistentEpisodes = criteria.list();

        episodes.removeAll(consistentEpisodes);
        Set<Episode> filteredEpisodes = new HashSet<>();
        filteredEpisodes.addAll(episodes);
        return filteredEpisodes;
    }

    public Set<Episode> filterEpisodesForCodedObsWithAnswersInList(List<Episode> episodes, Concept questionConcept, List<Concept> valueCodedAnswers) {
        if(episodes.isEmpty()){
            return new HashSet<Episode>();
        }
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Episode.class, "episodes");
        criteria.createAlias("episodes.patientPrograms", "patientProgram")
                .createAlias("episodes.encounters", "encounters")
                .createAlias("encounters.obs", "parentObs")
                .add(Restrictions.in("episodes.episodeId", getEpisodeIds(episodes)))
                .add(Restrictions.eq("parentObs.concept", questionConcept))
                .add(Restrictions.eq("parentObs.voided", false))
                .add(Restrictions.eq("patientProgram.voided", false))
                .add(Restrictions.in("parentObs.valueCoded", valueCodedAnswers));

        List<Episode> consistentEpisodes = criteria.list();

        return new HashSet<>(consistentEpisodes);
    }

    public Set<Episode> getEpisodesWithRequiredObs(List<Concept> questionConcepts) {

        Criteria criteria = sessionFactory.getCurrentSession()
                .createCriteria(Episode.class, "episodes")
                .createAlias("episodes.patientPrograms", "patientProgram")
                .createAlias("episodes.encounters", "encounters")
                .createAlias("encounters.obs", "parentObs")
                .add(Restrictions.in("parentObs.concept", questionConcepts))
                .add(Restrictions.eq("parentObs.voided", false))
                .add(Restrictions.eq("patientProgram.voided", false))
                .add(Restrictions.or(
                        Restrictions.isNotNull("parentObs.valueCoded"),
                        Restrictions.isNotNull("parentObs.valueDatetime")));

        return new HashSet<>(criteria.list());
    }

    public Set<Episode> getUniqueEpisodeForEncountersWithConceptObs(List<Concept> conceptsForObs) {
        StringBuilder queryString = new StringBuilder("select episode\n" +
                "   from Episode as episode\n" +
                "   join episode.patientPrograms as patientProgram\n" +
                "   join episode.encounters as encounter\n" +
                "   join encounter.obs as obs\n" +
                "   where obs.voided = false and patientProgram.voided = false");
        if(CollectionUtils.isNotEmpty(conceptsForObs)) {
            queryString.append("   and obs.concept in :conceptsForObs");
        }
        Query query = sessionFactory.getCurrentSession().createQuery(queryString.toString());

        if(CollectionUtils.isNotEmpty(conceptsForObs)) {
            query.setParameterList("conceptsForObs", conceptsForObs);
        }
        return new HashSet<>(query.list());
    }

    public Set<Episode> getEpisodesWithResistance(List<Episode> episodes, Concept treatmentStartDate, List<Concept> drugConcepts, List<Concept> valueCodedAnswers) {

        if (episodes.isEmpty()) {
            return new HashSet<Episode>();
        }
        StringBuilder queryString = new StringBuilder("SELECT e.episode_id\n" +
                "  FROM episode e JOIN episode_encounter ee ON ee.episode_id = e.episode_id\n" +
                "  JOIN episode_encounter ee_tsd ON ee_tsd.episode_id = e.episode_id\n" +
                "  JOIN episode_patient_program epp ON epp.episode_id = e.episode_id\n" +
                "  JOIN patient_program pp ON pp.patient_program_id = epp.patient_program_id and pp.voided = false\n" +
                "  JOIN obs drugObs ON drugObs.encounter_id = ee.encounter_id and drugObs.voided = false and drugObs.concept_id in (:drugConcepts)\n" +
                "  JOIn concept_view cv ON cv.concept_id = drugObs.value_coded and cv.concept_id in (:valueCodedAnswers)\n" +
                "  JOIN obs treatmentObs ON treatmentObs.encounter_id = ee_tsd.encounter_id and treatmentObs.voided = false and treatmentObs.concept_id = :treatmentStartDate\n" +
                "  where drugObs.obs_datetime <= DATE_ADD(treatmentObs.value_datetime, INTERVAL 30.5 DAY) and e.episode_id in (:episodes)");


        Query queryToGetObs = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString());
        queryToGetObs.setParameterList("drugConcepts", getConceptIds(drugConcepts));
        queryToGetObs.setParameterList("episodes", getEpisodeIds(episodes));
        queryToGetObs.setParameterList("valueCodedAnswers", getConceptIds(valueCodedAnswers));
        queryToGetObs.setParameter("treatmentStartDate", treatmentStartDate.getConceptId());

        List<Integer>  episodeIdList = queryToGetObs.list();
        List<Episode>  resultList = new ArrayList<>();
        for (Integer episodeId : episodeIdList) {
            resultList.add(episodeDAO.get(episodeId));
        }

        return new HashSet<>(resultList);
    }

    private List<Integer> getConceptIds(List<Concept> drugConcepts) {
        List<Integer> conceptIds = new ArrayList<>();
        for (Concept drugConcept : drugConcepts) {
            conceptIds.add(drugConcept.getConceptId());
        }
        return conceptIds;
    }

    public List<Obs> getObsListForAPatient(Person whom, List<Encounter> encounters, List<Concept> questions) {
        return obsService.getObservations(Arrays.asList(whom), encounters, questions, null, null, null, null, null, null, null, null, false);
    }

    private List<Integer> getEpisodeIds(List<Episode> episodes) {
        List<Integer> episodeIds = new ArrayList<>();
        for (Episode episode : episodes) {
            episodeIds.add(episode.getEpisodeId());
        }

        return episodeIds;
    }
}

