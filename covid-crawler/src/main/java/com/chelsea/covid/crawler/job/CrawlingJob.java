package com.chelsea.covid.crawler.job;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chelsea.covid.crawler.bean.Covid;
import com.chelsea.covid.crawler.util.DateUtil;
import com.chelsea.covid.crawler.util.HttpUtils;

/**
 * 疫情数据爬取job
 * 
 * @author shevchenko
 *
 */
@Component
public class CrawlingJob {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private KafkaTemplate<?, String> kafkaTemplate;
    
    @Scheduled(cron = "0 0 8 * * ?")
    public void execute() {
        crawling();
    }
    
    /**
     * 疫情数据爬取
     */
    private void crawling() {
        String url = "https://ncov.dxy.cn/ncovh5/view/pneumonia";
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");
        HttpEntity<Object> requestEntity = new HttpEntity<>("parameters", headers);
        String html = HttpUtils.getForEntity(restTemplate, url, requestEntity, String.class, "");
        // 解析页面指定内容，此处为id等于getAreaStat的script标签下的所有字符串
        Document doc = Jsoup.parse(html);
        String text = doc.select("script[id=getAreaStat]").toString();
        // 使用正则表达式获取json格式的疫情数据
        String pattern = "\\[(.*)\\]";
        Pattern reg = Pattern.compile(pattern);
        Matcher matcher = reg.matcher(text);
        String jsonStr = "";
        if (matcher.find()) {
            jsonStr = matcher.group(0);
        }
        // 对json数据进行更进一步解析
        // 将第一层json（省份数据）解析为bean
        List<Covid> pCovidList = JSON.parseArray(jsonStr, Covid.class);
        for (Covid pCovid : pCovidList) {
            // 设置时间字段为当前时间
            pCovid.setDatetime(DateUtil.getDateDateString2());
            // 获取城市json字符串
            String citysStr = pCovid.getCities();
            // 将城市json字符串解析为bean
            List<Covid> cCovidList = JSON.parseArray(citysStr, Covid.class);
            for (Covid cCovid : cCovidList) {
                cCovid.setDatetime(DateUtil.getDateDateString2());
                cCovid.setPid(pCovid.getLocationId());
                cCovid.setProvinceName(pCovid.getProvinceName());
                cCovid.setProvinceShortName(pCovid.getProvinceShortName());
                // 将城市疫情数据发送到kafka
                String cCovidJsonStr = JSON.toJSONString(cCovid);
                kafkaTemplate.send("covid19", cCovidJsonStr);
            }
            String statisticsDataUrl = pCovid.getStatisticsData();
            // 获取第一层json（省份数据）中的每天统计数据
            String statisticsDataStr = HttpUtils.getForEntity(restTemplate, statisticsDataUrl, requestEntity, String.class, "");
            JSONObject jsonObject = JSON.parseObject(statisticsDataStr);
            String dataStr = jsonObject.getString("data");
            // 将爬取解析出来的每天统计数据json字符串设置回省份bean中
            pCovid.setStatisticsData(dataStr);
            pCovid.setCities(null);
            // 将省份疫情数据发送到kafka
            String pCovidJsonStr = JSON.toJSONString(pCovid);
            kafkaTemplate.send("covid19", pCovidJsonStr);
        }
    }

}
