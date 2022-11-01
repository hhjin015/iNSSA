package com.inssa.backend.menu.controller;

import com.inssa.backend.ApiDocument;
import com.inssa.backend.common.domain.ErrorMessage;
import com.inssa.backend.common.domain.Message;
import com.inssa.backend.common.exception.NotFoundException;
import com.inssa.backend.menu.controller.dto.ItemsResponse;
import com.inssa.backend.menu.controller.dto.MenuRequest;
import com.inssa.backend.menu.controller.dto.MenuResponse;
import com.inssa.backend.menu.service.MenuService;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
public class MenuControllerTest extends ApiDocument {

    private static final Long ID = 1L;
    private static final List<String> ITEMS = Arrays.stream("코다리조림[명태:러시아산], 혼합잡곡밥, 비지찌개, 만두탕수, 상추겉절이, 포기김치".split(", ")).collect(Collectors.toList());
    private static final LocalDate DATE = LocalDate.parse("2022-10-27");
    private static final String DAY_OF_THE_WEEK = "목";
    private static final String START_DATE = "2022-10-31";
    private static final String END_DATE = "2022-11-04";

    @MockBean
    private MenuService menuService;

    private MenuRequest menuRequest;
    private MenuResponse menuResponse;

    private ItemsResponse itemsResponse;

    @BeforeEach
    void setUp() {
        List<ItemsResponse> itemsResponse = IntStream.range(0, 5)
                .mapToObj(n -> ItemsResponse.builder()
                        .date(DATE)
                        .dayOfTheWeek(DAY_OF_THE_WEEK)
                        .items(ITEMS)
                        .build())
                .collect(Collectors.toList());
        menuRequest = MenuRequest.builder()
                .items(ITEMS)
                .date(DATE)
                .dayOfTheWeek(DAY_OF_THE_WEEK)
                .build();
        menuResponse = MenuResponse.builder()
                .startDate(LocalDate.parse(START_DATE))
                .endDate(LocalDate.parse(END_DATE))
                .menus(itemsResponse)
                .build();
    }

    @DisplayName("식단 등록 성공")
    @Test
    void create_menu_success() throws Exception {
        // given
        willDoNothing().given(menuService).createMenu(anyLong(), any(MenuRequest.class));
        // when
        ResultActions resultActions = 식단_등록_요청(ID, menuRequest);
        // then
        식단_등록_성공(resultActions);
    }

    @DisplayName("식단 등록 실패")
    @Test
    void create_menu_fail() throws Exception {
        // given
        willThrow(new InternalException(ErrorMessage.FAIL_TO_CREATE_MENU.getMessage())).given(menuService).createMenu(anyLong(), any(MenuRequest.class));
        // when
        ResultActions resultActions = 식단_등록_요청(ID, menuRequest);
        // then
        식단_등록_실패(resultActions, new Message(ErrorMessage.FAIL_TO_CREATE_MENU));
    }

    @DisplayName("식단 조회 성공")
    @Test
    void get_menus_success() throws Exception {
        // given
        willReturn(menuResponse).given(menuService).getMenu();
        // when
        ResultActions resultActions = 식단_조회_요청();
        // then
        식단_조회_성공(resultActions);
    }

    @DisplayName("식단 조회 실패")
    @Test
    void get_menus_fail() throws Exception {
        // given
        willThrow(new NotFoundException(ErrorMessage.NOT_FOUND_MENU)).given(menuService).getMenu();
        // when
        ResultActions resultActions = 식단_조회_요청();
        // then
        식단_조회_실패(resultActions, new Message(ErrorMessage.NOT_FOUND_MENU));
    }

    private ResultActions 식단_등록_요청(Long userId, MenuRequest menuRequest) throws Exception {
        return mockMvc.perform(post("/api/v1/menus/users/" + userId)
                .contextPath("/api/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(menuRequest)));
    }

    private void 식단_등록_성공(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andDo(toDocument("create-menu-success"));
    }

    private void 식단_등록_실패(ResultActions resultActions, Message message) throws Exception {
        resultActions.andExpect(status().isInternalServerError())
                .andExpect(content().json(toJson(message)))
                .andDo(print())
                .andDo(toDocument("create-menu-fail"));
    }

    private ResultActions 식단_조회_요청() throws Exception {
        return mockMvc.perform(get("/api/v1/menus")
                .contextPath("/api/v1"));

    }

    private void 식단_조회_성공(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(menuResponse)))
                .andDo(print())
                .andDo(toDocument("get-menu-success"));
    }

    private void 식단_조회_실패(ResultActions resultActions, Message message) throws Exception {
        resultActions.andExpect(status().isNotFound())
                .andExpect(content().json(toJson(message)))
                .andDo(print())
                .andDo(toDocument("get-menu-fail"));
    }
}
