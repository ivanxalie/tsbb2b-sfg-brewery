package guru.springframework.brewery.events;

import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import guru.springframework.brewery.domain.BeerOrder;
import guru.springframework.brewery.domain.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@ExtendWith(WireMockExtension.class)
class BeerOrderStatusChangeEventListenerTest {

    @Managed
    private WireMockServer server = with(wireMockConfig().dynamicPort());

    private BeerOrderStatusChangeEventListener listener;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        listener = new BeerOrderStatusChangeEventListener(builder);
    }

    @Test
    void listen() {
        server.stubFor(post("/update").willReturn(ok()));

        BeerOrder beerOrder = BeerOrder.builder()
                .orderStatus(OrderStatusEnum.READY)
                .orderStatusCallbackUrl(String.format("http://localhost:%s/update", server.port()))
                .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        BeerOrderStatusChangeEvent event = new BeerOrderStatusChangeEvent(beerOrder, OrderStatusEnum.NEW);

        listener.listen(event);

        verify(1, postRequestedFor(urlEqualTo("/update")));
    }
}