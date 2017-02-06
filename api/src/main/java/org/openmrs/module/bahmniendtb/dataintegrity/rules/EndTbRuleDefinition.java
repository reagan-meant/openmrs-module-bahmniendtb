package org.openmrs.module.bahmniendtb.dataintegrity.rules;


import org.openmrs.module.dataintegrity.DataIntegrityRule;
import org.openmrs.module.dataintegrity.rule.RuleDefinition;

public abstract class EndTbRuleDefinition<T> implements RuleDefinition<T> {

    @Override
    public DataIntegrityRule getRule() {
        return null;
    }

}
