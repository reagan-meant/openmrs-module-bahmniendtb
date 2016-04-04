package org.openmrs.module.bahmniendtb.report.renderer.template;

import java.io.IOException;
import java.io.Writer;

public class DatabaseWriter {
	private String name;
	private Writer writer;

	public DatabaseWriter(String name, Writer writer){
		this.name = name;
		this.writer = writer;
	}

	public void write() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CREATE DATABASE `"+name+"`;");
		stringBuilder.append("USE `"+name+"`;");
		writer.write(stringBuilder.toString());
	}
}
