package com.teliacompany.backend.poc.entities;

import java.time.LocalTime;

public class LoginToken {

    private String fakedToken;

    public LoginToken(String user){
        LocalTime time = LocalTime.now();
        setFakedToken(user + time.toString());
    }


    public String getFakedToken(){
        return fakedToken;
    }

    public void setFakedToken(String fakedToken){
        this.fakedToken = fakedToken;
    }


}
