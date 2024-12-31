package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.blog.entity.Company;
import com.example.blog.entity.Member;
import com.example.blog.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void deleteCompanyWithoutClear() {
        Company company = companyRepository.save(new Company());
        Member member = memberRepository.save(new Member(company));

        assertThatThrownBy(() -> companyService.deleteCompanyWithoutClear(company.getId()))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void deleteCompanyWithClear() {
        Company company = companyRepository.save(new Company());
        Member member = memberRepository.save(new Member(company));

        assertThatCode(() -> companyService.deleteCompanyWithClear(company.getId()))
                .doesNotThrowAnyException();
    }
}