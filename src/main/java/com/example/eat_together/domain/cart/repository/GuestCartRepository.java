package com.example.eat_together.domain.cart.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;

/**
 * 비회원(게스트) 장바구니 데이터를 Redis에 저장하고 조회하는 저장소
 */
@Repository
@RequiredArgsConstructor
public class GuestCartRepository {

    private static final Duration TTL = Duration.ofDays(14);
    private final StringRedisTemplate redis;

    private String keyStore(Long guestIdHash) {
        return "guest:cart:%d:store".formatted(guestIdHash);
    }

    private String keyItems(Long guestIdHash) {
        return "guest:cart:%d:items".formatted(guestIdHash);
    }

    /**
     * 매장 ID 저장
     *
     * @param guestIdHash 게스트 ID 해시값
     * @param storeId     매장 ID
     */
    public void setStore(Long guestIdHash, Long storeId) {
        redis.opsForValue().set(keyStore(guestIdHash), String.valueOf(storeId), TTL);
    }

    /**
     * 매장 ID 조회
     *
     * @param guestIdHash 게스트 ID 해시값
     * @return 매장 ID 또는 null
     */
    public Long getStore(Long guestIdHash) {
        String v = redis.opsForValue().get(keyStore(guestIdHash));
        return (v == null) ? null : Long.valueOf(v);
    }

    /**
     * 장바구니 항목 저장 또는 수정
     *
     * @param guestIdHash 게스트 ID 해시값
     * @param menuId      메뉴 ID
     * @param quantity    수량
     */
    public void putItem(Long guestIdHash, Long menuId, int quantity) {
        String itemsKey = keyItems(guestIdHash);
        redis.opsForHash().put(itemsKey, String.valueOf(menuId), String.valueOf(quantity));
        redis.expire(itemsKey, TTL);
        redis.expire(keyStore(guestIdHash), TTL);
    }

    /**
     * 장바구니 항목 삭제
     *
     * @param guestIdHash 게스트 ID 해시값
     * @param menuId      메뉴 ID
     */
    public void removeItem(Long guestIdHash, Long menuId) {
        String itemsKey = keyItems(guestIdHash);
        redis.opsForHash().delete(itemsKey, String.valueOf(menuId));
        redis.expire(itemsKey, TTL);
        redis.expire(keyStore(guestIdHash), TTL);
    }

    /**
     * 장바구니 항목 전체 조회
     *
     * @param guestIdHash 게스트 ID 해시값
     * @return 메뉴 ID-수량 쌍의 맵
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getAllItems(Long guestIdHash) {
        return (Map<String, String>) (Map<?, ?>) redis.opsForHash().entries(keyItems(guestIdHash));
    }

    /**
     * 장바구니 전체 삭제
     *
     * @param guestIdHash 게스트 ID 해시값
     */
    public void deleteCart(Long guestIdHash) {
        redis.delete(keyStore(guestIdHash));
        redis.delete(keyItems(guestIdHash));
    }
}