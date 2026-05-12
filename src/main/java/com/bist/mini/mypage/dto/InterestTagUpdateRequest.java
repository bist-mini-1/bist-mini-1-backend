package com.bist.mini.mypage.dto;

import lombok.Data;

import java.util.List;

@Data
public class InterestTagUpdateRequest {

    /** 설정할 관심 태그 ID 목록 (null이면 전체 삭제) */
    private List<Long> tagIds;
}
