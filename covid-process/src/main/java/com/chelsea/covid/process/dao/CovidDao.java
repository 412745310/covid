package com.chelsea.covid.process.dao;

import com.chelsea.covid.domain.Covid;

public interface CovidDao {
    
    /**
     * 新增全国疫情汇总信息
     * 
     * @param covid
     */
    void addCovid1(Covid covid);
    
    /**
     * 新增全国疫情趋势
     * 
     * @param covid
     */
    void addCovid2(Covid covid);
    
    /**
     * 新增境外输入排行
     * 
     * @param covid
     */
    void addCovid3(Covid covid);
    
    /**
     * 新增统计北京市的累计确诊
     * 
     * @param covid
     */
    void addCovid4(Covid covid);

}
