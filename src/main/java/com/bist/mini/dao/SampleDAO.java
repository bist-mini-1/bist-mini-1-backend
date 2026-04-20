package com.bist.mini.dao;

import com.bist.mini.entity.Sample;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 샘플 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface SampleDAO {

    List<Sample> findAll();

    Sample findById(Long id);

}
