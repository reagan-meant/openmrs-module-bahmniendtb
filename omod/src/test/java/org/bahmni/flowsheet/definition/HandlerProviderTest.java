package org.bahmni.flowsheet.definition;

import org.bahmni.flowsheet.definition.impl.TreatmentEndDateHandler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniendtb.EndTBConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})

public class HandlerProviderTest {

    private HandlerProvider handlerProvider;

    @Autowired
    TreatmentEndDateHandler treatmentEndDateHandler;

    @Before
    public void setup() {
        mockStatic(Context.class);
        initMocks(this);
        handlerProvider = new HandlerProvider();
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldReturnNullIfHandlerNotPresent() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        handlerProvider.getHandler("someHandler");
    }

    @Ignore
    @Test
    public void shouldReturnTreatmentStartDateHandlerObject() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        PowerMockito.when(Context.getRegisteredComponent("treatmentEndDateHandler", TreatmentEndDateHandler.class)).thenReturn(treatmentEndDateHandler);

        Handler treatmentStartDateHandler = handlerProvider.getHandler(EndTBConstants.TREATMENT_END_DATE_HANDLER);

        assertEquals(TreatmentEndDateHandler.class, treatmentStartDateHandler.getClass());
    }

}