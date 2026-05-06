//package com.bist.mini.sample.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.Getter;
//import lombok.Setter;
//
///**
// * 샘플 등록 요청 DTO
// */
//@Getter
//@Setter
//@Schema(description = "샘플 등록 요청 데이터")
//public class SampleRequest {
//
//    @NotBlank(message = "내용(testStr)은 필수입니다.")
//    @Size(max = 100, message = "내용은 100자 이내로 입력해 주세요.")
//    @Schema(description = "테스트 문자열", example = "안녕하세요")
//    private String testStr;
//
//}
