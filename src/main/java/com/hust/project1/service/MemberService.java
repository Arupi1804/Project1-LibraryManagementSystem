package com.hust.project1.service;

import com.hust.project1.entity.Member;
import com.hust.project1.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Get all members with pagination
     */
    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    /**
     * Get member by ID
     */
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Create or update member
     */
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    /**
     * Delete member by ID
     */
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * Search members with multiple criteria
     */
    public Page<Member> searchMembers(String fullName, String memberCode, String email,
            String phone, String status, Pageable pageable) {
        return memberRepository.searchCombined(fullName, memberCode, email, phone, status, pageable);
    }

    /**
     * Check if member code exists
     */
    public boolean existsByMemberCode(String memberCode) {
        return memberRepository.existsByMemberCode(memberCode);
    }

    /**
     * Check if member code exists for a different member (for edit validation)
     */
    public boolean existsByMemberCodeAndIdNot(String memberCode, Long id) {
        Optional<Member> existing = memberRepository.findByMemberCode(memberCode);
        return existing.isPresent() && !existing.get().getId().equals(id);
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * Check if email exists for a different member (for edit validation)
     */
    public boolean existsByEmailAndIdNot(String email, Long id) {
        Optional<Member> existing = memberRepository.findByEmail(email);
        return existing.isPresent() && !existing.get().getId().equals(id);
    }
}
