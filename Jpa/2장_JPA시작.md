# 2장 JPA 시작 

라이브러리 , H2 데이터 베이스 설치 , 프로젝트 구조는 생략

> 예시를 위한 회원 테이블생성 

```sql
CREATE TABLE MEMBER(
	ID VARCHAR(225) NOT NULL,
	NAME VARCHAR(225),
	AGE INTEGER,
	PRIMARY KEY (ID)
)
```

> 애플리케이션에서 사용할 회원 클래스 작성

```java
@Getter
@Setter
public class Member{
	private String id;
	private String name;
	private int age;
}
```

> 회원클래스와 테이블 매핑

```java
@Getter
@Setter
@Table(name = "MEMBER")
@Entity
public class Member{
    
    @Id
    @Column(name = "ID")
    private String id;
    
    @Column(name ="NAME")
    private String name;
    
    private int age;
    
}
```

| 어노테이션 | 설명                                                         |
| ---------- | ------------------------------------------------------------ |
| @Entity    | 이 클래스를 테이블과 매핑한다고 jpa에게 알려준다.            |
| @Table     | 매핑할 테이블 정보를 알려준다 . 생략 할경우 테이블 이름을 클래스로 매핑한다. |
| @id        | 테이블의 기본키에 매핑한다.                                  |
| @Colum     | 필드를 칼럼에 매핑한다 . 생략할 경우 필드명을 사용해서 칼럼에 매핑한다. |

> jpa 사용 

```java
pulic class JpaMain{
	pulic static void main(String[]args){
		//엔티티 매니져 팩토리 생성
    	EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("jpabook");
		//엔티티 매니저 생성
		EntityManager em = emf.createEntityManager();
		//트랜잭션 획득
		EntityTransaction tx = em.getTransaction();
        
        try{
          tx.begin(); //트랜잭션 - 시작
          logic(em); //비즈니스 로직 실행
          tx.commit(); // 트랜잭션 - 커밋
        }catch(Exception e){
            tx.rollback(); //트랜잭션 롤백
        }finally{
            em.close(); //엔티티 매니저 종료
        } 
        emf.close(); //엔티티 매니저 팩토리 종료
	}
    private static void logic(EntityManger em){...}
}
```

코드는 크게 3부분으로 나뉘어 있다.

- 엔티티 매니저 설정
- 트랜잭션 관리
- 비즈니스 로직

> tip - 데이터베이스의 상태를 변환시키는 하나의 논리적 기능을 수행하기 위한 작업의 단위 또는 한꺼번에 모두 수행되어야 할 일련의 연산들을 의미한다

### 엔티티 매니저의 생성 과정 

1. 우선 persistence.xml 의 설정 정보를 사용해서 엔티티 매니저 팩토리를 생성해야한다. 이때 persistence 클래스를 사용하는데 , 이클래스는 앤티티 매니저 팩토리를 생성해서 jpa를 사용할수 있게해준다. persistence에서 이름이 jpabook인 영속성 유닛을 persistence-unit 을 찾아서 엔티티 매니저를 생성한다. 이때 xml의 생성 정보를 읽어서 jpa를 동작시키기 위한 기반 객체를 만들고 jpa 구현체에 따라서는 데이터 베이스 커넥션 풀도 생성하므로 엔티티 매니저 팩토리를 생성하는 비용은 아주크다. **따라서 엔티티 매니저 팩토리는 전체에서 딱 한번만 생성하고 공유해서 사용해야한다.**
2. 엔티티 매니저 팩토리에서 엔티티 매니저를 생성한다 . jpa기능의 대부분은 **엔티티 매니저가 제공한다.** 엔티티 매니저는 데이터베이스 커넥션과 밀접한 관계가있으므로 스레드간에 공유하거나 재사용하면 안된다.
3. 마지막으로 사용이 끝난 엔티티 매니저는 다음처럼 반드시 종료해야한다 . 앤티티 매니저 팩토리도 종료 해야한다.

### 트랜잭션관리

jpa를 사용하면 항상 트랜잭션 안에서 데이터를 변경해야 한다 . 트랜잭션 없이 데이터를 변경하면 예외가 발생한다. 엔티티 매니저에서 트랜잭션 api를 받아와야한다.

```
EntityTransaction tx = ex.getTransaction();
try{
	tx.begin();
	logic(em);
	tx.commit();
}catch(Exception e){
	tx.rollback();
}
```

트랜잭션 API를 사용해서 비즈니스 로직이 정상 동작하면 트랜잭션을 커밋하고 , 예외가 발생하면 트랜잭션을 롤백한다.

### 비즈니스 로직

```java
 @Test
    public void logic() {

        em.persist(member);

        member.setAge(20);
        
        Member findMember = em.find(Member.class, "idl");
        log.info("findMember=" + findMember.getName() + ", age =" + findMember.getAge());

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        log.info("member.size = " + members.size());

        em.remove(member);
    }
```

> 출력결과

```
findMember=지한, age =20
member.size = 1
```

- 수정 부분을 보면 내용을 반영하는 부분이 없고 , 단순히 엔티티의 값만 변경했다.하지만 jpa는 어떤 엔티티가 변경되었는지 추적하는 기능을 갖추고 있다 . 따라서 setAge() 처럼 엔티티의 값만 변경하면 다음과같은 update sql을 생성해서 데이터 베이스에 값을 변경한다.

