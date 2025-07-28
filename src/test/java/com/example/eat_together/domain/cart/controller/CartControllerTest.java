package com.example.eat_together.domain.cart.controller;

import com.example.eat_together.domain.cart.dto.request.CartItemRequestDto;
import com.example.eat_together.domain.cart.dto.response.CartResponseDto;
import com.example.eat_together.domain.cart.service.CartService;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @Nested
    @DisplayName("장바구니 추가 성공 케이스")
    class AddSuccess {

        @Test
        @DisplayName("장바구니에 메뉴 추가 성공")
        @WithMockUser(username = "1")
        void addCartItem_success() throws Exception {
            // given
            CartItemRequestDto request = new CartItemRequestDto();
            request.setMenuId(1L);
            request.setQuantity(2);

            // when & then
            mockMvc.perform(post("/stores/10/carts/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("장바구니에 메뉴가 추가되었습니다."));
        }
    }

    @Nested
    @DisplayName("장바구니 추가 실패 케이스")
    class AddFailure {

        @Test
        @DisplayName("장바구니 최대 수량 초과")
        @WithMockUser(username = "1")
        void addCartItem_fail_exceedsMaxQuantity() throws Exception {
            // given
            CartItemRequestDto request = new CartItemRequestDto();
            request.setMenuId(1L);
            request.setQuantity(100);

            willThrow(new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY))
                    .given(cartService).addItem(eq(1L), eq(10L), any());

            // when & then
            mockMvc.perform(post("/stores/10/carts/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("수량은 최대 99개까지 담을 수 있습니다."));
        }

        @Test
        @DisplayName("기존 장바구니와 다른 매장의 메뉴 추가")
        @WithMockUser(username = "1")
        void addCartItem_fail_invalidStore() throws Exception {
            // given
            CartItemRequestDto request = new CartItemRequestDto();
            request.setMenuId(1L);
            request.setQuantity(1);

            willThrow(new CustomException(ErrorCode.CART_INVALID_STORE))
                    .given(cartService).addItem(eq(1L), eq(10L), any());

            // when & then
            mockMvc.perform(post("/stores/10/carts/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("기존 장바구니와 다른 매장의 메뉴는 담을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("장바구니 조회 성공 케이스")
    class ReadSuccess {

        @Test
        @DisplayName("장바구니 조회 성공")
        @WithMockUser(username = "1")
        void getCart_success() throws Exception {
            // given
            CartResponseDto dummyResponse = new CartResponseDto(
                    10L,
                    List.of(),
                    3000.0
            );

            given(cartService.getCart(1L)).willReturn(dummyResponse);

            // when & then
            mockMvc.perform(get("/carts/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("장바구니를 조회했습니다."));
        }
    }

    @Nested
    @DisplayName("장바구니 조회 실패 케이스")
    class ReadFailure {

        @Test
        @DisplayName("장바구니 조회 실패 - 존재하지 않음")
        @WithMockUser(username = "1")
        void getCart_fail_notFound() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.CART_NOT_FOUND))
                    .given(cartService).getCart(eq(1L));

            // when & then
            mockMvc.perform(get("/carts/me"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("해당 장바구니를 찾을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("장바구니 수정 성공 케이스")
    class UpdateSuccess {

        @Test
        @DisplayName("장바구니 수량 수정 성공")
        @WithMockUser(username = "1")
        void updateCartItem_success() throws Exception {
            // given
            CartItemRequestDto request = new CartItemRequestDto();
            request.setMenuId(1L);
            request.setQuantity(5);

            // when & then
            mockMvc.perform(patch("/carts/items/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("수량이 수정되었습니다."));
        }
    }

    @Nested
    @DisplayName("장바구니 수정 실패 케이스")
    class UpdateFailure {

        @Test
        @DisplayName("장바구니 수량 수정 실패 - 초과")
        @WithMockUser(username = "1")
        void updateCartItem_fail_exceedsMaxQuantity() throws Exception {
            // given
            CartItemRequestDto request = new CartItemRequestDto();
            request.setQuantity(150);

            willThrow(new CustomException(ErrorCode.CART_EXCEEDS_MAX_QUANTITY))
                    .given(cartService).updateQuantity(eq(1L), any());

            // when & then
            mockMvc.perform(patch("/carts/items/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("수량은 최대 99개까지 담을 수 있습니다."));
        }
    }

    @Nested
    @DisplayName("장바구니 삭제 성공 케이스")
    class DeleteSuccess {

        @Test
        @DisplayName("장바구니 아이템 삭제 성공")
        @WithMockUser(username = "1")
        void deleteCartItem_success() throws Exception {
            // when & then
            mockMvc.perform(delete("/carts/items/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("장바구니에서 항목이 삭제되었습니다."));
        }
    }

    @Nested
    @DisplayName("장바구니 삭제 실패 케이스")
    class DeleteFailure {

        @Test
        @DisplayName("장바구니 아이템 삭제 실패 - 존재하지 않음")
        @WithMockUser(username = "1")
        void deleteCartItem_fail_notFound() throws Exception {
            // given
            willThrow(new CustomException(ErrorCode.CART_ITEM_NOT_FOUND))
                    .given(cartService).deleteItem(eq(1L));

            // when & then
            mockMvc.perform(delete("/carts/items/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("아이템을 찾을 수 없습니다."));
        }
    }
}