package com.example.eat_together.domain.order.repository;

import com.example.eat_together.domain.order.dto.OrderResponseDto;
import com.example.eat_together.domain.order.dto.QOrderResponseDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.eat_together.domain.order.entity.QOrder.order;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public OrderRepositoryCustomImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<OrderResponseDto> findOrders(Pageable pageable) {
        List<OrderResponseDto> content = queryFactory.select(new QOrderResponseDto(order))
                .from(order)
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(order.count())
                .from(order);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
