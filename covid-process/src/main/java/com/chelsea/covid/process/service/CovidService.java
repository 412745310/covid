package com.chelsea.covid.process.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chelsea.covid.domain.Covid;
import com.chelsea.covid.process.dao.CovidDao;

@Service
public class CovidService {
    
    @Autowired
    private CovidDao dao;
    
    /**
     * 新增全国疫情汇总信息
     * 
     * @param value
     */
    public void addCovid1(Covid value) {
        dao.addCovid1(value);
    }
    
    /**
     * 新增全国疫情趋势
     * 
     * @param value
     */
    public void addCovid2(Covid value) {
        dao.addCovid2(value);
    }
    
    /**
     * 新增境外输入排行
     * 
     * @param value
     */
    public void addCovid3(Covid value) {
        dao.addCovid3(value);
    }
    
    /**
     * 新增统计北京市的累计确诊
     * 
     * @param value
     */
    public void addCovid4(Covid value) {
        dao.addCovid4(value);
    }

}
