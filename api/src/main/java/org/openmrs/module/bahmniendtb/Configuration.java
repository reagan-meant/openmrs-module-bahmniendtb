package org.openmrs.module.bahmniendtb;

import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

	public static final String PATIENT_DATA_SET_SQL = "patientDataSet.sql";
	public static final String PATIENT_DATA_SET_NAME = "patient";

	@Bean
	public SqlDataSetDefinition patientInformationDataSetDefinition(){
		String sql = ReportUtil.readStringFromResource(PATIENT_DATA_SET_SQL);
		SqlDataSetDefinition patientInfoSqlDataSetDefinition = new SqlDataSetDefinition(PATIENT_DATA_SET_NAME,"",sql);
		return patientInfoSqlDataSetDefinition;
	}

}
