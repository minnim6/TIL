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

### Cross Site Request Forgery (CSRF)

- csrf 공격이란 ?  - 웹사이트에서 인증 한뒤 로그 아웃 하지 않고 다른 악의 적인 웹 사이트에 방문 했다고 해보자 . 돈을 준다길래 제출 버튼을 눌렀다 . 이과정에서 의도치 않게 악의 적인 사용자에서 100달러를 송금 하고 말았다 . 이웹사이트 에선 쿠키를 볼순 없지만 , 은행 사이트에서 사용했던 쿠키도 요청에 포함되기 때문에 실제로 가능한 일이다.

  csrf 공격이 먹히는 이유는 공격받는 웹사이트의 http요청과 공격하는 웹사이트의 요청이 완전히 동일하기 때문이다 . 그렇기 때문에 악의적인 웹사이트의 요청은 거절하고 은행 웹사이트의 요청만 수락할수있는 뾰족한 방법이 없다 . csrf 공격을 방어 하려면 악의적인 사이트에서는 제공할 수 없는 무언가를 요청에 사용해서 두 요청을 구분해야만한다.

  스프링은 csrf공격을 방어하기 위한 두가지 매커 니즘을 제공한다

- 동기화 토큰 패턴 - 가장 포괄적인 방법 . 이패턴은 모든 http요청에 세션 쿠키와는 별도로 csrf 토큰이라 불리는 안전한 , 랜덤으로 생성한 값을 추가한다.

  핵심은 http 요청에 브라우저가 자동으로 넣어주지 않는 csrf 토큰이 있어야 한다는 것이다. 

  예를 들어 http 피러미터나 http헤더에 실제 csrf 토큰을 받으면 csrf공격을 방어 할수 있다.

  쿠키는 브라우저가 http요청에 자동으로 포함시키기 떄문에 csrf토큰을 쿠키에서 받으면 효과가 없다.

  애플리케이션 상태를 업데이트 하는 http요청에서만 csrf토큰을 사용하도록 조건을 완화해도 좋다

  이를위해서 반드시 safe메소드는 멱등성을 보장해야한다 . ( 즉 http 메소드 get, head , optons,trace 요청은 어플리케이션 상태를 변화 시키면안된다. )

### csrf 는 어떨때 방어 해야할까 ?

일반 사용자가 브라우저에서 처리할수있는 모든 요청에 csrf방어를 적용하길 권장하는 바이다.

만들고 있는 서비스를 브라우저가 아닌 다른 클라이언트에서만 사용한다면 csrf방어를 비활성화해도 좋다.

### csrf and Session Timeouts

서버에서 비교할 때 쓸 csrf 토큰 값은 종종 세션에 저장 하곤한다. 이말은 세션이 만료하는 즉시 서버에선 csrf 토큰값을 조회 할 수 없으므로 http 요청을 거절 한다는 뜻이다 . 타임 아웃을 해결 할수 있는 방법은 많으며 모두 각각의 장단점이 있다.

- 타임아웃 이슈를 줄일 가장 좋은 방법은 폼을 제출할 때 자바스크립트로 csrf토큰을 요청하는것이다.이렇게 하면 csrf 토큰을 폼에 업데이트하고 제출할수있다.

- 자바스크립트로 사용자에게 세션이 곧 만료됨을 알리는 것이다 . 사용자가 버튼을 누르면 갱신

- csrf토큰을 쿠키에 저장할수 있다 . csrf토큰이 세션보다 더 오래 지속된다.

  ( 디폴트로 쿠키에 저장할경우 헤더를 다른 도메인으로 설정하는 취약점 공격이 있기떄문)

### Multipart (file upload)

csrf 공격을 막으려면 반드시 http 요청 body를 읽어 실제 csrf 토큰을 확인해야 한다. 

하지만 반드시 body를 읽는다는것이 파일이 업로드된다는 뜻이므로 외부 사이트에서도 파일을 업로드 할수 있다.

Multipart / form-data 에서 사용 할수 있는 csrf방어옵션은 두가지가 있다 .

