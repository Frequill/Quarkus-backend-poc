package org.acme;

import java.time.LocalTime;

public class LoginTokenEntity {

    private String fakedToken;

    public LoginTokenEntity(String user){
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
