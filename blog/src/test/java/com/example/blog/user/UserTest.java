package com.example.blog.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTest {


    @Nested
    @Transactional
    class TransactionTest {

        @Autowired
        private UserService userService;

        @Autowired
        private UserRepository userRepository;

        @MockitoBean
        private TransactionEventListener eventListener;

        @Test
        @Order(1)
        void 당근을_새로운_스레드_범위에서_저장한다() throws ExecutionException, InterruptedException {
            System.out.println("test thread name : " + Thread.currentThread().getName());
            CompletableFuture<User> saveUser = userService.saveCarrot();
            User user = saveUser.get();
        }

        @Order(2)
        @Test
        void 롤백_검증() {
            long userCount = userRepository.count();
            assertThat(userCount).isEqualTo(1);
            userRepository.deleteAll();
        }


//        @Test
//        @Order(1)
//        void 피망을_새로운_트랜잭션_범위에서_저장한다() {
//            userService.savePmang();
//        }
//
//        @Order(2)
//        @Test
//        void 롤백_검증() {
//            long userCount = userRepository.count();
//            assertThat(userCount).isEqualTo(0);
//        }

        @Test
        void 트랜잭션이_전파된다() {
            userService.method();
        }

        @Test
        void 트랜잭션_생명주기가_확장되어_커밋_이벤트_리스닝이_되지_않는다() {
            userService.publishEvent();

            Mockito.verify(eventListener, never()).onTransactionEvent(any());
        }

        @Test
        void 트랜잭션이_상위에서_열려_변경감지가_된다() {
            User user = new User("브로콜리");
            User savedUser = userRepository.save(user);

            userService.changeName(savedUser.getId(), "보더콜리");

            User changedUser = userRepository.findById(savedUser.getId())
                    .orElseThrow(RuntimeException::new);

            assertThat(changedUser.getName()).isEqualTo("보더콜리");
        }

    }


    @Nested
    class NoTransactionalTest {

        @Autowired
        private UserService userService;

        @Autowired
        private UserRepository userRepository;

        @MockitoBean
        private TransactionEventListener eventListener;

        @AfterEach
        void tearDown(){
            userRepository.deleteAll();
        }

        @Test
        @Order(1)
        void 당근을_새로운_스레드_범위에서_저장한다() throws ExecutionException, InterruptedException {
            System.out.println("test thread name : " + Thread.currentThread().getName());
            CompletableFuture<User> saveUser = userService.saveCarrot();
            User user = saveUser.get();
        }

        @Order(2)
        @Test
        void 롤백_검증() {
            long userCount = userRepository.count();
            assertThat(userCount).isEqualTo(0);
        }

//        @Test
//        @Order(1)
//        void 피망을_새로운_트랜잭션_범위에서_저장한다() {
//            userService.savePmang();
//        }
//
//        @Order(2)
//        @Test
//        void 롤백_검증() {
//            long userCount = userRepository.count();
//            assertThat(userCount).isEqualTo(0);
//        }

        @Test
        void 트랜잭션이_실제_운영환경과_동일하게_유지된다() {
            userService.method();
        }

        @Test
        void 트랜잭션_생명주기가_동일하여_커밋_이벤트_리스닝이_된다() {
            userService.publishEvent();

            Mockito.verify(eventListener, times(1)).onTransactionEvent(any());
        }

        @Test
        void 프로덕션코드의_트랜잭션을_반영한다() {
            User user = new User("브로콜리");
            User savedUser = userRepository.save(user);

            userService.changeName(savedUser.getId(), "보더콜리");

            User changedUser = userRepository.findById(savedUser.getId())
                    .orElseThrow(RuntimeException::new);

            assertThat(changedUser.getName()).isNotEqualTo("보더콜리");
        }
    }

}
