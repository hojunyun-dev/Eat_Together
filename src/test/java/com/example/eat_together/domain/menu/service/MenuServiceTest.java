package com.example.eat_together.domain.menu.service;

import com.example.eat_together.domain.menu.dto.request.MenuRequestDto;
import com.example.eat_together.domain.menu.dto.respones.MenuResponseDto;
import com.example.eat_together.domain.menu.dto.respones.PagingMenuResponseDto;
import com.example.eat_together.domain.menu.entity.Menu;
import com.example.eat_together.domain.menu.fixture.MenuTestDtoFixture;
import com.example.eat_together.domain.menu.fixture.MenuTestFixture;
import com.example.eat_together.domain.menu.repository.MenuRepository;
import com.example.eat_together.domain.store.entity.Store;
import com.example.eat_together.domain.store.fixture.StoreTestFixture;
import com.example.eat_together.domain.store.repository.StoreRepository;
import com.example.eat_together.domain.users.common.entity.User;
import com.example.eat_together.domain.users.user.repository.UserRepository;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.fixture.UserTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service:Menu")
public class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;


    // 캐시 접근을 위한 Mock 객체들
    @Mock
    private RedisTemplate<String, PagingMenuResponseDto> pagingMenuRedisTemplate;
    @Mock
    private ValueOperations<String, PagingMenuResponseDto> pagingValueOperations;

    // 캐시 접근을 위한 Mock 객체들
    @Mock
    private RedisTemplate<String, MenuResponseDto> menuRedisTemplate;
    @Mock
    private ValueOperations<String, MenuResponseDto> valueOperations;

    User user;
    Store store;
    Menu menu;
    UserDetails userDetails;

    @BeforeEach
    void set() {
        user = UserTestFixture.유저_생성(1L);
        store = StoreTestFixture.매장_생성(user);
        menu = MenuTestFixture.메뉴_생성(store);

        userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn("1");

        // Redis 호출 시 반환할 Mock 객체 설정, 해당 객체가 쓰이지 않더라도 테스트 통과
        lenient().when(pagingMenuRedisTemplate.opsForValue()).thenReturn(pagingValueOperations);
        lenient().when(menuRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test()
    @DisplayName("메뉴_생성")
    void 메뉴_등록_성공() {

        // given
        // 요청 값 생성
        MenuRequestDto requestDto = MenuTestDtoFixture.requestDtoMock();

        // 유저 정보 세팅
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

    @Test()
    @DisplayName("매장_메뉴_조회")
    void 매장_메뉴_조회() {

        // given
        // 페이징 설정
        Pageable pageable = PageRequest.of(0, 10);

        // 메뉴 리스트 생성
        Menu menu1 = MenuTestFixture.리스트용_메뉴_생성(store, "촉촉한 초코칩");
        Menu menu2 = MenuTestFixture.리스트용_메뉴_생성(store, "안촉촉한 초코칩");
        Menu menu3 = MenuTestFixture.리스트용_메뉴_생성(store, "바삭한 초코칩");
        Menu menu4 = MenuTestFixture.리스트용_메뉴_생성(store, "딱딱한 초코칩");
        List<Menu> menuList = List.of(menu1, menu2, menu3, menu4);

        // 캐시 관련 Mock 객체
        when(pagingValueOperations.get("storeMenus:1")).thenReturn(null);

        // 매장 정보 세팅
        when(storeRepository.findByStoreIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(store));

        // 페이징 정보 세팅
        when(menuRepository.findAllByStoreAndIsDeletedFalse(store, pageable)).thenReturn(new PageImpl<>(menuList));

        // when
        // getMenusByStoreId메서드 호출
        PagingMenuResponseDto responseDto = menuService.getMenusByStoreId(1L, pageable);

        // then
        // 값이 들어있는지 검증
        assertThat(responseDto).isNotNull();

        // 입력한 메뉴와 갯수가 일치하는지 검증       메뉴 4개 넣었으므로 리스트의 사이즈가 4가 맞는지 검증
        assertThat(responseDto.getMenuList().size()).isEqualTo(4);

        // 리스트의 첫번째 인자가 menu1이 맞는지 검증
        assertThat(responseDto.getMenuList().get(0).getName()).isEqualTo("촉촉한 초코칩");
    }

    @Test()
    @DisplayName("매장_메뉴_단건_조회")
    void 매장_메뉴_단건_조회() {

        // given
        // 캐시 관련 Mock 객체
        when(valueOperations.get("menu:1")).thenReturn(null);

        // 매장 정보 설정
        when(storeRepository.findByStoreIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(store));

        // 메뉴 정보 설정
        when(menuRepository.findByMenuIdAndStore(1L, store)).thenReturn(menu);

        // when
        // getMenuByStore메서드 호출
        MenuResponseDto menuByStore = menuService.getMenuByStore(1L, 1L);

        // then
        // 조회한 메뉴의 값이 들어있는지 검증
        assertThat(menuByStore).isNotNull();

        // 조회한 메뉴의 이름이 저장된 이름이 맞는지 검증        현재 Fixture 클래스에 테스트용 이름으로 저장
        assertThat(menuByStore.getName()).isEqualTo("테스트용 이름");

        // 조회한 메뉴의 가격이 저장된 메뉴의 가격과 일치하는지 검증
        assertThat(menuByStore.getPrice()).isEqualTo(menu.getPrice());
    }

    @Test()
    @DisplayName("메뉴_수정")
    void 메뉴_수정() {

        // given
        // 유저, 매장, 메뉴 설정
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(storeRepository.findByStoreIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findByMenuIdAndStore(1L, store)).thenReturn(menu);

        // when
        MenuResponseDto responseDto =
                menuService.updateMenu
                        (
                                1L,
                                1L,
                                MenuTestDtoFixture.updateRequestDtoMock(), // 수정 정보 Dto
                                userDetails
                        );

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getName()).isEqualTo("수정된 테스트용 이름");
        assertThat(responseDto.getPrice()).isEqualTo(4500.0);
        assertThat(responseDto.getDescription()).isEqualTo("수정된 테스트용 메뉴 소개");

    }

    @Test()
    @DisplayName("메뉴_삭제")
    void 메뉴_삭제() {

        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(storeRepository.findByStoreIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findByMenuIdAndStore(1L, store)).thenReturn(menu);

        // when
        menuService.deleteMenu(1L, 1L, userDetails);

        // then
        assertThat(menu.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("User 예외 발생")
    void 유저를_찾을_수_없어_예외_발생() {

        // given
        MenuRequestDto requestDto = MenuTestDtoFixture.requestDtoMock();

        // 유저 조회 시 null 반환
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        // Menu 생성 시 예외
        CustomException menuCreateException = assertThrows(CustomException.class, () ->
        {
            menuService.createMenu(1L, requestDto, userDetails);
        });

        // Menu 수정 시 예외
        CustomException menuUpdateException = assertThrows(CustomException.class, () ->
        {
            menuService.updateMenu(1L, 1L, MenuTestDtoFixture.updateRequestDtoMock(), userDetails);
        });

        // Menu 삭제 시 예외
        CustomException menuDeleteException = assertThrows(CustomException.class, () ->
        {
            menuService.deleteMenu(1L, 1L, userDetails);
        });

        // then
        // Menu 생성 시 예외가 발생하는지 검증
        assertThat(menuCreateException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        // Menu 수정 시 예외가 발생하는지 검증
        assertThat(menuUpdateException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        // Menu 삭제 시 예외가 발생하는지 검증
        assertThat(menuDeleteException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }


    @Test
    @DisplayName("Store 예외 발생")
    void 매장을_찾을_수_없어_예외_발생() {

        // given
        MenuRequestDto requestDto = MenuTestDtoFixture.requestDtoMock();

        // 유저 조회
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 매장 조회 시 null 반환
        when(storeRepository.findByStoreIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        // when
        // 메뉴 생성 시 예외
        CustomException menuCreateException = assertThrows(CustomException.class, () ->
        {
            menuService.createMenu(1L, requestDto, userDetails);
        });

        // 메뉴 목록 조회 시 예외
        CustomException findMenusException = assertThrows(CustomException.class, () ->
        {
            menuService.getMenusByStoreId(1L, PageRequest.of(0, 10));
        });

        // 메뉴 단건 조회 시 예외
        CustomException findMenuException = assertThrows(CustomException.class, () ->
        {
            menuService.getMenuByStore(1L, 1L);
        });

        // 메뉴 수정 시 예외
        CustomException menuUpdateException = assertThrows(CustomException.class, () ->
        {
            menuService.updateMenu(1L, 1L, MenuTestDtoFixture.updateRequestDtoMock(), userDetails);
        });

        // 메뉴 삭제 시 예외
        CustomException menuDeleteException = assertThrows(CustomException.class, () ->
        {
            menuService.deleteMenu(1L, 1L, userDetails);
        });


        // then
        // 메뉴 생성 시 예외가 발생하는지 검증
        assertThat(menuCreateException.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
        // 메뉴 목록 조회 시 예외가 발생하는지 검증
        assertThat(findMenusException.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
        // 메뉴 단건 조회 시 예외가 발생하는지 검증
        assertThat(findMenuException.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
        // 메뉴 수정 시 예외가 발생하는지 검증
        assertThat(menuUpdateException.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
        // 메뉴 삭제 시 예외가 발생하는지 검증
        assertThat(menuDeleteException.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
    }
}
