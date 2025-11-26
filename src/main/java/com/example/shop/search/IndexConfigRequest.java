package com.example.shop.search;

// 인덱스 생성 시 샤드/레플리카 설정 요청 DTO
public record IndexConfigRequest(Integer numberOfShards, Integer numberOfReplicas) {
}
