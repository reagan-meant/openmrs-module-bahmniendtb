package org.openmrs.module.bahmniendtb.test;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Produces the endtbMetadata.xml test dataset.  This should be run as follows:
 * 1. Using an existing endTB database as the starting point (ideally a freshly created one)
 * 2. Point your runtime properties file at this database
 * 3. Specify a location where you want the dataset to be written (leaving this blank will skip the execution of this)
 * 4. Run this as a unit test
 * This should produce a new version of endtbMetadata.xml at the location you have specified,
 * and you can copy it into the resources folder if and as appropriate
 */
public class CreateEndTBMetadata extends BaseModuleContextSensitiveTest {

	public String getOutputDirectory() {
		return "src/test/resources";
	}

    @Override
    public Properties getRuntimeProperties() {
	    Properties p = super.getRuntimeProperties();
	    p.setProperty("connection.username", "root");
        p.setProperty("connection.password", "root");
        p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_endtbtest?autoReconnect=true&sessionVariables=storage_engine=InnoDB&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        return p;
    }

    @Test
	public void run() throws Exception {

		// only run this test if it is being run alone and if an output directory has been specified
		if (getLoadCount() != 1 || ObjectUtil.isNull(getOutputDirectory()))
			return;
		
		// database connection for dbunit
		IDatabaseConnection connection = new DatabaseConnection(getConnection());

		// Starting database export
		QueryDataSet initialDataSet = new QueryDataSet(connection);

		for (String table : getAllMetadataTables()) {
		    String query = "select * from " + table;
            System.out.println("Adding table: " + table + " -> " + query);
		    initialDataSet.addTable(table, query);
        }

		File outputFile = new File(getOutputDirectory(), "endtbMetadata.xml");
		System.out.println("Writing xml file to: " + outputFile.getAbsolutePath());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FlatXmlDataSet.write(initialDataSet, baos);
		String contents = baos.toString("UTF-8");
		FileWriter writer = new FileWriter(outputFile);

		for (String line : contents.split(System.getProperty("line.separator"))) {
            if (line.contains("<concept ")) {
                line = line.replaceAll("short_name=\"[\\w\\s]*\"", "");  // concept.short_name still exists in DB but will fail since it is not in hibernate xml
                line = line.replaceAll("description=\"[\\w\\s]*\"", ""); // concept.description still exists in DB but will fail since it is not in hibernate xml
            }
            if (line.contains("<users ")) {
                line = line.replaceAll("password=\"[\\w\\s]*\"", "password=\"4a1750c8607d0fa237de36c6305715c223415189\"");
                line = line.replaceAll("salt=\"[\\w\\s]*\"", "salt=\"c788c6ad82a157b712392ca695dfcf2eed193d7f\"");
            }
			writer.write(line + System.getProperty("line.separator"));
		}

		writer.flush();
		writer.close();
	}

    // TODO: Currently getting non core tables not found error when trying to import this.  Need in investigate why more and uncomment below once fixed
	public static List<String> getAllMetadataTables() {
	    return Arrays.asList(
	            "active_list_type",
                "care_setting",
                "concept",
                "concept_answer",
                "concept_class",
                "concept_complex",
                "concept_datatype",
                "concept_description",
                "concept_map_type",
                "concept_name",
                "concept_numeric",
                "concept_reference_map",
                "concept_reference_source",
                "concept_reference_term",
                "concept_set",
                "concept_stop_word",
                "dataintegrity_rule",
                "drug",
                "encounter_type",
                "entity_mapping",
                "entity_mapping_type",
                "field_type",
                "global_property",
                "location",
                "location_attribute",
                "location_tag",
                "location_tag_map",
                "order_frequency",
                "order_type",
                "order_type_class_map",
                "patient_identifier_type",
                "person",
                "person_attribute_type",
                "person_name",
                "privilege",
                "program",
                "program_attribute_type",
                "program_workflow",
                "program_workflow_state",
                "provider",
                "role",
                "role_privilege",
                "role_role",
                "user_role",
                "users",
                "visit_attribute_type",
                "visit_type"
        );
    }
	
	/**
	 * Make sure we use the database defined by the runtime properties and not the hsql in-memory
	 * database
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
