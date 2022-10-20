package com.teliacompany.backend.poc.jsonbentities;

import javax.json.bind.annotation.JsonbProperty;

public class RequestEntity {

    @JsonbProperty("requestId")
    private String requestId;
    @JsonbProperty("name")
    private String name;
    @JsonbProperty("specialAttack")
    private String specialAttack;

    public RequestEntity(String requestId, String name, String specialAttack) {
        this.requestId = requestId;
        this.name = name;
        this.specialAttack = specialAttack;
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

    public String getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(String specialAttack) {
        this.specialAttack = specialAttack;
    }
}