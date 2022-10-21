package com.teliacompany.backend.poc.jsonbentities;

import javax.json.bind.annotation.JsonbProperty;

public class RequestEntity {

    @JsonbProperty("requestId")
    private String requestId;
    @JsonbProperty("name")
    private String name;
    @JsonbProperty("inputString")
    private String inputString;

    public RequestEntity(String requestId, String name, String inputString) {
        this.requestId = requestId;
        this.name = name;
        this.inputString = inputString;
    }

    public RequestEntity() {}

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }
}