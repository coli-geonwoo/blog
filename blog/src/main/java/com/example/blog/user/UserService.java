package com.example.blog.user;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void changeName(long id, String name) {
        User user = userRepository.findById(id)
                .orElse(null);

        user.setName(name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePmang() {
        userRepository.save(new User("피망"));
    }

    public CompletableFuture<User> saveCarrot() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("thread name : " + Thread.currentThread().getName());
            return userRepository.save(new User("당근"));
            }, Executors.newSingleThreadExecutor()
        );
    }

    public void method() {
        boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("트랜잭션 활성화 여부: :{}", isActive);
    }

    @Transactional
    public void publishEvent() {
        eventPublisher.publishEvent(new UserEvent(this));
    }
}
