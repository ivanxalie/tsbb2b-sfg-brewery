package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerControllerIT {
    @Autowired
    private TestRestTemplate template;

    @Test
    void listBeers() {
        BeerPagedList list = template.getForObject("/api/v1/beer", BeerPagedList.class);

        assertThat(list.getContent()).hasSize(3);
    }
}
