package com.chelsea.covid.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chelsea.covid.web.bean.Covid1;
import com.chelsea.covid.web.dao.Covid1Dao;

@Service
public class CovidService {
    
    @Autowired
    private Covid1Dao covid1Dao;
    
    public Covid1 getNationalData(String dateTime) {
        QueryWrapper<Covid1> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("datetime", dateTime);
        return covid1Dao.selectOne(queryWrapper);
    }

}
