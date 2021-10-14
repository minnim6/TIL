package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class EncoderTest {
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
    public void passwordTest(){
        String result = passwordEncoder.encode("myPassword");
        assertTrue(passwordEncoder.matches("myPassword", result));
    }
}
