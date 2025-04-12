package com.example.blog.isolation;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.RunnableWrapper;
import com.example.blog.domain.User;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NonRepeatableReadTest extends BaseTest {

    @AfterEach
    void tearDown() throws SQLException {
        userDao.clear(dataSource.getConnection());
    }

    /*
        Non Repeatable Read란 같은 트랜잭션 내에서 데이터를 읽었을 때
        항상 같은 결과가 보장되는 것을 의미한다.

        READ_UNCOMMITED 와 READ_COMMITED 에서 Repeatable_Read는 보장되지 않는다
        - 두 격리 수준 모두 트랜잭션 완료 시 이전에 열렸던 트랜잭션에서 완료된 트랜잭션의 변경 내역을 바라볼 수 있다.
     */

    @DisplayName("READ_UNCOMMITED에서는 NON_REPEATABLE_READ가 발생한다")
    @Test
    void read_uncommited_non_repeatable_read() throws SQLException {
        int readUncommitted = Connection.TRANSACTION_READ_UNCOMMITTED;

        setUp(createH2DataSource());
        userDao.insert(dataSource.getConnection() , new User("user1", "password", "email1@email.com")); //유저 삽입

        //커넥션A 시작
        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false);
        connectionA.setTransactionIsolation(readUncommitted);

        User firstFind = userDao.findByAccount(connectionA, "user1");
        String firstPassword = firstFind.getPassword();

        new Thread(RunnableWrapper.accept(() -> {
            //connectionB 생성
            Connection connectionB = dataSource.getConnection();

            //비밀벊를 바꿈 (auto commit = true)
            User findByConnectionB = userDao.findByAccount(connectionB, "user1");
            findByConnectionB.changePassword("changePassword");
            userDao.update(connectionB, findByConnectionB);
        })).start();

        sleep(0.5);

        //다시 유저 비밀번호 조회
        User secondFind = userDao.findByAccount(connectionA, "user1");
        String secondPassword = secondFind.getPassword();

        //하나의 트랜잭션에서 반복조회의 결과가 다르다
        assertThat(firstPassword).isNotEqualTo(secondPassword);
        connectionA.rollback();
    }

    @DisplayName("READ_COMMITED에서는 NON_REPEATABLE_READ가 발생한다")
    @Test
    void read_commited_non_repeatable_read() throws SQLException {
        int readCommitted = Connection.TRANSACTION_READ_COMMITTED;

        setUp(createH2DataSource());
        userDao.insert(dataSource.getConnection() , new User("user1", "password", "email1@email.com")); //유저 삽입

        //커넥션A 시작
        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false);
        connectionA.setTransactionIsolation(readCommitted);

        User firstFind = userDao.findByAccount(connectionA, "user1");
        String firstPassword = firstFind.getPassword();

        new Thread(RunnableWrapper.accept(() -> {
            //connectionB 생성
            Connection connectionB = dataSource.getConnection();

            //비밀벊를 바꿈 (auto commit = true)
            User findByConnectionB = userDao.findByAccount(connectionB, "user1");
            findByConnectionB.changePassword("changePassword");
            userDao.update(connectionB, findByConnectionB);
        })).start();

        sleep(0.5);

        //다시 유저 비밀번호 조회
        User secondFind = userDao.findByAccount(connectionA, "user1");
        String secondPassword = secondFind.getPassword();

        //하나의 트랜잭션에서 반복조회의 결과가 다르다
        assertThat(firstPassword).isNotEqualTo(secondPassword);
        connectionA.rollback();
    }

    @DisplayName("상위 트랜잭션 수준에서는 NON_REPEATABLE_READ가 발생하지 않는다")
    @ValueSource(ints = {
            Connection.TRANSACTION_REPEATABLE_READ,
            Connection.TRANSACTION_SERIALIZABLE
    })
    @ParameterizedTest
    void repeatable_read(int isolationLevel) throws SQLException {
        setUp(createH2DataSource());
        userDao.insert(dataSource.getConnection() , new User("user1", "password", "email1@email.com")); //유저 삽입

        //커넥션A 시작
        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false);
        connectionA.setTransactionIsolation(isolationLevel);

        User firstFind = userDao.findByAccount(connectionA, "user1");
        String firstPassword = firstFind.getPassword();

        new Thread(RunnableWrapper.accept(() -> {
            //connectionB 생성
            Connection connectionB = dataSource.getConnection();

            //비밀벊를 바꿈 (auto commit = true)
            User findByConnectionB = userDao.findByAccount(connectionB, "user1");
            findByConnectionB.changePassword("changePassword");
            userDao.update(connectionB, findByConnectionB);
        })).start();

        sleep(0.5);

        //다시 유저 비밀번호 조회
        User secondFind = userDao.findByAccount(connectionA, "user1");
        String secondPassword = secondFind.getPassword();

        //하나의 트랜잭션에서 반복조회의 결과가 같다
        assertThat(firstPassword).isEqualTo(secondPassword);
        connectionA.rollback();
    }
}
