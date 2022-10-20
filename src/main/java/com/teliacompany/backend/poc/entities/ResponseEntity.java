package com.teliacompany.backend.poc.entities;

public class ResponseEntity {

    public String requestId;

    public Integer status;

    public String message;

    public String response;

    public ResponseEntity() {}

    public ResponseEntity(String requestId, Integer status, String message, String response) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.response = response;
    }

}