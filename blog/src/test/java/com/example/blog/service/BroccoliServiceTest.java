package com.example.blog.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BroccoliServiceTest {

    @Autowired
    private BroccoliService broccoliService;

    @Test
    void deleteAll() {
        long start = System.currentTimeMillis();
        broccoliService.deleteAll();
        long end = System.currentTimeMillis();
        long elapseTime = end - start;
        System.out.println("deleteAll-ElapseTime = " + elapseTime);
    }

    @Test
    void deleteAllInBatch() {
        long start = System.currentTimeMillis();
        broccoliService.deleteAllInBatch();
        long end = System.currentTimeMillis();
        long elapseTime = end - start;
        System.out.println("deleteAllInBatch-ElapseTime = " + elapseTime);
    }
}