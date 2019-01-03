package br.com.investigate.progress.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

public class SafeDeserializationRepository<S extends Session> implements SessionRepository<S> {
    private final SessionRepository<S> delegate;
    private final RedisTemplate<Object, Object> redisTemplate;
    private String boundHashKeyPrefix;

    private static final Logger logger = LoggerFactory.getLogger(SafeDeserializationRepository.class);

    public SafeDeserializationRepository(SessionRepository<S> delegate,
                                         RedisTemplate<Object, Object> redisTemplate,
                                         String redisNamespace) {
        this.delegate = delegate;
        this.redisTemplate = redisTemplate;
        this.boundHashKeyPrefix = redisNamespace + ":sessions";
    }

    @Override
    public S createSession() {
        S session = null;
        try {
            session = delegate.createSession();
        } catch (Exception e) {
            logger.error("Error on create session", e);
        }
        return session;
    }

    @Override
    public void save(S session) {
        try {
            delegate.save(session);
        } catch (Exception e) {
            logger.error("Error on save session", e);
            throw e;
        }
    }

    @Override
    public S findById(String id) {
        try {
            return delegate.findById(id);
        } catch (SerializationException e) {
            logger.warn("Deleting non-deserializable session with key {}", id, e);
            redisTemplate.delete(boundHashKeyPrefix + id);
            return null;
        }
    }

    @Override
    public void deleteById(String sessionId) {
        delegate.deleteById(sessionId);
    }
}