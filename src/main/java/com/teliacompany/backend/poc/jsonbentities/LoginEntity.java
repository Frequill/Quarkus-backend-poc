package com.teliacompany.backend.poc.jsonbentities;

import javax.json.bind.annotation.JsonbProperty;

public class LoginEntity {

    @JsonbProperty("status")
    private String jsonbStatus;
    @JsonbProperty("username")
    private String jsonbUsername;
    @JsonbProperty("loginToken")
    private String jsonbToken;
    @JsonbProperty("password")
    private String jsonbPass;

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

    public String getJsonbToken() {
        return jsonbToken;
    }

    public void setJsonbToken(String jsonbToken) {
        this.jsonbToken = jsonbToken;
    }

    public String getJsonbPass() {
        return jsonbPass;
    }

    public void setJsonbPass(String jsonbPass) {
        this.jsonbPass = jsonbPass;
    }
}