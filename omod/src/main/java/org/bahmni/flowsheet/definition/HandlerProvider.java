package org.bahmni.flowsheet.definition;

import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class HandlerProvider {

    public Handler getHandler(String handler) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> handlerClass = Class.forName(handler);
        String name = getNameForHandler(handler);
        return (Handler) Context.getRegisteredComponent(name, handlerClass);
    }

    private String getNameForHandler(String handler) {
        if (handler.equals(EndTBConstants.TREATMENT_END_DATE_HANDLER)) {
            return "treatmentEndDateHandler";
        }
        if(handler.equals(EndTBConstants.SIX_MONTH_POST_TREATMENT_OUTCOME_HANDLER)) {
            return "sixMonthPostTreatmentOutcomeHandler";
        }
        return "";
    }
}
