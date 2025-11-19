package com.example.shop.member.application;

import com.example.shop.common.ResponseEntity;
import com.example.shop.member.application.dto.MemberCommand;
import com.example.shop.member.application.dto.MemberInfo;
import com.example.shop.member.domain.Member;
import com.example.shop.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    public ResponseEntity<List<MemberInfo>> findAll(Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
        List<MemberInfo> members = page.stream()
                .map(MemberInfo::from)
                .toList();
        return new ResponseEntity<>(HttpStatus.OK.value(), members, page.getTotalElements());
    }
    public ResponseEntity<MemberInfo> create(MemberCommand command) {
        Member member = Member.create(
                command.email(),
                command.name(),
                command.password(),
                command.phone(),
                command.saltKey(),
                command.flag()
        );
        Member saved = memberRepository.save(member);
        return new ResponseEntity<>(HttpStatus.CREATED.value(), MemberInfo.from(saved), 1);
    }

    public ResponseEntity<MemberInfo> update(MemberCommand command, String id) {
        UUID uuid = UUID.fromString(id);
        Member member = memberRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));

        member.updateInformation(
                command.email(),
                command.name(),
                command.password(),
                command.phone(),
                command.saltKey(),
                command.flag()
        );

        Member updated = memberRepository.save(member);
        return new ResponseEntity<>(HttpStatus.OK.value(), MemberInfo.from(updated), 1);
    }

    public ResponseEntity<Void> delete(String id) {
        UUID uuid = UUID.fromString(id);
        memberRepository.deleteById(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT.value(), null, 0);
    }
}
