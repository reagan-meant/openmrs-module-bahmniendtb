package org.openmrs.module.bahmniendtb.report.renderer.template;

import org.openmrs.module.reporting.dataset.DataSetColumn;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseColumnWriter {

	private final Writer writer;

	private final DataSetColumn column;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String COLUMN_DEFINITION = " `%s` %s DEFAULT NULL";

	public DatabaseColumnWriter(DataSetColumn column, Writer writer) {
		this.column = column;
		this.writer = writer;
	}

	public void write() throws IOException {
		writer.write(String.format(COLUMN_DEFINITION,column.getName(),getSqlDatatype(column.getDataType())));
	}

	//TODO: this needs to be refactored. Waiting for an end-to-end test
	private String getSqlDatatype(Class<?> dataType) {

		if(dataType.equals(String.class)){
			return "text";
		}

		if(dataType.equals(Date.class)){
			return "datetime";
		}

		if(dataType.equals(Integer.class)){
			return "int(20)";
		}

		return null;
	}

	public void writeInsert(Object columnValue) throws IOException {
		writer.write(getFormattedValue(columnValue));
	}

	private String getFormattedValue(Object columnValue){
		if(columnValue == null)
			return "null";

		if(column.getDataType().equals(String.class)){
			return "'"+columnValue.toString()+"'";
		}

		if(column.getDataType().equals(Date.class)){
			return "'"+simpleDateFormat.format(columnValue)+"'";
		}

		if(column.getDataType().equals(Integer.class)){
			return columnValue.toString();
		}

		throw new RuntimeException("Unsupported Datatype ["+column.getDataType()+"] and cannot convert the value ["+columnValue+"]");
	}
}
