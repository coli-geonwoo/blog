package com.example.blog.service;

import com.example.blog.repository.BroccoliRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BroccoliService {

    private final BroccoliRepository broccoliRepository;

    @Transactional
    public long deleteAll() {
        long start = System.currentTimeMillis();
        broccoliRepository.deleteAll();
        long end = System.currentTimeMillis();
        return end - start;
    }

    @Transactional
    public long deleteAllInBatch() {
        long start = System.currentTimeMillis();
        broccoliRepository.deleteAllInBatch();
        long end = System.currentTimeMillis();
        return end - start;
    }
}
