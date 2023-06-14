# 톰캣 구현하기

## 기능 요구사항
- [ ] GET /login 요청에 로그인 페이지를 보여준다.
- [ ] GET /register 요청에 회원가입 페이지를 보여준다.
- [ ] POST /register , body를 포함한 요청에 회원가입을 시키고 login 페이지로 redirect 시킨다.
- [ ] POST /login 요청에 로그인 처리를 한다.
   - [ ]  서버에서 세션을 생성해 로그인 정보를 저장한다.
   - [ ]  쿠키에 JSESSION 아이디를 담아서 로그인을 유지시킨다.
- [ ] 로그인 처리가 된 사용자에게는 index.html 페이지를 보여준다.
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
