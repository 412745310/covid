package com.chelsea.covid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.chelsea.covid.util.HttpUtils;

/**
 * 爬虫单元测试
 * 
 * @author shevchenko
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JsoupTest {
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 爬取网页内容并解析
     */
    @Test
    public void crawling() {
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
        if (matcher.find()) {
            String group = matcher.group(0);
            System.out.println(group);
        }
    }

}
