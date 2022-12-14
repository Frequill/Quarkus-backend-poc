package com.teliacompany.backend.poc.jsonbentities;

import javax.json.bind.annotation.JsonbProperty;

public class LoginEntity {

    // BACKEND login entity needs to carry loginToken so that it can be sent to Frontend

    @JsonbProperty("status")
    private String jsonbStatus;
    @JsonbProperty("username")
    private String jsonbUsername;
    @JsonbProperty("password")
    private String jsonbPass;
    @JsonbProperty("loginToken")
    private String jsonbLoginToken;

    public LoginEntity(){}

    public LoginEntity(String status, String user, String password){
        setJsonbStatus(status);
        setJsonbUsername(user);
        setJsonbPass(password);
    }



    public String getJsonbStatus() {
        return jsonbStatus;
    }

    public void setJsonbStatus(String jsonbStatus) {
        this.jsonbStatus = jsonbStatus;
    }

    public String getJsonbUsername() {
        return jsonbUsername;
    }

    public void setJsonbUsername(String jsonbUsername) {
        this.jsonbUsername = jsonbUsername;
    }

    public String getJsonbPass() {
        return jsonbPass;
    }

    public void setJsonbPass(String jsonbPass) {
        this.jsonbPass = jsonbPass;
    }

    public String getJsonbLoginToken() {
        return jsonbLoginToken;
    }

    public void setJsonbLoginToken(String jsonbLoginToken) {
        this.jsonbLoginToken = jsonbLoginToken;
    }
}
