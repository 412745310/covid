package com.chelsea.covid.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 防疫物资数据库实体类
 * 
 * @author shevchenko
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CovidWz {
    
    private String name;
    private Integer caigou;
    private Integer xiabo;
    private Integer juanzeng;
    private Integer xiaohao;
    private Integer xuqiu;
    private Integer kucun;
    
}
