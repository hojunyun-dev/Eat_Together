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
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.eat_together.domain.order.entity.QOrder.order;

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

    private BooleanExpression searchOrder(Long userId, LocalDate startDate, LocalDate endDate, OrderStatus status) {
        return order.isDeleted.eq(false)
                .and(order.user.userId.eq(userId))
                .and(dateCheck(startDate, endDate))
                .and(statusCheck(status));
    }

    @Override
    public Page<OrderResponseDto> findOrdersByUserId(Long userId, Pageable pageable, LocalDate startDate, LocalDate endDate, OrderStatus status) {
        List<OrderResponseDto> content = queryFactory.select(new QOrderResponseDto(order))
                .from(order)
                .where(searchOrder(userId, startDate, endDate, status))
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(order.count())
                .from(order)
                .where(searchOrder(userId, startDate, endDate, status));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Order> findByIdAndUserId(Long orderId, Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(order)
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
