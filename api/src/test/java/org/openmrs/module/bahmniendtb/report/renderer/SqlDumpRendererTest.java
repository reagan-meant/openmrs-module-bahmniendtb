package org.openmrs.module.bahmniendtb.report.renderer;

import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SqlDumpRendererTest {

	@Test
	public void ensureThatTheSchemaIsGeneratedCorrectly() throws IOException, ParseException {
		ReportData data = new ReportData();
		SimpleDataSet dataSet = new SimpleDataSet(null, null);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 1);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_NAME", "PATIENT_NAME", String.class), "Ram");
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_DOB", "PATIENT_DOB", Date.class), new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"));

		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 2);
		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_NAME", "PATIENT_NAME", String.class), "Shyam");
		dataSet.addColumnValue(1, new DataSetColumn("PATIENT_DOB", "PATIENT_DOB", Date.class), new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-01"));


		SimpleDataSet dataSet2 = new SimpleDataSet(null, null);
		dataSet2.addColumnValue(0, new DataSetColumn("CONCEPT_ID", "CONCEPT_ID", Integer.class), 10);
		dataSet2.addColumnValue(0, new DataSetColumn("CONCEPT_NAME", "CONCEPT_NAME", String.class), "Systolic");

		dataSet2.addColumnValue(1, new DataSetColumn("CONCEPT_ID", "CONCEPT_ID", Integer.class), 20);
		dataSet2.addColumnValue(1, new DataSetColumn("CONCEPT_NAME", "CONCEPT_NAME", String.class), "Diastolic");


		data.getDataSets().put("patient", dataSet);
		data.getDataSets().put("concept", dataSet2);

		SqlDumpRenderer renderer = new SqlDumpRenderer();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		renderer.render(data, "", out);

		System.out.println(out.toString());

		assertEquals("CREATE DATABASE `endtbreports`;USE `endtbreports`;DROP TABLE IF EXISTS `patient`;\n"
				+ "CREATE TABLE `patient` (\n"
				+ " `PATIENT_ID` int(20) DEFAULT NULL, `PATIENT_NAME` text DEFAULT NULL, `PATIENT_DOB` datetime DEFAULT NULL);INSERT INTO `patient` VALUES (1,'Ram','2015-01-01 00:00:00'),(2,'Shyam','2014-01-01 00:00:00');DROP TABLE IF EXISTS `concept`;\n"
				+ "CREATE TABLE `concept` (\n"
				+ " `CONCEPT_ID` int(20) DEFAULT NULL, `CONCEPT_NAME` text DEFAULT NULL);INSERT INTO `concept` VALUES (10,'Systolic'),(20,'Diastolic');",out.toString());

	}

}
