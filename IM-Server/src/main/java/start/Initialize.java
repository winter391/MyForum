package start;

import Code.RedisCode;
import Code.RedissonCode;
import Code.ServerId;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import Exception.GlobalException;
import java.util.concurrent.TimeUnit;

public class Initialize implements ApplicationRunner
{
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        RLock lock = redissonClient.getLock(RedissonCode.LOCK_SERVER_ID);
        boolean isLock = lock.tryLock(RedissonCode.WAITING_TIME, TimeUnit.SECONDS);
        if(isLock)
        {
            ServerId.id = redisTemplate.opsForValue().increment(RedisCode.SERVER_ID);
            lock.unlock();
        }
        else
        {
            throw new GlobalException("初始化错误，无法获得redisson锁");
        }
    }
}
