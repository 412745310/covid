package com.chelsea.covid.web.controller;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chelsea.covid.domain.Result;
import com.chelsea.covid.web.bean.Covid1;
import com.chelsea.covid.web.service.CovidService;

@RestController
@RequestMapping("/covid")
public class CovidController {
    
    @Autowired
    private CovidService covidService;

    /**
     * 获取全国疫情汇总数据
     * 
     * @return
     */
    @RequestMapping("/getNationalData")
    public Result getNationalData(String date) {
        String dateTime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        Covid1 covid1 = covidService.getNationalData(dateTime);
        return Result.success(covid1);
    }

}
