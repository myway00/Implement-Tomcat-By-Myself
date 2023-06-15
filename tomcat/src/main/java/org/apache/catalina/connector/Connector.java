// 패키지 import
package org.apache.catalina.connector;

// IOException 및 UncheckedIOException을 위한 import
import java.io.IOException;
import java.io.UncheckedIOException;
// 네트워크 관련 클래스 import
import java.net.ServerSocket;
import java.net.Socket;
// 동시성 처리를 위한 ExecutorService 및 Executors import
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//  Tomcat의 Coyote 컴포넌트에 속하는 클래스로, HTTP 1.1 프로토콜을 처리하는 역할을 담당
//  클라이언트의 HTTP 요청을 처리하고, 해당 요청에 대한 응답을 생성
import org.apache.coyote.http11.Http11Processor;
// 로깅을 위한 import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Connector 클래스 선언 및 Runnable 인터페이스 구현
public class Connector implements Runnable {
    // 로깅을 위한 Logger 생성
    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    // 기본 포트 번호 및 접속 허용 수 설정
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    // 허용 가능한 최대 쓰레드 수 설정
    private static final int DEFAULT_MAX_THREADS = 250;

    private final ExecutorService executorService;
    private final ServerSocket serverSocket;
    private boolean stopped;

    // 기본 생성자
    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    // 생성자 오버로딩
    public Connector(final int port, final int acceptCount, final int maxThread) {
        // 고정 크기 스레드 풀 생성
        this.executorService = Executors.newFixedThreadPool(maxThread);
        // 서버 소켓 생성 및 포트 번호, 접속 허용 수 확인
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
    }

    // 서버 소켓 생성 메서드
    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            // 포트 번호와 접속 허용 수 확인
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            // ServerSocket 생성
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            // IOException 을 UncheckedIOException 으로 변환하여 예외 처리
            throw new UncheckedIOException(e);
        }
    }

    // 서버 시작 메서드
    public void start() {
        // 새로운 스레드 생성 및 실행
        var thread = new Thread(this);
        // 백그라운드 스레드로 설정
        thread.setDaemon(true);
        thread.start();
        stopped = false;
    }

    // Runnable 인터페이스의 run 메서드 구현
    @Override
    public void run() {
        // 서버가 정지되지 않은 동안 계속해서 접속 요청 처리
        while (!stopped) {
            connect();
        }
    }

    // 클라이언트의 접속 요청 처리 메서드
    private void connect() {
        try {
            // 클라이언트의 접속 요청을 받아들임
            process(serverSocket.accept());
        } catch (IOException e) {
            // IOException 로깅
            log.error(e.getMessage(), e);
        }
    }

    // 클라이언트 연결 처리 메서드
    private void process(final Socket connection) { //  클라이언트와의 소켓 연결
        if (connection == null) { // 매개변수 connection 이 null 인 경우 메서드를 종료하고 반환합니다.
            return;
        }
        // 클라이언트의 호스트와 포트 정보를 로깅
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        // Http11Processor 인스턴스 생성 *클라이언트의 요청을 처리하기 위한 처리기 역할
        var processor = new Http11Processor(connection);
        // ExecutorService :  executorService.submit(processor)를 사용하여 processor 를 처리 작업으로 제출합
        executorService.submit(processor);
        // => executorService 는 스레드 풀에서 사용 가능한 쓰레드를 가져와서 processor 를 실행하게 됩니다.
    }

    // 서버 정지 메서드
    public void stop() {
        stopped = true;
        try {
            // 서버 소켓 닫기
            serverSocket.close();
        } catch (IOException e) {
            // IOException 로깅
            log.error(e.getMessage(), e);
        }
    }

    // 포트 번호 확인 메서드
    private int checkPort(final int port) {
        // 포트 번호 범위 설정
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        // 포트 번호가 범위를 벗어나면 기본 포트 번호를 반환하고, 그렇지 않으면 입력된 포트 번호 반환
        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    // 접속 허용 수 확인 메서드
    private int checkAcceptCount(final int acceptCount) {
        // 접속 허용 수와 기본 접속 허용 수 중 큰 값을 반환
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}