package org.openmrs.module.bahmniendtb.report.renderer.template;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class DatabaseWriterTest {

	@Test
	public void writeDatabaseCreationString() throws IOException {
		StringWriter writer = new StringWriter();
		DatabaseWriter databaseWriter = new DatabaseWriter("endtb",writer);
		databaseWriter.write();

		assertEquals("CREATE DATABASE `endtb`;USE `endtb`;",writer.toString());
	}

}
