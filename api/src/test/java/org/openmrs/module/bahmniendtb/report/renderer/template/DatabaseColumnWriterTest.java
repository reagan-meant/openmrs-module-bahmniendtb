package org.openmrs.module.bahmniendtb.report.renderer.template;

import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSetColumn;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DatabaseColumnWriterTest {

	@Test
	public void ensureStringColumnsOutputProperSqlDatatype() throws IOException {
		StringWriter writer = new StringWriter();

		DataSetColumn column = new DataSetColumn();
		column.setName("name");
		column.setDataType(String.class);

		DatabaseColumnWriter databaseColumnWriter = new DatabaseColumnWriter(column,writer);
		databaseColumnWriter.write();

		assertEquals(" `name` text DEFAULT NULL",writer.toString());
	}

	@Test
	public void ensureIntegerColumnsOutputProperSqlDatatype() throws IOException {
		StringWriter writer = new StringWriter();

		DataSetColumn column = new DataSetColumn();
		column.setName("name");
		column.setDataType(Integer.class);

		DatabaseColumnWriter databaseColumnWriter = new DatabaseColumnWriter(column,writer);
		databaseColumnWriter.write();

		assertEquals(" `name` int(20) DEFAULT NULL",writer.toString());
	}

	@Test
	public void ensureDateColumnsOutputProperSqlDatatype() throws IOException {
		StringWriter writer = new StringWriter();

		DataSetColumn column = new DataSetColumn();
		column.setName("name");
		column.setDataType(Date.class);

		DatabaseColumnWriter databaseColumnWriter = new DatabaseColumnWriter(column,writer);
		databaseColumnWriter.write();

		assertEquals(" `name` datetime DEFAULT NULL",writer.toString());
	}

	@Test
	public void ensureDataIsWrittenInProperType() throws Exception {
		StringWriter writer = new StringWriter();

		String date = "2013-12-27";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		DataSetColumn column = new DataSetColumn();
		column.setName("DateOfBirth");
		column.setDataType(Date.class);

		DatabaseColumnWriter databaseColumnWriter = new DatabaseColumnWriter(column,writer);
		databaseColumnWriter.writeInsert(format.parse(date));

		assertEquals("'2013-12-27 00:00:00'",writer.toString());
	}

	@Test
	public void ensureDataIsWrittenForNullValue() throws Exception {
		StringWriter writer = new StringWriter();

		DatabaseColumnWriter databaseColumnWriter = new DatabaseColumnWriter(null,writer);
		databaseColumnWriter.writeInsert(null);

		assertEquals("null",writer.toString());
	}

	@Test
	public void ensureColumnWithStringDataTypeIsFormattedCorrectly() throws Exception {
		StringWriter writer = new StringWriter();

		DataSetColumn column = new DataSetColumn();
		column.setName("PatientName");
		column.setDataType(String.class);

		DatabaseColumnWriter databaseColumnWriter = new DatabaseColumnWriter(column,writer);
		databaseColumnWriter.writeInsert("HelloWorld");

		assertEquals("'HelloWorld'",writer.toString());
	}

	@Test
	public void ensureColumnWithIntegerDataTypeIsFormattedCorrectly() throws Exception {
		StringWriter writer = new StringWriter();

		DataSetColumn column = new DataSetColumn();
		column.setName("PatientName");
		column.setDataType(Integer.class);

		DatabaseColumnWriter databaseColumnWriter = new DatabaseColumnWriter(column,writer);
		databaseColumnWriter.writeInsert("100");

		assertEquals("100",writer.toString());
	}
}
