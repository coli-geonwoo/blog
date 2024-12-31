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
    public void deleteAll() {
        broccoliRepository.deleteAll();
    }

    @Transactional
    public void deleteAllInBatch() {
        broccoliRepository.deleteAllInBatch();
    }
}
