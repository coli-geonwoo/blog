package com.example.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class User {

    private Long id;
    private final String account;
    private String password;
    private final String email;


    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void changePassword(String password) {
        this.password = password;
    }
}

