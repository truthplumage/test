package com.example.shop.controller;

import com.example.shop.common.ResponseEntity;
import com.example.shop.member.Member;
import com.example.shop.member.MemberRequest;
import com.example.shop.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.v1}/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(
            summary = "회원 목록 조회",
            description = "public.member 테이블에 저장된 모든 회원을 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<Member>> findAll() {
        return memberService.findAll();
    }

    @Operation(
            summary = "회원 등록",
            description = "요청으로 받은 회원 정보를 public.member 테이블에 저장한다."
    )
    @PostMapping
    public ResponseEntity<Member> create(@RequestBody MemberRequest request) {
        return memberService.create(request);
    }

    @Operation(
            summary = "회원 수정",
            description = "요청으로 받은 회원 정보를 public.member 테이블에 수정한다."
    )
    @PutMapping("{id}")
    public ResponseEntity<Member> update(@RequestBody MemberRequest request, @PathVariable String id) {
        return memberService.update(request, id);
    }
    @Operation(
            summary = "회원 정보 삭제",
            description = "요청으로 받은 회원 정보를 public.member 테이블에서 삭제한다."
    )
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return memberService.delete(id);
    }

}
