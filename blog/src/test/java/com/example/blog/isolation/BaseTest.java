package com.example.blog.isolation;

import com.example.blog.DBPopulatorUtils;
import com.example.blog.dao.UserDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;

public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(DirtyReadTest.class);
    protected DataSource dataSource;
    protected UserDao userDao;

    protected void setUp(final DataSource dataSource) {
        this.dataSource = dataSource;
        DBPopulatorUtils.execute(dataSource);
        this.userDao = new UserDao(dataSource);
    }

    protected DataSource createMySQLDataSource(final JdbcDatabaseContainer<?> container) {
        final var config = new HikariConfig();
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        config.setDriverClassName(container.getDriverClassName());
        return new HikariDataSource(config);
    }

    protected DataSource createH2DataSource() {
        final var jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL;");
        jdbcDataSource.setUser("sa");
        jdbcDataSource.setPassword("");
        return jdbcDataSource;
    }

    protected void sleep(double seconds) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (seconds * 1000));
        } catch (InterruptedException ignored) {
        }
    }
}
