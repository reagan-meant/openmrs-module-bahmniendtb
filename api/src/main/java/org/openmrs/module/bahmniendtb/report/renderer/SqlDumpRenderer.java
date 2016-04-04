package org.openmrs.module.bahmniendtb.report.renderer;

import org.openmrs.module.bahmniendtb.report.renderer.template.DatabaseTableWriter;
import org.openmrs.module.bahmniendtb.report.renderer.template.DatabaseWriter;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public class SqlDumpRenderer extends ReportDesignRenderer {

	@Override
	public String getRenderedContentType(ReportRequest request) {
		return "application/sql";
	}

	@Override
	public String getFilename(ReportRequest request) {
		return "someFileName.sql";
	}

	@Override
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		Writer writer = new OutputStreamWriter(out, "UTF-8");

		new DatabaseWriter("endtbreports",writer).write();

		Map<String, DataSet> datasets = reportData.getDataSets();
		Iterator<Map.Entry<String, DataSet>> iterator = datasets.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String,DataSet> entry = iterator.next();
			DatabaseTableWriter databaseTableWriter = new DatabaseTableWriter(entry.getKey(),entry.getValue(),writer);
			databaseTableWriter.write();
			databaseTableWriter.writeData();
		}

		writer.flush();
	}

}
