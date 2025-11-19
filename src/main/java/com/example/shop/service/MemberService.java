package com.example.shop.service;

import com.example.shop.common.ResponseEntity;
import com.example.shop.member.Member;
import com.example.shop.member.MemberRepository;
import com.example.shop.member.MemberRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    public ResponseEntity<List<Member>> findAll(){
        return new ResponseEntity<>(HttpStatus.OK.value(), memberRepository.findAll(), memberRepository.count());
    }
    public ResponseEntity<Member> create(MemberRequest request) {
        Member member = new Member(
                UUID.randomUUID(),
                request.email(),
                request.name(),
                request.password(),
                request.phone(),
                request.saltKey(),
                request.flag()
        );
        Member member1 = memberRepository.save(member);
        return new ResponseEntity<>(HttpStatus.CREATED.value(), member1, 1);
    }

    public ResponseEntity<Member> update(MemberRequest request, String id) {
        UUID uuid = UUID.fromString(id);
        Member member = memberRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));

        member.setEmail(request.email());
        member.setName(request.name());
        member.setPassword(request.password());
        member.setPhone(request.phone());
        member.setSaltKey(request.saltKey());
        member.setFlag(request.flag());

        Member updated = memberRepository.save(member);
        return new ResponseEntity<>(HttpStatus.OK.value(), updated, 1);
    }

    public ResponseEntity<Void> delete(String id) {
        UUID uuid = UUID.fromString(id);
        memberRepository.deleteById(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT.value(), null, 0);
    }
}
