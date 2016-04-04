package org.openmrs.module.bahmniendtb.report.manager;

import org.openmrs.module.bahmniendtb.report.renderer.SqlDumpRenderer;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class FullDataExportReportManager implements ReportManager{

	public static final String FULL_DATA_EXPORT_UUID = "225c4a2e-f65c-11e5-9821-0800275293b2";
	public static final String EXCEL_REPORT_DESIGN_UUID = "a3b556b6-f66f-11e5-9821-0800275293b2";
	public static final String CSV_REPORT_DESIGN_UUID = "e4e5fd83-f66f-11e5-9821-0800275293b2";
	public static final String SQL_REPORT_DESIGN_UUID = "31e608d7-9c1f-41eb-814f-a305059ca4f6";
	public static final String MESSAGE_PREFIX = "endtb.fulldataexport.";
	public static final String VERSION = "1.0-SNAPSHOT";

	@Autowired
	private SqlDataSetDefinition patientInformationDataSetDefinition;

	private String translate(String code) {
		String messageCode = MESSAGE_PREFIX+code;
		String translation = MessageUtil.translate(messageCode);
		if (messageCode.equals(translation)) {
			return code;
		}
		return translation;
	}

	@Override
	public String getUuid() {
		return FULL_DATA_EXPORT_UUID;
	}

	@Override
	public String getName() {
		return translate(MESSAGE_PREFIX + "name");
	}

	@Override
	public String getDescription() {
		return translate(MESSAGE_PREFIX + "description");
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition fullDataReportDefinition = new ReportDefinition();
		fullDataReportDefinition.setName(getName());
		fullDataReportDefinition.setUuid(getUuid());
		fullDataReportDefinition.setDescription(getDescription());
		fullDataReportDefinition.setParameters(getParameters());
		fullDataReportDefinition.addDataSetDefinition(patientInformationDataSetDefinition, new HashMap<String, Object>());

		return fullDataReportDefinition;
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign sqlReportDesign = new ReportDesign();
		sqlReportDesign.setUuid(SQL_REPORT_DESIGN_UUID);
		sqlReportDesign.setName("SqlGenerator");
		sqlReportDesign.setReportDefinition(reportDefinition);
		sqlReportDesign.setRendererType(SqlDumpRenderer.class);

		return Arrays.asList(ReportManagerUtil.createExcelDesign(EXCEL_REPORT_DESIGN_UUID,reportDefinition),sqlReportDesign);
	}

	@Override
	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
		return new ArrayList<ReportRequest>();
	}

	@Override
	public String getVersion() {
		return VERSION;
	}
}
