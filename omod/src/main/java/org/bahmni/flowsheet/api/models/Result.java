package org.bahmni.flowsheet.api.models;

import org.bahmni.flowsheet.api.Status;

public class Result {
    Status status;

    public Result(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
