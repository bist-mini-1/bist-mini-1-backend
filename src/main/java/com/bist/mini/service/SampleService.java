package com.bist.mini.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.dao.SampleDAO;
import com.bist.mini.entity.Sample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 샘플 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SampleService {

    private final SampleDAO sampleDAO;

    /**
     * 샘플 리스트 조회
     */
    public List<Sample> getSampleList() {
        return sampleDAO.findAll();
    }

    /**
     * 상세 조회
     */
    public Sample getSampleDetail(Long id) {
        Sample sample = sampleDAO.findById(id);
        if (sample == null) {
            throw new CustomException("존재하지 않는 샘플 ID입니다.", ErrorCode.SAMPLE_ERROR);
        }
        return sample;
    }

    /**
     * 샘플 등록
     */
    @Transactional
    public Sample createSample(Sample sample) {
        sampleDAO.insert(sample);
        return sample;
    }
}
