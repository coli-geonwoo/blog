package com.example.blog.isolation;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.RunnableWrapper;
import com.example.blog.domain.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public class PhantomReadTest extends BaseTest {

    private MySQLContainer container;

    @BeforeEach
    void setUp() {
        container = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.30"))
                .withUrlParam("allowMultiQueries", "true")
                .withLogConsumer(new Slf4jLogConsumer(log));
        container.start();
        setUp(createMySQLDataSource(container));
    }

    @AfterEach
    void tearDown() throws SQLException {
        userDao.clear(dataSource.getConnection());
        container.close();
    }

    @DisplayName("다른 트랜잭션에서 insert문이 발생하면 팬텀 리딩이 발생한다")
    @ValueSource(ints = {
            Connection.TRANSACTION_READ_UNCOMMITTED,
            Connection.TRANSACTION_READ_COMMITTED,
            Connection.TRANSACTION_REPEATABLE_READ
    })
    @ParameterizedTest
    void phantom_reading(int isolationLevel) throws SQLException {
        userDao.insert(dataSource.getConnection(), new User("coli", "old", "email@email.com"));

        //커넥션 A 시작
        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false);
        connectionA.setTransactionIsolation(isolationLevel);

        // 사용자A가 id로 범위를 조회했다.
        List<User> firstRead = userDao.findGreaterThan(connectionA, 1);

        new Thread(RunnableWrapper.accept(() -> {
            Connection connectionB = dataSource.getConnection();
            connectionB.setAutoCommit(false);

            // 새로운 user 객체를 저장했다.
            userDao.insert(connectionB, new User("newUser", "new", "new@email.com"));
            connectionB.commit();
        })).start();

        sleep(0.5);

        // MySQL에서 팬텀 읽기를 시연하려면 update를 실행해야 한다.
        // http://stackoverflow.com/questions/42794425/unable-to-produce-a-phantom-read/42796969#42796969
        userDao.updatePasswordGreaterThan(connectionA, "changePassword", 1);

        // 사용자A가 다시 id로 범위를 조회했다.
        List<User> secondRead = userDao.findGreaterThan(connectionA, 1);

        assertThat(firstRead).hasSize(1); //첫번째 읽었을 때의 상태
        assertThat(secondRead).hasSize(2); //두번째 읽었을 땐 삽입된 행을 인식한다
        connectionA.rollback();
    }

    @DisplayName("Mysql은 REPEATABLE_READ 에서 읽기 작업에 한해선 팬텀 리딩이 발생하지 않는다")
    @Test
    void not_phantom_reading_when_only_read() throws SQLException {
        int isolationLevel = Connection.TRANSACTION_REPEATABLE_READ;
        userDao.insert(dataSource.getConnection(), new User("coli", "old", "email@email.com"));

        //커넥션 A 시작
        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false);
        connectionA.setTransactionIsolation(isolationLevel);

        // 사용자A가 id로 범위를 조회했다.
        List<User> firstRead = userDao.findGreaterThan(connectionA, 1);

        new Thread(RunnableWrapper.accept(() -> {
            Connection connectionB = dataSource.getConnection();
            connectionB.setAutoCommit(false);

            // 새로운 user 객체를 저장했다.
            userDao.insert(connectionB, new User("newUser", "new", "new@email.com"));
            connectionB.commit();
        })).start();

        sleep(0.5);

        // 사용자A가 다시 id로 범위를 조회했다.
        List<User> secondRead = userDao.findGreaterThan(connectionA, 1);

        assertThat(secondRead).hasSize(firstRead.size()); //팬텀리딩이 발생하지 않음
        connectionA.rollback();
    }

    @DisplayName("SERIALIZABLE에서는 팬텀리딩이 발생하지 않는다")
    @Test
    void not_phantom_reading() throws SQLException {
        final int isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
        userDao.insert(dataSource.getConnection(), new User("coli", "old", "email@email.com"));

        //커넥션 A 시작
        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false);
        connectionA.setTransactionIsolation(isolationLevel);

        // 사용자A가 id로 범위를 조회했다.
        List<User> firstRead = userDao.findGreaterThan(connectionA, 1);

        new Thread(RunnableWrapper.accept(() -> {
            // 새로운 user 객체를 저장했다.
            Connection connectionB = dataSource.getConnection();
            connectionB.setAutoCommit(false);
            userDao.insert(connectionB, new User("newUser", "new", "new@email.com"));
            connectionB.commit();
        })).start();

        sleep(0.5);

        // MySQL에서 팬텀 읽기를 시연하려면 update를 실행해야 한다.
        // http://stackoverflow.com/questions/42794425/unable-to-produce-a-phantom-read/42796969#42796969
        userDao.updatePasswordGreaterThan(connectionA, "changePassword", 1);

        // 사용자A가 다시 id로 범위를 조회했다.
        List<User> secondRead = userDao.findGreaterThan(connectionA, 1);
        assertThat(secondRead).hasSize(firstRead.size()); //두번째 읽었을 때에도 처음 행 상태를 유지한다
        connectionA.rollback();
    }
}
