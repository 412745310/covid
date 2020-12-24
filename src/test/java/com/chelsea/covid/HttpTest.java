package com.chelsea.covid;

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
 * http测试
 * 
 * @author shevchenko
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpTest {
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * get请求
     */
    @Test
    public void doGet() {
        String url = "http://www.itcast.cn";
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");
        HttpEntity<Object> requestEntity = new HttpEntity<>("parameters", headers);
        String s = HttpUtils.getForEntity(restTemplate, url, requestEntity, String.class, "");
        System.out.println(s);
    }
    
}
