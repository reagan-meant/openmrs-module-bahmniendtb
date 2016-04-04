package org.openmrs.module.bahmniendtb.report.renderer.template;

import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DatabaseTableWriterTest {

	@Test
	public void ensureTableDefinitionIsCreated() throws IOException {
		StringWriter writer = new StringWriter();

		SimpleDataSet dataSet = new SimpleDataSet(null, null);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 2);
		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_NAME", "PATIENT_NAME", String.class), "Adam");

		DatabaseTableWriter databaseTableWriter = new DatabaseTableWriter("drugorders",dataSet,writer);
		databaseTableWriter.write();
		assertEquals("DROP TABLE IF EXISTS `drugorders`;\n" + "CREATE TABLE `drugorders` (\n"
				+ " `PATIENT_ID` int(20) DEFAULT NULL, `PATIENT_NAME` text DEFAULT NULL);",writer.toString());
	}

	@Test
	public void ensureTableDefinitionIsCreatedWithDateDatatype() throws IOException {
		StringWriter writer = new StringWriter();

		SimpleDataSet dataSet = new SimpleDataSet(null, null);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 2);
		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_NAME", "PATIENT_NAME", String.class), "Adam");
		dataSet.addColumnValue(3, new DataSetColumn("PATIENT_DOB", "DATE_OF_BIRTH", Date.class), "2014/01/01");

		DatabaseTableWriter databaseTableWriter = new DatabaseTableWriter("drugorders",dataSet,writer);
		databaseTableWriter.write();
		assertEquals("DROP TABLE IF EXISTS `drugorders`;\n" + "CREATE TABLE `drugorders` (\n"
				+ " `PATIENT_ID` int(20) DEFAULT NULL, `PATIENT_NAME` text DEFAULT NULL, `PATIENT_DOB` datetime DEFAULT NULL);",writer.toString());
	}

	@Test
	public void ensureTableRowIsInserted() throws IOException, ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		StringWriter writer = new StringWriter();

		SimpleDataSet dataSet = new SimpleDataSet(null, null);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 2);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_NAME", "PATIENT_NAME", String.class), "Adam");
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_DOB", "DATE_OF_BIRTH", Date.class), simpleDateFormat.parse("2013-12-27"));

		DatabaseTableWriter databaseTableWriter = new DatabaseTableWriter("drugorders",dataSet,writer);
		databaseTableWriter.writeData();

		assertEquals("INSERT INTO `drugorders` VALUES (2,'Adam','2013-12-27 00:00:00');",writer.toString());

	}

	@Test
	public void ensureMultipleTableRowsAreInserted() throws IOException, ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		StringWriter writer = new StringWriter();

		SimpleDataSet dataSet = new SimpleDataSet(null, null);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 2);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_NAME", "PATIENT_NAME", String.class), "Adam");
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_DOB", "DATE_OF_BIRTH", Date.class), simpleDateFormat.parse("2013-12-27"));

		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 3);
		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_NAME", "PATIENT_NAME", String.class), "Eve");
		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_DOB", "DATE_OF_BIRTH", Date.class), simpleDateFormat.parse("2013-12-31"));


		DatabaseTableWriter databaseTableWriter = new DatabaseTableWriter("drugorders",dataSet,writer);
		databaseTableWriter.writeData();

		assertEquals("INSERT INTO `drugorders` VALUES (2,'Adam','2013-12-27 00:00:00'),(3,'Eve','2013-12-31 00:00:00');",writer.toString());

	}

}
