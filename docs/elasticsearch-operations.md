## Elasticsearch 운영 메모 (샤드/병렬 검색 관점)

병렬 검색은 “샤드가 여러 개일 때 샤드별로 검색을 동시에 실행한 뒤 머지한다”는 의미입니다. 노드가 1대여도 샤드가 여러 개면 샤드 단위 병렬이 돌고, 노드를 늘리면 물리적 분산이 추가됩니다.


### 샤드/병렬 상태 확인
- 인덱스 샤드 수 확인
  ```bash
  curl -s "http://localhost:9200/shop-products/_settings?pretty" \
    | jq '.["shop-products"].settings.index.number_of_shards'
  ```
- 샤드 배치 확인
  ```bash
  curl "http://localhost:9200/_cat/shards/shop-products?v"
  ```
  - 열 의미: shard 번호, prirep(P/R), state, docs, store, node 등
- 검색은 샤드별로 병렬 실행 후 코디네이터가 결과를 머지합니다. 단일 노드라도 샤드가 2개 이상이면 내부 스레드가 샤드 단위로 병렬 처리합니다. CPU 사용량으로 간접 확인 가능.

### 로컬 단일 노드 vs 멀티 노드
- 단일 노드: `discovery.type=single-node` 컨테이너 1개로 간단히 띄움. 샤드를 늘리면 논리 병렬, 물리 자원은 동일 머신.
- 멀티 노드: 컨테이너 여러 개 또는 물리/VM 노드 여러 대를 클러스터로 구성. 레플리카를 1 이상 두면 샤드가 다른 노드에 분산되어 장애 대응·읽기 부하 분산·물리 병렬성이 확보됨.

### 인덱스 생성 시 샤드 설정 예시
```sh
curl -X PUT http://localhost:9200/shop-products -H "Content-Type: application/json" -d '
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "name": {"type": "text", "analyzer": "nori"},
      "category": {"type": "keyword"},
      "price": {"type": "integer"},
      "updatedAt": {"type": "date"}
    }
  }
}'
```
- `number_of_shards`를 늘리면 파티셔닝이 많아져 병렬성이 늘지만 오버헤드도 증가. 데이터량·QPS(초당 요청 수)에 맞게 설정.
- `number_of_replicas`는 가용성·읽기 부하 분산용. 단일 노드 개발환경은 0으로 시작.

### 샤드·클러스터 상태 체크 명령
- 헬스: `curl http://localhost:9200/_cluster/health?pretty`
- 샤드: `curl http://localhost:9200/_cat/shards?v`
- 인덱스: `curl http://localhost:9200/_cat/indices?v`

### 병렬 검색 체감하려면
1. `number_of_shards`를 2 이상으로 인덱스를 생성하고, 데이터 적재.
2. `_cat/shards`로 샤드가 존재하는지 확인.
3. 검색 부하를 주고 CPU/스레드 증가를 관찰(top, htop 등) → 샤드 수가 많을수록 병렬 실행 스레드가 늘어납니다.
4. 노드를 2대 이상으로 구성하고 `number_of_replicas>=1`이면 샤드가 노드별로 분산되어 물리적 병렬성이 확보됩니다.

### 참고
- 샤드 수를 과도하게 늘리면 오히려 오버헤드가 커집니다(파일 핸들, 메모리, 머지 비용 증가). 데이터량·QPS에 맞게 적정 수준을 선택하세요.
