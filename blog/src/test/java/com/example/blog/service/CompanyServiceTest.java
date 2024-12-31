package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.blog.entity.Company;
import com.example.blog.entity.Member;
import com.example.blog.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("batch 쿼리를 동기화하지 않으면 에러가 발생한다.")
    @Test
    void deleteCompanyWithoutClear() {
        Company company = companyRepository.save(new Company());
        Member member = memberRepository.save(new Member(company));

        assertThatThrownBy(() -> companyService.deleteCompanyWithoutClear(company.getId()))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @DisplayName("batch 쿼리 후, clear로 영속성 컨텍스트를 비워주면 에러가 발생하지 않는다.")
    @Test
    void deleteCompanyWithClear() {
        Company company = companyRepository.save(new Company());
        Member member = memberRepository.save(new Member(company));

        assertThatCode(() -> companyService.deleteCompanyWithClear(company.getId()))
                .doesNotThrowAnyException();
    }
}