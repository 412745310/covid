package com.chelsea.covid.crawler.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 防疫物资实体类
 * 
 * @author shevchenko
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Materia {
    
    // 物资名称
    private String name;
    // 物资来源
    private String from;
    // 物资数量
    private Integer count;

}
