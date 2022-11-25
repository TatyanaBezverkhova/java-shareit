package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User();

    @Test
    public void saveUserJpaTest() {
        addUser();
        User user1 = em.persist(user);
        User user2 = userRepository.save(user);
        assertThat(user2.getName()).isEqualTo(user1.getName());
        assertThat(user2.getId()).isEqualTo(user1.getId());
        assertThat(user2.getEmail()).isEqualTo(user1.getEmail());

    }

    @Test
    public void updateUserJpaTest() {
        addUser();
        em.persist(user);
        Assertions.assertNotNull(user.getId());
        user.setName("Katya");
        User user2 = userRepository.save(user);
        assertThat(user2.getName()).isEqualTo(user.getName());

    }

    @Test
    public void deleteUserJpaTest() {
        addUser();
        User user1 = em.persist(user);
        Assertions.assertNotNull(user1.getId());
        Optional<User> user2 = userRepository.findById(user1.getId());
        assertThat(user1).isEqualTo(user2.get());
        userRepository.deleteById(user1.getId());
        Assertions.assertEquals(Optional.empty(), userRepository.findById(user1.getId()));

    }

    @Test
    public void getAllUsersJpaTest() {
        addUser();
        User user1 = new User();
        user1.setName("Katya");
        user1.setEmail("katya@katya.com");
        User userPersist1 = em.persist(user);
        User userPersist2 = em.persist(user1);
        List<User> users = userRepository.findAll();
        assertThat(userPersist1).isEqualTo(users.get(0));
        assertThat(userPersist2).isEqualTo(users.get(1));
    }

    @Test
    public void getUserJpaTest() {
        addUser();
        em.persist(user);
        assertThat(user).isEqualTo(userRepository.findById(user.getId()).get());
    }

    private void addUser() {
        user.setName("Leo");
        user.setEmail("leo@angel.com");
    }

}