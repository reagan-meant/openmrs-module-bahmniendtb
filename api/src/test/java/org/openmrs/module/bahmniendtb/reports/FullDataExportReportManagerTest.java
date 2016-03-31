package org.openmrs.module.bahmniendtb.reports;

import org.junit.Test;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class FullDataExportReportManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private TestDataManager testDataManager;

	@Autowired
	private FullDataExportReportManager reportManager;

	@Autowired
	protected ReportDefinitionService reportDefinitionService;

	@Test
	public void testFullExport() throws Exception {
		ReportDefinition reportDefinition = reportManager.constructReportDefinition();
		ReportData reportData = reportDefinitionService.evaluate(reportDefinition, new EvaluationContext());
		DataSet patientDataSet = reportData.getDataSets().get(FullDataExportReportManager.PATIENT_DATA_SET_NAME);
		Iterator<DataSetRow> iterator = patientDataSet.iterator();

		int count = 0;
		while(iterator.hasNext()){
			iterator.next();
			count++;
		}
		assertEquals(10,count);
	}

}
