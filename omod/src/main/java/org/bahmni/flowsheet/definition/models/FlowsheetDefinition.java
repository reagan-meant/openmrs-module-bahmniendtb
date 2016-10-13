package org.bahmni.flowsheet.definition.models;

import org.bahmni.flowsheet.api.models.Flowsheet;
import org.bahmni.flowsheet.api.models.Milestone;
import org.openmrs.PatientProgram;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class FlowsheetDefinition {

    private Date startDate;
    private Set<MilestoneDefinition> milestoneDefinitionList;

    public FlowsheetDefinition(Date startDate, Set<MilestoneDefinition> milestoneDefinitionList) {
        this.startDate = startDate;
        this.milestoneDefinitionList = milestoneDefinitionList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Set<MilestoneDefinition> getMilestoneDefinitionList() {
        return milestoneDefinitionList;
    }

    public void setMilestoneDefinitionList(Set<MilestoneDefinition> milestoneDefinitionList) {
        this.milestoneDefinitionList = milestoneDefinitionList;
    }

    public void addMilestoneDefinition(MilestoneDefinition milestoneDefinition) {
        this.milestoneDefinitionList.add(milestoneDefinition);
    }


    public Flowsheet createFlowsheet(PatientProgram patientProgram) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Flowsheet flowsheet = new Flowsheet();
        flowsheet.setStartDate(this.startDate);
        Set<Milestone> milestones = new LinkedHashSet<>();
        for (MilestoneDefinition milestoneDefinition : this.milestoneDefinitionList) {
            Milestone milestone = milestoneDefinition.createMilestone(this.startDate, patientProgram);
            if (milestone != null) {
                milestones.add(milestone);
            }
        }
        flowsheet.setMilestones(milestones);
        return flowsheet;
    }
}
