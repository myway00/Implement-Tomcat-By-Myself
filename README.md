# 톰캣 구현하기

- 작업한 코드 부분
   - 1) `tomcat/src/main/java/org/apache` (catalina, coyote) 
   - 2) `tomcat/src/main/java/nextstep/jwp/controller` (controller)


## 작업 개요 
> 아래 사항들이 도저히 감이 잡히지 않고, 서버 개발자로서 이 부분에 대해 숙지해야, 서버 이상 시 점검 가능 역량 쌓을 수 있다고 판단
- TOMCAT 이 HTTP 요청을 받아 처리하고 응답하는 과정 
- TCP Socket을 통해 socket의 buffer에 I/O로 들어온 데이터 처리하는 과정
- Thread를 관리하는 과정 (어떻게 Multithread 로 구동되는가)
_________________________________________________________________________
## 기능 구현사항

http://localhost:8080/index.html 페이지에 접근 가능하다.
접근한 페이지의 js, css 파일을 불러온다.
uri의 QueryString을 파싱한다.
HTTP 요청의 Request 를 파싱한다.
로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.
HTTP Request, HTTP Response 클래스로 나눠서 구현한다.
Controller 인터페이스와 RequestMapping 클래스를 활용하여 if절을 제거한다.
_________________________________________________________________________

## 프로세스 
1. Application이 실행되고 Tomcat이 생성되고 실행됩니다.
2. Tomcat이 실행되면 Connector가 생성되고 실행됩니다.
3. Connector가 생성될때 ThreadPool과 ServerSocket을 생성합니다.
4. 제일 앞단에서 Socket이 클라이언트의 요청을 기다리다가 요청이 들어오고 수락되면, ServerSocket에 요청데이터를 넘깁니다.
5. Connector는 요청 1개당 ThreadPool에서 Thread 1개를 사용해 요청을 처리합니다.
6. 클라이언트의 요청을 읽을때는 ServerSocket의 inputStream을 읽습니다.
 (이와같이 Socket에 데이터를 읽고쓰며 클라이언트와 데이터를 주고받을 수 있습니다.)
7. 요청이 처리되면 다시 ServerSocket을 통해 outputStream을 쓰고 클라이언트에게 데이터를 전달합니다.

_________________________________________________________________________

## Tomcat Connector (tomcat docs )
- 서버로 들어오는 각 요청에는 요청 기간 동안 쓰레드가 필요합니다.
- 현재 사용 가능한 요청 처리 쓰레드에서 처리할 수 있는 것보다 많은 동시 요청이 수신되면 구성된 최대값(max-Threads)값까지 추가 쓰레드가 생성됩니다.
- 더 많은 동시 요청이 수신되면 Connector에 의해 생성된 ServerSocket 내부에 구성된 최대값 (accepCount)값까지 누적됩니다.
- 더 이상의 추가 동시 요청은 리소스를 처리할 수 있을 때까지 "connection refused" 오류를 수신합니다.
   - maxThread에 따라 newFixedThreadPool 를 생성합니다.   
   - acceptCount에 따라 ServerSocket을 생성합니다.
   - Connector가 생성될때 ServerSocket을 생성합니다.
   - 요청 1개당 1개의 쓰레드에 요청을 처리합니다.

____________________________________________________________________________

## Http11Processor
- Connector로부터 받은 Socket의 InputStream을 읽고 데이터를 처리한 후 OutputStream에 데이터를 담아 클라이언트에게 전달합니다.
