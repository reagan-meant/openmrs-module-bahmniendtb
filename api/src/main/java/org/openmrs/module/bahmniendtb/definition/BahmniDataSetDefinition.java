package org.openmrs.module.bahmniendtb.definition;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;

import java.util.Map;

public interface BahmniDataSetDefinition {

	String getKey();

	BaseDataSetDefinition getDataSetDefinition();

	Map<String, Object> getMappings();

}
