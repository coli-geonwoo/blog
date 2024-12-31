package com.example.blog.service;

import com.example.blog.entity.Member;
import com.example.blog.repository.CompanyRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;
    private final EntityManager entityManager;

    @Transactional
    public void deleteCompanyWithoutClear(long companyId) {
        List<Member> companyMember = memberRepository.findAllByCompanyId(companyId);
        memberRepository.deleteAllInBatch(companyMember);
        companyRepository.deleteById(companyId);
    }

    @Transactional
    public void deleteCompanyWithClear(long companyId) {
        List<Member> companyMember = memberRepository.findAllByCompanyId(companyId);
        memberRepository.deleteAllInBatch(companyMember);
        entityManager.clear();
        companyRepository.deleteById(companyId);
    }
}
