package org.openmrs.module.bahmniendtb.dataintegrity.rules.IT;


import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import javax.validation.constraints.AssertTrue;

@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class SimpleTest {
    @Test
    public void name() throws Exception {
        Assert.assertTrue(true);

    }
}
