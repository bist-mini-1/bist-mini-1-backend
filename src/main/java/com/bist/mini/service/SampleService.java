package com.bist.mini.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.entity.Sample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 샘플 서비스 (Mock 데이터 반환)
 */
@Service
@Transactional(readOnly = true)
public class SampleService {

    /**
     * 샘플 리스트 조회 (Mock)
     */
    public List<Sample> getSampleList() {
        List<Sample> list = new ArrayList<>();
        list.add(Sample.builder().id(1L).testStr("첫 번째 샘플 데이터").build());
        list.add(Sample.builder().id(2L).testStr("두 번째 샘플 데이터").build());
        return list;
    }

    /**
     * 상세 조회 예시 (에러 발생 샘플 포함)
     */
    public Sample getSampleDetail(Long id) {
        if (id == 999L) {
            throw new CustomException("존재하지 않는 샘플 ID입니다.", ErrorCode.SAMPLE_ERROR);
        }
        return Sample.builder().id(id).testStr("상세 조회 결과: " + id).build();
    }
}
