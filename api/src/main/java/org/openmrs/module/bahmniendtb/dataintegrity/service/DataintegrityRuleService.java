package org.openmrs.module.bahmniendtb.dataintegrity.service;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.module.episodes.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataintegrityRuleService {

    private PatientProgramResultsMapper patientProgramResultsMapper;
    private SessionFactory sessionFactory;

    @Autowired
    public DataintegrityRuleService(SessionFactory sessionFactory,
                                    PatientProgramResultsMapper patientProgramResultsMapper) {
        this.sessionFactory = sessionFactory;
        this.patientProgramResultsMapper = patientProgramResultsMapper;
    }

    public List<Episode> getEpisodeForEncountersWithDrugs(List<Concept> conceptsForDrugs){
        StringBuilder queryString = new StringBuilder("select episode\n" +
                "from Episode as episode\n" +
                "    join episode.encounters as encounter\n" +
                "        join encounter.orders as order\n" +
                "    join episode.patientPrograms as patientProgram\n" +
                "where order.voided = false and order.action != 'DISCONTINUE'");
        if (CollectionUtils.isNotEmpty(conceptsForDrugs)) {
            queryString.append(" and order.concept in :conceptsForDrugs ");
        }
        Query query = sessionFactory.getCurrentSession().createQuery(queryString.toString());

        if (CollectionUtils.isNotEmpty(conceptsForDrugs)) {
            query.setParameterList("conceptsForDrugs", conceptsForDrugs);
        }
        return query.list();
    }

    public Set<Episode> filterEpisodesForObsWithSpecifiedValue(List<Episode> episodes, Concept questionConcept, List<Concept> valueCodedAnswers){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Episode.class, "episodes");
        criteria.createAlias("episodes.encounters", "encounters")
                .createAlias("encounters.obs", "parentObs")
                .add(Restrictions.in("episodes.episodeId", getEpisodeIds(episodes)))
                .add(Restrictions.eq("parentObs.concept", questionConcept))
                .add(Restrictions.eq("parentObs.voided", false))
                .add(Restrictions.not(Restrictions.in("parentObs.valueCoded", valueCodedAnswers)));

        List<Episode> consistentEpisodes = criteria.list();

        episodes.removeAll(consistentEpisodes);
        Set<Episode> filteredEpisodes = new HashSet<>();
        filteredEpisodes.addAll(episodes);
        return filteredEpisodes;
    }

    private List<Integer> getEpisodeIds(List<Episode> episodes){
        List<Integer> episodeIds = new ArrayList<>();
        for (Episode episode : episodes) {
            episodeIds.add(episode.getEpisodeId());
        }

        return episodeIds;
    }
}

