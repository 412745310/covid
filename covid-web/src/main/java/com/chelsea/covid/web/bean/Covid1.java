package com.chelsea.covid.web.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 省份城市疫情数据通用实体类
 * 
 * @author shevchenko
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "covid_1")
public class Covid1 {
    
    // 当前确诊人数
    @TableField(value = "CurrentConfirmedCount")
    private Integer currentConfirmedCount;
    // 累计确认人数
    @TableField(value = "ConfirmedCount")
    private Integer confirmedCount;
    // 疑似病例人数
    @TableField(value = "SuspectedCount")
    private Integer suspectedCount;
    // 治愈人数
    @TableField(value = "CuredCount")
    private Integer curedCount;
    // 死亡人数
    @TableField(value = "DeadCount")
    private Integer deadCount;
    // 爬取时间
    @TableField(value = "Datetime")
    private String datetime;

}
