package com.chelsea.covid.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 省份城市疫情数据通用实体类
 * 
 * @author shevchenko
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Covid {
    
    // 省份名称
    private String provinceName;
    // 省份短名
    private String provinceShortName;
    // 城市名称
    private String cityName;
    // 当前确诊人数
    private String currentConfirmedCount;
    // 累计确认人数
    private String confirmedCount;
    // 疑似病例人数
    private String suspectedCount;
    // 治愈人数
    private String curedCount;
    // 死亡人数
    private String deadCount;
    // 位置id
    private String locationId;
    // 省位置id
    private String pid;
    // 每天统计数据
    private String statisticsData;
    // 下属城市
    private String cities;
    // 爬取时间
    private String datetime;

}
