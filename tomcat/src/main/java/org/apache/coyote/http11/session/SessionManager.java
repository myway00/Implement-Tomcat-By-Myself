package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.ToString;

@ToString
public class SessionManager {

    // ConcurrentHashMap : 동시 접속 시 멀티쓰레드 환경에서 경쟁상태가 발생 -> 세션 데이터의 정합성 이슈 방지
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Session create() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SESSIONS.put(sessionId, session);
        return session;
    }

    public static Optional<Session> findSession(final String sessionId) {
        return Optional.ofNullable(SESSIONS.get(sessionId));
    }

    public void remove(final String sessionId) {
        SESSIONS.remove(sessionId);
    }
}