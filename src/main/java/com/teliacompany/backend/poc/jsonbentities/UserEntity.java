package com.teliacompany.backend.poc.jsonbentities;

import javax.json.bind.annotation.JsonbProperty;

public class UserEntity {

    @JsonbProperty("username")
    private String username;
    @JsonbProperty("password")
    private String password;
    @JsonbProperty("email")
    private String email;

    public UserEntity() {}

    public UserEntity(String username, String password, String email){
        setUsername(username);
        setPassword(password);
        setEmail(email);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
