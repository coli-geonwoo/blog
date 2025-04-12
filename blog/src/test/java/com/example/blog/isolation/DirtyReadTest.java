package com.example.blog.isolation;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.RunnableWrapper;
import com.example.blog.domain.User;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DirtyReadTest extends BaseTest {

    /*
        Diry Read란 작업이 완료되지 않은(commit 되지 않은) 더러운 데이터가
        다른 트랜잭션에서 보이는 현상을 말한다.
     */

    @DisplayName("READ_UNCOMMITED에서는 DIRTY_READ가 발생한다")
    @Test
    void read_uncommited() throws SQLException {
        int readUncommitted = Connection.TRANSACTION_READ_UNCOMMITTED;
        setUp(createH2DataSource());

        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false); // 수동 커밋으로 커넥션 A 시작
        userDao.insert(connectionA , new User("user1", "password", "email1@email.com")); //유저 삽입

        new Thread(RunnableWrapper.accept(() -> {
            //connectionB 생성
            Connection connectionB = dataSource.getConnection();
            connectionB.setTransactionIsolation(readUncommitted);

            User dirtyReadUser = userDao.findByAccount(connectionB, "user1");

            assertThat(dirtyReadUser).isNotNull(); //READ_UNCOMMITED 에선 유저가 읽힌다.
        })).start();

        //connectionA 롤백 -> 실제로는 저장하지 않았는데 사용자 B는 데이터가 있다고 착각함
        connectionA.rollback();
    }

    @DisplayName("상위 격리 수준에서는 DIRTY_READ가 발생하지 않는다")
    @ValueSource(ints = {
            Connection.TRANSACTION_READ_COMMITTED,
            Connection.TRANSACTION_REPEATABLE_READ,
            Connection.TRANSACTION_SERIALIZABLE
    })
    @ParameterizedTest
    void not_dirty_read(int isolationLevel) throws SQLException {
        setUp(createH2DataSource());

        Connection connectionA = dataSource.getConnection();
        connectionA.setAutoCommit(false); // 수동 커밋으로 커넥션 A 시작
        userDao.insert(connectionA , new User("user1", "password", "email1@email.com")); //유저 삽입

        new Thread(RunnableWrapper.accept(() -> {
            //connectionB 생성
            Connection connectionB = dataSource.getConnection();
            connectionB.setTransactionIsolation(isolationLevel);

            User dirtyReadUser = userDao.findByAccount(connectionB, "user1");

            assertThat(dirtyReadUser).isNull(); //READ_UNCOMMITED보다 상위 격리수준에서는 읽히지 않음
        })).start();

        //connectionA 롤백
        connectionA.rollback();
    }
}
