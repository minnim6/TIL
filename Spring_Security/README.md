# Spring Security
> 하위 SpringBootSecurity 에서 스프링부트 버전으로 예시 파일을 사용해 보고 있습니다 . 참고해주세요 :)

스프링 5.0 버전 이전 -> NoOpasswordEncoder 가 디폴트 패스워드 인코더 였다 .

현재 디폴트 패스워드 인코더는 BCryptPasswordEncoder나 이와 유사한 무언가라고 생각 했을 것이다. 하지만 현실 세계 에서는 세가지 문제를 간과 했다.

- 많은 어플리 케이션이 마이그레이션이 쉽지 않은 옛날 방식으로 비밀번호를 인코딩 하고 있다.
- 비밀번호를 저장하기 위한 최선의 관행은 또다시 바뀔것이다
- 스프링 시큐리티는 프레임워크 이기 때문에 하위 호환성을 보장하지 않는 업데이트를 자주 할순 없다.

대신 스프링 시큐리티는 DelegatingPasswordEncoder 를 도입해서 다음고 같은 방법으로 이문제를 해결한다.

- 비밀번호를 현재 권장하는 저장 방식으로 인코딩함을 보장한다
- 비밀번호 검증은 최식 형식과 레거시 형식을 모두 지원한다 .
- 나중에 인코딩을 변경할수있다.

```java
PasswordEncoder passwordEncoder =
    PasswordEncoderFactories.createDelegatingPasswordEncoder();
```

```java
String idForEncode = "bcrypt";
Map encoders = new HashMap<>();
encoders.put(idForEncode, new BCryptPasswordEncoder());
encoders.put("noop", NoOpPasswordEncoder.getInstance());
encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
encoders.put("scrypt", new SCryptPasswordEncoder());
encoders.put("sha256", new StandardPasswordEncoder());

PasswordEncoder passwordEncoder =
    new DelegatingPasswordEncoder(idForEncode, encoders);
```

id는 사용할 PassWordEncoder를 식별하는데 사용하는 값이고 ,encodedPassword는 선택한 PasswordEncoder에서 사용할 인코딩된 비밀번호이다 . 

> 출력결과 

```
{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG // (1)
{noop}password // (2)
{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc // (3)
{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc= // (4)
{sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0 // (5)
```

생성자에 전달한 idForEncode가 비밀번호를 인코딩 할때 사용할 PasswordEncoderd를 결정한다 .

위에서 만든 DelegatingPasswordEncoder에선 Password인코딩 결과를 BCryptPasswordEncoder 위임하며 ,

프리 픽스는 {bcrypt}를 사용한다.

### 패스워드 매칭

{id}를 기반으로 매칭되며 id는 생성자에 제공한 PassWordEncoder로 매핑된다. Password Storage Format에서 사용한 예제는 동작 방식을 확인할수 있는 실전 예시이다.

기본적으로 비밀번호와 id가 매핑되지 않으면 (null id포함) 

matches( charSequence, String ) 는 IllegalArgumentException를 던진다 . 이는

DelegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(PasswordEncoder)로 커스텀 할수 있다.



