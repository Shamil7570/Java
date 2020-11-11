package com.khizriev.server.storage.service;

import lombok.Getter;
import lombok.Setter;

/**
 * User
 */
@Getter
@Setter
public class User {
    String login;
    String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}