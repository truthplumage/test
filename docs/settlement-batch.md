# Seller Settlement Batch

## 개요

1. 판매자가 상품을 등록 (`/api/v1/products`, sellerId 포함)
2. 구매자가 주문을 생성 (`/api/v1/orders`)
3. Toss 결제 완료 시 `/api/v1/payments/confirm`에서
   - `purchase_order` 상태를 `PAID`로 변경
   - `seller_settlement` 테이블에 `PENDING` 상태 레코드 생성
4. Spring Batch `sellerSettlementJob`이 `PENDING` 레코드를 읽어 셀러별 합계를 계산하고 `COMPLETED`로 전환

## Batch 구성

- `SellerSettlementBatchConfig`: `sellerSettlementJob` + `settlementStep`
  - Tasklet이 `SellerSettlementRepository.findByStatus(PENDING)`로 데이터를 읽음
  - 셀러별 금액을 그룹화해 로그로 출력
  - 각 레코드를 `markCompleted()` 후 저장
- 실행 방법: `POST /api/v1/settlements/run`
  - 내부적으로 `JobLauncher`가 timestamp 파라미터로 배치를 실행한다.

## 테이블

- `seller_settlement`
  - `id`, `seller_id`, `order_id`, `amount`, `status(PENDING/COMPLETED)`, `created_at`, `settled_at`
- `purchase_order`
  - 주문과 판매자/상품/금액 정보를 보유한다 (상세는 `docs/order-ddl.sql`)

## 향후 확장 포인트

- 실제 송금 API 연동 시 Tasklet에서 외부 결제/정산 시스템 호출
- 셀러별 정산 이력 조회 API 추가
- 배치 스케줄링 (현재는 수동 API 트리거)