- 요청 body에 실제 csrf토큰을 추가하는것이다 . csrf 토큰을 body에 넣으면 body를 읽고나서 권한을 부여한다. 즉 , 누구든지 서버에 임시파일을 만들수 있다 . 하지만 결국엔 인가된 사용자가 제출한 파일만 처리된다 . 임시 파일업로드가 서버에 주는 영향은 거의 무시해도 될 수준이기때문에 , 일반적으로 권장하는 방법이다.
- 권한이 없는 사용자가 임심 파일을 업로드하게 만드는게 불가능하다면, 폼에 action 속성에 쿼리 파라미터로 csrf 토큰을 넣는 것도 방법이다 . 쿼리 파라미터가 유출될수 있다는 단점은 있다 . 민감한 정보를 유출하지 않는 , 좀더 일반적인 관행은 body나 헤더에 두는 것이다.

## Security Http Response Headers

웹 어플리케이션의 보안을 위해 사용 할수있는 http응답 헤더는 다양하다. 이번섹션은 스프링 시큐리티가 직접 지원하는 여러 가지 http 응답 헤더를 설명할것이다 . 필요하다면 스프링 시큐리티에서 **커스텀 헤더를 설정할수있다.**

### Default Security Headers

스프링 시큐리티는 기본적인 보안을 위한 http 응답 헤더의 디폴트 셋을 제공한다.

아래에 있는 헤더도 스프링 시큐리티의 디폴트 헤어데 해당한다.

```http
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Content-Type-Options: nosniff
Strict-Transport-Security: max-age=31536000 ; includeSubDomains
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
```

이 디폴트 헤더만으로 해결 할수 없는 요구 사항이 있다면 간단하게 디폴트 헤더를 제거하거나 , 수정,추가 할수 있다. 

### Cache Control

스프링 시큐리티는 사용자 컨텐츠를 보호하기 위해 기본적으로 캐쉬를 비활성화한다.

민감한 정보 조회 권한을 인가받은 사용자가 로그아웃했을때 , 다른 사용자가 악의적으로 뒤로가기 버튼을 눌러 해당 정보를 보지 못하기 위해서다 . cache-control 헤더는 디폴트로 다음과 같이 전송한다.

```http
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
```

스프링 시큐리티는 보안을 위해 기본적으로 이헤더를 추가한다 . 하지만 어플리케이션 자체에서 cache-control 헤더를 사용하면 스프링 시큐리티는 디폹트 헤더값을 추가하지 않는다.

**따라서 어플리케이션 에서는 원하는 css나 자바 스크립트같은 스태릭 리소스를 캐시 할수 있다.**

### content type options

인터넷 익스플로러를 포함해서 , 브라우저는 컨텐츠  **스니핑( [바이트 스트림](https://en.wikipedia.org/wiki/Byte_stream) 의 콘텐츠를 검사하여 그 안에 있는 데이터 의 [파일 형식](https://en.wikipedia.org/wiki/File_format) 을 추론하는 것 )** 을 사용해 요청의 컨텐츠 타입을 추론해왔다 .

브라우저가 컨텐츠 타입을 명시하지않은 리소스의 컨텐츠 타입을 추측하기 때문에 사용자는 좀더 나은 경험을 할수 있다 . 예를들어 브라우저는 컨텐츠 타입을 명시하지 않는 자바 스크립트 파일을 만나면 , 컨텐츠 타입을 추론한 다음 실행한다.

컨텐츠 스니핑의 문제점은 악의적으로 다국어를 사용해 xss 공격( 관리자가 아닌 권한이 없는 사용자가 웹 사이트에 스크립트를 삽입하는 공격 )을 실행할수 있다는점이다.

스프링 시큐리티는 디폴트로 http 응답에 다음 헤더를 추가해 컨텐츠 스니핑을 비활성화 한다

```http
X-Content-Type-Options: nosniff
```

### http strict Transport Security (HSTS)

은행 웹사이트 주소를 직접 치고 들어간다면 mm.mm.com 과 http://mm.mm.com 둘중에 무엇을 타이핑 할것인가 . http 프로토콜을 생략하는건 중간자 공격에 취약하다.

웹사이트에서 프로토콜을포함해 리다이렉트 하더라도 악의적으로 최초 http요청을 가로채서 응답을 조작할수 있다.

htttp 프로토콜을 생략하고 접근하는 사용자가 많기 때문에 http strict transport security가 생겨났다. 프로토콜을 생략한 접속이 hsts 호스트에 추가되면 브라우저는 프로토콜을 추가한 요청으로 해석해야한다는 것을 미리 알수 있다 . 따라서 중간자 공격을 받을 가능성이 현저히 떨어진다.



