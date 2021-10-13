package com.example.demo;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public class Member {

    private String userName;

    private String passWord;

    private Roles roles;

    public Member(String userName,String passWord , Roles roles){
        this.userName = userName;
        this.passWord = passWord;
        this.roles = roles;
    }
}
