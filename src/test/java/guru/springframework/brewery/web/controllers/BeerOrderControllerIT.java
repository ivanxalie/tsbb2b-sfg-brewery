package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerIT {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private CustomerRepository repository;

    @Test
    void listOrders() {
        List<Customer> customers = repository.findAll();
        BeerOrderPagedList list = template.getForObject("/api/v1/customers/" +
                customers.get(0).getId() + "/orders", BeerOrderPagedList.class);

        assertThat(list).isNotNull();
        assertThat(list.getContent()).isNotNull().hasSize(1);
    }
}