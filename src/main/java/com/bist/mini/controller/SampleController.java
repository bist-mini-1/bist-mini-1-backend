package com.bist.mini.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.dto.SampleRequest;
import com.bist.mini.entity.Sample;
import com.bist.mini.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 샘플 API 컨트롤러
 */
@Tag(name = "Sample", description = "샘플 관리 API")
@RestController
@RequestMapping("/api/sample")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @Operation(summary = "샘플 전체 목록 조회", description = "모든 샘플 데이터를 Mock 데이터 형식으로 조회합니다.")
    @GetMapping
    public ApiResponse<List<Sample>> getSamples() {
        List<Sample> samples = sampleService.getSampleList();
        return ApiResponse.success(samples);
    }

    @Operation(summary = "샘플 상세 조회", description = "샘플 ID를 통해 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<Sample> getSample(
            @Parameter(description = "샘플 고유 ID (999 요청 시 에러 발생)") @PathVariable Long id) {
        Sample sample = sampleService.getSampleDetail(id);
        return ApiResponse.success(sample);
    }

    @Operation(summary = "샘플 등록 예시 (@Valid)", description = "입력값을 검증하여 샘플 데이터를 등록하는 예시입니다.")
    @PostMapping
    public ApiResponse<Sample> createSample(@RequestBody @Valid SampleRequest request) {
        // 실제 저장 로직 대신 성공 응답 반환 (Mock)
        Sample created = Sample.builder()
                .id(100L)
                .testStr(request.getTestStr())
                .build();
        return ApiResponse.success(created);
    }
}
