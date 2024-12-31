package com.example.blog.service;

import com.example.blog.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByCompanyId(long companyId);
}
