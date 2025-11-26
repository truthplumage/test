package com.example.shop.search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "검색")
// 검색/색인 관련 API 엔드포인트를 노출하는 컨트롤러
@RestController
@RequestMapping("${api.v1}/search")
public class ProductSearchController {

    private final SearchService searchService;

    public ProductSearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(
        summary = "상품 검색",
        description = "키워드와 카테고리로 엘라스틱서치 상품 인덱스를 조회합니다."
    )
    @GetMapping("/products")
    public ProductSearchResponse searchProducts(
        @Parameter(description = "검색 키워드", example = "남자 신발")
        @RequestParam(required = false) String keyword,
        @Parameter(description = "카테고리 필터", example = "shoes")
        @RequestParam(required = false) String category,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return searchService.searchProducts(keyword, category, pageable);
    }

    @Operation(
        summary = "상품 색인",
        description = "ES 상품 인덱스에 문서를 저장합니다. id와 시간은 서버에서 자동 생성됩니다."
    )
    @PostMapping("/products")
    public ResponseEntity<ProductDocument> indexProduct(
        @RequestBody(
            description = "상품 색인 요청",
            required = true,
            content = @Content(examples = @ExampleObject(value = "{\n  \"name\": \"남자 셔츠\",\n  \"brand\": \"SHOP\",\n  \"category\": \"shirts\",\n  \"price\": 59000\n}"))
        ) ProductIndexRequest request
    ) {
        ProductDocument saved = searchService.indexProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
        summary = "상품 인덱스 설정/매핑 갱신",
        description = "인덱스가 없으면 생성하고, 있으면 레플리카/매핑을 업데이트합니다. 샤드 수 변경은 기존 인덱스에 적용되지 않습니다."
    )
    @PutMapping("/products/index")
    public IndexUpdateResponse updateIndex(
        @RequestBody(
            description = "인덱스 설정",
            required = true,
            content = @Content(examples = @ExampleObject(value = "{\n  \"numberOfShards\": 3,\n  \"numberOfReplicas\": 0\n}"))
        ) IndexConfigRequest request
    ) {
        return searchService.applyProductIndexConfig(request);
    }

    @Operation(summary = "상품 인덱스 상태 조회", description = "인덱스 존재 여부, 설정, 매핑 정보를 반환합니다.")
    @GetMapping("/products/index")
    public IndexStatusResponse getIndexStatus() {
        return searchService.getProductIndexStatus();
    }
}
