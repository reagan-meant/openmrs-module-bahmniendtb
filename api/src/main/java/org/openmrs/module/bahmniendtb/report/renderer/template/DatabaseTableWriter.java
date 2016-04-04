package org.openmrs.module.bahmniendtb.report.renderer.template;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class DatabaseTableWriter {

	private String name;
	private DataSet dataSet;
	private Writer writer;
	private static final String CREATE_TABLE_COMMAND_PREFIX = "DROP TABLE IF EXISTS `%s`;\n"
			+ "CREATE TABLE `%s` (\n";
	private static final String CREATE_TABLE_COMMAND_SUFFIX = ");";
	private static final String INSERT_TABLE_COMMAND = "INSERT INTO `%s` VALUES ";


	public DatabaseTableWriter(String name, DataSet dataSet,Writer writer) {
		this.name = name;
		this.dataSet = dataSet;
		this.writer = writer;
	}

	public void write() throws IOException {
		List<DataSetColumn> dataSetColumns = dataSet.getMetaData().getColumns();

		writer.write(String.format(CREATE_TABLE_COMMAND_PREFIX,name,name));
		int firstColumn=0;
		for(DataSetColumn column: dataSetColumns){
			if(firstColumn!=0){
				writer.write(",");
			}
			new DatabaseColumnWriter(column,writer).write();
			firstColumn++;
		}
		writer.write(CREATE_TABLE_COMMAND_SUFFIX);
	}

	public void writeData() throws IOException {
		StringBuilder insertCommand = new StringBuilder(String.format(INSERT_TABLE_COMMAND,name));

		List<DataSetColumn> dataSetColumns = dataSet.getMetaData().getColumns();
		writer.write(insertCommand.toString());

		int i=0;
		for (DataSetRow dataSetRow : dataSet) {
			writer.write(addCommaIfRequired(i));
			i++;
			writeDataSetRow(dataSetColumns, dataSetRow);
		}
		writer.write(";");
	}

	private void writeDataSetRow(List<DataSetColumn> dataSetColumns, DataSetRow dataSetRow) throws IOException {
		writer.write("(");

		for(int i=0;i<dataSetColumns.size();i++){
			writer.write(addCommaIfRequired(i));
			Object cell = dataSetRow.getColumnValue(dataSetColumns.get(i));
			new DatabaseColumnWriter(dataSetColumns.get(i),writer).writeInsert(cell);
		}

		writer.write(")");
	}

	private String addCommaIfRequired(int index){
		if(index > 0)
			return ",";
		return "";
	}

}
