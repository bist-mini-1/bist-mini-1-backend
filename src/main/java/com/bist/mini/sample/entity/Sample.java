package com.bist.mini.sample.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 샘플 엔티티
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sample {

    private Long id;
    private String testStr;

}
