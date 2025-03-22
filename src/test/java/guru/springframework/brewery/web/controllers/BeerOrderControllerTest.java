package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {
    @MockBean
    private BeerOrderService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listOrders() throws Exception {
        List<BeerOrderDto> content = List.of(
                new BeerOrderDto(),
                new BeerOrderDto()
        );
        BeerOrderPagedList pagedList = new BeerOrderPagedList(content);
        given(service.listOrders(any(UUID.class), any(Pageable.class))).willReturn(pagedList);

        mockMvc.perform(get("/api/v1/customers/{customerId}/orders/", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.numberOfElements", is(2)))
                .andReturn();

        then(service).should().listOrders(any(UUID.class), any(Pageable.class));
    }

    @Test
    void getOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        BeerOrderDto beerOrderDto = new BeerOrderDto(
                orderId, 1, OffsetDateTime.now(), OffsetDateTime.now(), customerId, null,
                OrderStatusEnum.NEW, "https://google.com", null
        );

        given(service.getOrderById(customerId, orderId)).willReturn(beerOrderDto);

        mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}",
                        customerId, orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())))
                .andExpect(jsonPath("$.orderStatus", is(OrderStatusEnum.NEW.toString())))
                .andReturn();

        then(service).should().getOrderById(customerId, orderId);
    }
}