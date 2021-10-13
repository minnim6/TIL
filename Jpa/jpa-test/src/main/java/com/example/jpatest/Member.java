package com.example.jpatest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Table(name = "MEMBER")
@NoArgsConstructor
@Entity
public class Member{

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name ="NAME")
    private String name;

    private int age;

    public Member(String id,String name,int age){
        this.id = id;
        this.name = name;
        this.age = age;
    }

}
