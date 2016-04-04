package org.openmrs.module.bahmniendtb.report;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.report.renderer.SqlDumpRenderer;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SqlGenerationRendererTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldRenderVelocityTemplate() throws Exception {
		shouldRenderTemplate("SqlGenerationTemplate.vm", "Velocity");
	}

	private void shouldRenderTemplate(String templateName, String templateType) throws Exception {

		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Test Report");

		SimplePatientDataSetDefinition allPatients = new SimplePatientDataSetDefinition("allPatients", "");
		allPatients.addPatientProperty("patientId");
		allPatients.addPatientProperty("gender");
		allPatients.addPatientProperty("birthdate");
		reportDefinition.addDataSetDefinition("allPatients", allPatients, null);

		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("Males");
		males.setMaleIncluded(true);

		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setName("Females");
		females.setFemaleIncluded(true);

		CohortCrossTabDataSetDefinition genderDsd = new CohortCrossTabDataSetDefinition();
		genderDsd.addColumn("males", males, null);
		genderDsd.addColumn("females", females, null);
//		reportDefinition.addDataSetDefinition("genders", genderDsd, null);

		final ReportDesign reportDesign = new ReportDesign();
		reportDesign.setName("TestDesign");
		reportDesign.setReportDefinition(reportDefinition);
		reportDesign.setRendererType(SqlDumpRenderer.class);

//		if (templateType != null) {
//			reportDesign.addPropertyValue(TextTemplateRenderer.TEMPLATE_TYPE, templateType);
//		}

		EvaluationContext context = new EvaluationContext();
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData reportData = rs.evaluate(reportDefinition, context);

		ReportDesignResource resource = new ReportDesignResource();
		resource.setName(templateName);
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(templateName);
		resource.setContents(IOUtils.toByteArray(is));
		IOUtils.closeQuietly(is);
		reportDesign.addResource(resource);

		reportDesign.addPropertyValue("databaseName","endtbreports");

		SqlDumpRenderer renderer = new SqlDumpRenderer();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		renderer.render(reportData, "ReportData", baos);
		String renderedOutput = baos.toString();
//		System.out.println(renderedOutput);

//		String xml = "<?xml version=\"1.0\"?>" + "<dataset>" + "	<rows>"
//				+ "		<row><patientId>2</patientId><gender>M</gender><birthdate>08/Apr/1975</birthdate></row>"
//				+ "		<row><patientId>6</patientId><gender>M</gender><birthdate>27/May/2007</birthdate></row>"
//				+ "		<row><patientId>7</patientId><gender>F</gender><birthdate>25/Aug/1976</birthdate></row>"
//				+ "		<row><patientId>8</patientId><gender>F</gender><birthdate></birthdate></row>" + "	</rows>"
//				+ "</dataset>";
//
//		xml = templateType != null ? StringUtils.deleteWhitespace(xml) : "Males=2Females=2";
//		Assert.assertEquals(xml, renderedOutput);

	}
}
