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
@Table(name = "Member")
@Entity
public class Member{
	@id
	@Colum(name = "ID")
	private String id;
	@Colum(name ="NAME")
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
			Prsistencr.createEntityMangerFactory("jpabook");
		//엔티티 매니저 생성
		EntityManager em = emf.createEntityManager();
		//트랜잭션 획득
		EntityTransaction tx = em.getTransaction();
	}
}
```

