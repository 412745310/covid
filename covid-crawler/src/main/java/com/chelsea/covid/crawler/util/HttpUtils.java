package com.chelsea.covid.crawler.util;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * http工具类
 * 
 * @author shevchenko
 *
 */
@Slf4j
public class HttpUtils {

    /**
     * GET 方式调用，参数形式为1,2,3
     * 
     * @param restRemplate
     * @param url
     * @param requestEntity
     * @param uriVariables
     * @return
     */
    public static <T> T getForEntity(RestTemplate restRemplate, String url, HttpEntity<?> requestEntity,
            Class<T> responseType, Object... uriVariables) {
        try {
            ResponseEntity<T> rs =
                    restRemplate.exchange(url, HttpMethod.GET, requestEntity, responseType, uriVariables);
            if (HttpStatus.OK.equals(rs.getStatusCode())) {
                return rs.getBody();
            } else {
                log.error("http error staus:{}", rs.getStatusCode());
            }
        } catch (HttpClientErrorException hce) {
            log.error("http error staus:{},resopnse:{}", hce.getStatusCode(), hce.getResponseBodyAsString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * GET 方式调用，参数形式为Map
     * 
     * @param restRemplate
     * @param url
     * @param requestEntity
     * @param requestParam
     * @return
     */
    public static <T> T getForEntity(RestTemplate restRemplate, String url, HttpEntity<?> requestEntity,
            Class<T> responseType, Map<String, Object> requestParam) {
        try {
            ResponseEntity<T> rs =
                    restRemplate.exchange(url, HttpMethod.GET, requestEntity, responseType, requestParam);
            if (HttpStatus.OK.equals(rs.getStatusCode())) {
                return rs.getBody();
            } else {
                log.error("http error staus:{}", rs.getStatusCode());
            }
        } catch (HttpClientErrorException hce) {
            log.error("http error staus:{},resopnse:{}", hce.getStatusCode(), hce.getResponseBodyAsString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * POST方式调用
     * 
     * @param restRemplate
     * @param url
     * @param requestEntity
     * @param uriVariables
     * @return
     */
    public static <T> T postForEntity(RestTemplate restRemplate, String url, HttpEntity<?> requestEntity,
            Class<T> responseType, Object... uriVariables) {
        try {
            ResponseEntity<T> rs =
                    restRemplate.exchange(url, HttpMethod.POST, requestEntity, responseType, uriVariables);
            if (HttpStatus.OK.equals(rs.getStatusCode())) {
                // log.info("请求URL：{}，返回参数:{}" ,url, JSON.toJSONString(rs.getBody()));
                return rs.getBody();
            } else {
                log.error("http error staus:{}", rs.getStatusCode());
            }
        } catch (HttpClientErrorException hce) {
            log.error("http error staus:{},resopnse:{}", hce.getStatusCode(), hce.getResponseBodyAsString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * POST方式调用
     * 
     * @param restRemplate
     * @param url
     * @param requestEntity
     * @param requestParam
     * @return
     */
    public static <T> T postForEntity(RestTemplate restRemplate, String url, HttpEntity<?> requestEntity,
            Class<T> responseType, Map<String, Object> requestParam) {
        try {
            ResponseEntity<T> rs =
                    restRemplate.exchange(url, HttpMethod.POST, requestEntity, responseType, requestParam);
            if (HttpStatus.OK.equals(rs.getStatusCode())) {
                return rs.getBody();
            } else {
                log.error("http error staus:{}", rs.getStatusCode());
            }
        } catch (HttpClientErrorException hce) {
            log.error("http error staus:{},resopnse:{}", hce.getStatusCode(), hce.getResponseBodyAsString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
