package com.example.eat_together.domain.order.repository;

import com.example.eat_together.domain.order.dto.response.OrderResponseDto;
import com.example.eat_together.domain.order.dto.response.QOrderResponseDto;
import com.example.eat_together.domain.order.entity.Order;
import com.example.eat_together.domain.order.orderEnum.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.eat_together.domain.order.entity.QOrder.order;
import static com.example.eat_together.domain.order.entity.QOrderItem.orderItem;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public OrderRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * findOrdersByUserId에 쓰이는 메서드 모음
     * dateCheck  : startDate 와 endDate 가 모두 존재하면 해당 기간 조건을 반환, 없으면 null
     * statusCheck: status 값이 존재하면 해당 상태 조건을 반환, 없으면 null
     * searchOrder: 삭제 여부, 사용자 id, 기간 조건, 상태 조건을 모두 합쳐서 반환
     */
    private BooleanExpression dateCheck(LocalDate startDate, LocalDate endDate) {
        return (startDate != null && endDate != null) ?
                order.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)) : null;
    }

    private BooleanExpression statusCheck(OrderStatus status) {
        return status != null ? order.status.eq(status) : null;
    }

    private BooleanExpression menuNameCheck(String menuName) {
        return (menuName != null && !menuName.isBlank()) ? order.orderItems.any().menu.name.containsIgnoreCase(menuName) : null;
    }

    private BooleanExpression storeNameCheck(String storeName) {
        return (storeName != null && !storeName.isBlank()) ? order.store.name.containsIgnoreCase(storeName) : null;
    }

    private BooleanExpression searchOrder(Long userId, String menuName, String storeName, LocalDate startDate, LocalDate endDate, OrderStatus status) {
        return order.isDeleted.eq(false)
                .and(order.user.userId.eq(userId))
                .and(menuNameCheck(menuName))
                .and(storeNameCheck(storeName))
                .and(dateCheck(startDate, endDate))
                .and(statusCheck(status));
    }

    @Override
    public Page<OrderResponseDto> findOrdersByUserId(Long userId, Pageable pageable, String menuName, String storeName, LocalDate startDate, LocalDate endDate, OrderStatus status) {

        // order id 먼저 조회
        List<Long> idList = queryFactory
                .select(order.id)
                .from(order)
                .where(searchOrder(userId, menuName, storeName, startDate, endDate, status))
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // idList가 비어있는 경우 빈 페이지 반환
        if (idList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // id로 실제 데이터 조회
        List<OrderResponseDto> content = queryFactory.select(new QOrderResponseDto(order))
                .from(order)
                .join(order.store).fetchJoin() // 주문과 가게 정보를 한 번에 조회
                .where(order.id.in(idList))
                .orderBy(order.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(order.count())
                .from(order)
                .where(searchOrder(userId, menuName, storeName, startDate, endDate, status));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    @Override
    public Optional<Order> findByIdAndUserId(Long orderId, Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(order)
                .join(order.store).fetchJoin() // 주문과 가게 정보를 한 번에 조회
                .leftJoin(order.orderItems, orderItem).fetchJoin() // 주문과 주문아이템을 한 번에 조회
                .leftJoin(orderItem.menu).fetchJoin() // 주문아이템과 메뉴를 한 번에 조회
                .where(order.isDeleted.eq(false),
                        order.user.userId.eq(userId),
                        order.id.eq(orderId))
                .fetchOne());
    }

    @Override
    public List<Order> findByUserIdAndStoreIdAndStatus(Long userId, Long storeId, OrderStatus status) {
        return queryFactory.selectFrom(order)
                .where(order.user.userId.eq(userId),
                        order.store.storeId.eq(storeId),
                        order.status.eq(status))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)  // JPA 비관적 락 설정
                .fetch();
    }
}
