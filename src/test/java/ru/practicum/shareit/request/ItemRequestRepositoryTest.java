package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    private final ItemRequest requestOne = new ItemRequest();
    private final ItemRequest requestTwo = new ItemRequest();

    private final ItemRequest requestThree = new ItemRequest();
    List<ItemRequest> requestsPersist = new ArrayList<>();

    @Test
    void findByRequestorOrderByCreatedDesc() {
        addRequestOne();
        em.persist(requestOne);
        addRequestTwo();
        em.persist(requestTwo);
        requestsPersist.add(requestTwo);
        requestsPersist.add(requestOne);
        List<ItemRequest> requests = requestRepository.findByRequestorOrderByCreatedDesc(requestOne.getRequestor());
        assertThat(requestsPersist.size()).isEqualTo(requests.size());
        assertThat(requestsPersist.get(0).getId()).isEqualTo(requests.get(0).getId());
    }

    @Test
    void findAllBy() {
        Pageable pageable = PageRequest.of(0, 1);
        addRequestOne();
        em.persist(requestOne);
        addRequestTwo();
        em.persist(requestTwo);
        addRequestThree();
        em.persist(requestThree);
        requestsPersist.add(requestThree);
        Page<ItemRequest> requestsPage = requestRepository.findAllBy(2L, pageable);
        List<ItemRequest> requests = requestsPage.getContent();
        assertThat(requestsPersist.size()).isEqualTo(requests.size());
        assertThat(requestsPersist.get(0).getId()).isEqualTo(requests.get(0).getId());
    }

    private void addRequestOne() {
        User requestor = new User();
        requestor.setId(2);
        requestor.setName("Kat");
        requestor.setEmail("Kat@kat.com");
        requestOne.setRequestor(requestor);
        requestOne.setDescription("I need a fork to eat");
        requestOne.setCreated(LocalDateTime.now());
    }

    private void addRequestTwo() {
        User requestor = new User();
        requestor.setId(2);
        requestor.setName("Kat");
        requestor.setEmail("Kat@kat.com");
        requestTwo.setRequestor(requestor);
        requestTwo.setDescription("I need a fork to eat");
        requestTwo.setCreated(LocalDateTime.now());
    }
    private void addRequestThree() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("Kat");
        requestor.setEmail("Kat@kat.com");
        requestThree.setRequestor(requestor);
        requestThree.setDescription("I need a fork to eat");
        requestThree.setCreated(LocalDateTime.now());
    }

}