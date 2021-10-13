package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
public class EncoderTest {

    @Autowired
    InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void createDelegatingPasswordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("sha256", new StandardPasswordEncoder());

        PasswordEncoder passwordEncoder =  new DelegatingPasswordEncoder(idForEncode, encoders);
        log.info(passwordEncoder.encode("password"));
        idForEncode = "pbkdf2";

        passwordEncoder = new DelegatingPasswordEncoder(idForEncode,encoders);
        log.info(passwordEncoder.encode("password"));
    }

    @Test
    public void getPassword(){
        UserDetails user = inMemoryUserDetailsManager.loadUserByUsername("user");
        log.info(user.getPassword());
        log.info(user.getUsername());
    }
    @Test
    public void passwordTest(){
        log.info(passwordEncoder.encode("password"));
    }
}
