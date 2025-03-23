/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package guru.springframework.brewery.events;

import guru.springframework.brewery.domain.BeerOrder;
import guru.springframework.brewery.web.mappers.DateMapper;
import guru.springframework.brewery.web.model.OrderStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BeerOrderStatusChangeEventListener {
    private final RestTemplate restTemplate;
    private final DateMapper dateMapper = new DateMapper();

    public BeerOrderStatusChangeEventListener(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Async
    @EventListener
    public void listen(BeerOrderStatusChangeEvent event) {
        System.out.println("I got an order status change event");
        System.out.println(event);

        BeerOrder order = event.getBeerOrder();

        OrderStatusUpdate update = OrderStatusUpdate.builder()
                .id(order.getId())
                .version(order.getVersion() != null ? order.getVersion().intValue() : null)
                .createdDate(dateMapper.asOffsetDateTime(order.getCreatedDate()))
                .lastModifiedDate(dateMapper.asOffsetDateTime(order.getLastModifiedDate()))
                .orderStatus(event.getPreviousStatus().toString())
                .customerRef(order.getCustomerRef())
                .build();

        try {
            log.debug("Posting to callback url");
            restTemplate.postForObject(order.getOrderStatusCallbackUrl(), update, String.class);
        } catch (Exception e) {
            log.error("Error preforming ");
        }
    }
}
