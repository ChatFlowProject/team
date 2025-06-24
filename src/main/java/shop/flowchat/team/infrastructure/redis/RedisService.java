package shop.flowchat.team.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import shop.flowchat.team.common.exception.custom.InternalServiceException;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public <T> void create(String key, T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    public <T> T read(String key, Class<T> type) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public <T> T read(String key, TypeReference<T> type) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        String json = bucket.get();
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new InternalServiceException("Failed to deserialize value for key: " + key);
        }
    }

    public <T> void update(String key, T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }
}