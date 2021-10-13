package com.example.jpatest;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.*;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@DataJpaTest
public class MemberRepositoryTest {

    //엔티티 매니져 팩토리 생성
    @PersistenceUnit
    EntityManagerFactory emf;
    //엔티티 매니저 생성
    @PersistenceContext
    EntityManager em;

    EntityTransaction tx;

    Member member;

    @BeforeEach
    public void setup(){
        String id = "idl";
        member = new Member(id,"지한",2);
    }

    @AfterEach
    public void cleanup(){
        em.remove(member);
        em.close();
        emf.close();
    }

    @Test
    public void entityTest() {
        try {
            tx.begin();
            tx = em.getTransaction();
            em.persist(member);

            member.setAge(20);

            Member findMember = em.find(Member.class, "idl");

            List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

            assertThat(member.getName()).isEqualTo("지한");
            assertThat(members.get(0).getName()).isEqualTo("지한");

            em.remove(member);
        } catch (Exception e) {
            log.info("실패");
        }
    }
}


