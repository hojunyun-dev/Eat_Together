package com.example.eat_together.domain.menu.service;

import com.example.eat_together.domain.menu.dto.request.MenuRequestDto;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.fixture.MenuTestDtoFixture;
import com.example.eat_together.domain.menu.fixture.MenuTestFixture;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.fixture.StoreTestFixture;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.user.entity.User;
import com.example.eat_together.domain.user.repository.UserRepository;
import com.example.eat_together.global.fixture.UserTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;


    User user;
    Store store;
    Menu menu;

    @BeforeEach
    void set() {
        user = UserTestFixture.유저_생성(1L);
        store = StoreTestFixture.매장_생성(user);
        menu = MenuTestFixture.메뉴_생성(store);
    }

    @Test()
    @DisplayName("메뉴_생성")
    void 메뉴_등록_성공() {

        // given
        // userDetails Mock 객체 생성
        UserDetails userDetails = mock(UserDetails.class);

        // 요청 값 생성
        MenuRequestDto requestDto = MenuTestDtoFixture.requestDtoMock();

        // 유저 정보 세팅
        when(userDetails.getUsername()).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 매장 정보 세팅
        when(storeRepository.findByStoreIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(store));

        // when
        // 메뉴 생성 서비스 호출
        menuService.createMenu(1L, requestDto, userDetails);

        // then
        // save 메서드가 한 번 실행됐는지 검증
        verify(menuRepository, times(1)).save(any(Menu.class));
    }


}
