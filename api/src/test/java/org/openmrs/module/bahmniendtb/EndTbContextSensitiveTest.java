package org.openmrs.module.bahmniendtb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.bahmniendtb.dataintegrity.service.EndTBObsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.PreparedStatement;
import java.util.List;

@SkipBaseSetup
@Ignore
public class EndTbContextSensitiveTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "endtbMetadata.xml";

    // Wire in services for convenience for subclasses
    @Autowired ConceptService conceptService;
    @Autowired ProgramWorkflowService programWorkflowService;
    @Autowired EndTBObsService endTBObsService;

    @Before
    public void setup() throws Exception {
        setReferencialIntegrityChecks(false);
        executeDataSet(XML_DATASET_PATH);
        getConnection().commit();
        setReferencialIntegrityChecks(true);
        updateSearchIndex();
        authenticate();
    }

    @Test
    public void testMetadataAndServicesAreAvailable() throws Exception {

        List<Concept> allConcepts = conceptService.getAllConcepts();
        Assert.assertTrue(allConcepts.size() > 1000);
        System.out.println("Found " + allConcepts.size() + " concepts");

        List<Program> allPrograms = programWorkflowService.getAllPrograms();
        Assert.assertTrue(allPrograms.size() > 1);
        System.out.println("Found " + allPrograms.size() + " programs");
    }

    protected void setReferencialIntegrityChecks(boolean setting) throws Exception {
        PreparedStatement ps = getConnection().prepareStatement("SET REFERENTIAL_INTEGRITY " + (setting ? "TRUE" : "FALSE"));
        ps.execute();
        ps.close();
    }
}