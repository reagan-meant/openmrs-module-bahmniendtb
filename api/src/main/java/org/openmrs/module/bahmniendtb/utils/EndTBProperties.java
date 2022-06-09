package org.openmrs.module.bahmniendtb.utils;

import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Component
public class EndTBProperties {

    protected Properties props;

    public EndTBProperties(String fileName) {
        this(OpenmrsUtil.getApplicationDataDirectory(), fileName);
    }

    public EndTBProperties() {
        this(OpenmrsUtil.getApplicationDataDirectory(), "/endTB.properties");
    }

    private EndTBProperties(String directory, String file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(directory + file);
            props = new Properties();
            props.load(new InputStreamReader(inputStream));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAuthUrl() {
        return props.getProperty("openmrs.auth.uri");
    }

    public String getOpenmrsUser() {
        return props.getProperty("openmrs.username");
    }

    public String getOpenmrsPassword() {
        return props.getProperty("openmrs.password");
    }

    public String getConnectionTimeoutInMilliseconds() {
        return props.getProperty("openmrs.connectionTimeoutInMilliseconds");
    }

    public String getReplyTimeoutInMilliseconds() {
        return props.getProperty("openmrs.replyTimeoutInMilliseconds");
    }
}