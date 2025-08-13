package com.example.eat_together.global.redis.service;

import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.example.eat_together.domain.order.repository.OrderRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LockService {

    private final RedissonClient redissonClient;
    private final OrderRepository orderRepository;

    public LockService(RedissonClient redissonClient, OrderRepository orderRepository) {
        this.redissonClient = redissonClient;
        this.orderRepository = orderRepository;
    }

    // 개별 주문 락
    public void executeWithLockForOrder(Long userId, Long storeId, Runnable task) {
        String lockKey = "order_lock:" + userId + ":" + storeId;
        executeWithLock(lockKey, 10, 30, () -> {
            // 락 내부에서 중복 주문 체크
            boolean exists = orderRepository.existsByUserUserIdAndStoreStoreIdAndStatus(
                    userId, storeId, OrderStatus.ORDERED);
            if (exists) {
                throw new CustomException(ErrorCode.DUPLICATE_ORDER);
            }
            task.run();
        });
    }

    // 공유 주문 락
    public void executeWithLockForSharedOrder(Long chatRoomId, Store store, List<User> participants, Runnable task) {
        String lockKey = "shared_order_lock:" + chatRoomId;
        executeWithLock(lockKey, 10, 60, () -> {
            // 락 내부에서 중복 주문 체크
            for (User participant : participants) {
                boolean exists = orderRepository.existsByUserUserIdAndStoreStoreIdAndStatus(
                        participant.getUserId(), store.getStoreId(), OrderStatus.ORDERED);
                if (exists) {
                    throw new CustomException(ErrorCode.DUPLICATE_ORDER);
                }
            }
            task.run();
        });
    }

    // 락을 유지하면서 주문
    public void executeWithLock(String lockKey, int waitTime, int leaseTime, Runnable task) {
        // Redisson 분산 락 객체 생성
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            // 락 획득 시도 최대 10초, 락을 획득하면 30/60초(개별, 공유) 동안 유지
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            // 락을 얻지 못하면 중복 주문
            if (!isLocked) {
                throw new CustomException(ErrorCode.DUPLICATE_ORDER);
            }
            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // 락 해제
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
