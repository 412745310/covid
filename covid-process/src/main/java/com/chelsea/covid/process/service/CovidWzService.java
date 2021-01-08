package com.chelsea.covid.process.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chelsea.covid.domain.CovidWz;
import com.chelsea.covid.process.dao.CovidWzDao;

@Service
public class CovidWzService {
    
    @Autowired
    private CovidWzDao dao;
    
    /**
     * 新增物资
     * 
     * @param value
     */
    public void addCovidWz(CovidWz value) {
        dao.addCovidWz(value);
    }

}
