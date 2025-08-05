package com.example.eat_together.domain.chat.repository;

import com.example.eat_together.domain.chat.chatEnum.FoodType;
import com.example.eat_together.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


    @Query("""
                SELECT cr
                FROM ChatRoom cr
                JOIN FETCH cr.chatGroup cg
                JOIN FETCH cr.chatRoomUserList crul
                JOIN FETCH crul.user u
                WHERE (:foodType IS NULL AND :keyWord IS NULL)
                    OR (:foodType IS NOT NULL AND :keyWord IS NULL
                        AND cg.foodType = :foodType)
                    OR (:foodType IS NULL AND :keyWord IS NOT NULL
                        AND (cg.title LIKE CONCAT('%', :keyWord, '%')
                            OR cg.description LIKE CONCAT('%', :keyWord, '%')
                        )
                    )
                    OR (:foodType IS NOT NULL AND :keyWord IS NOT NULL
                        AND (cg.foodType = :foodType
                            OR cg.title LIKE %:keyWord%
                            OR cg.description LIKE CONCAT('%', :keyWord, '%')
                        )
                    )
                ORDER BY cg.updatedAt DESC
            """)
    List<ChatRoom> findAll(@Param("foodType") FoodType foodType, @Param("keyWord") String keyWord);
}