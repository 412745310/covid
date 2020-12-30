package com.chelsea.covid.crawler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * kafka测试类
 * 
 * @author shevchenko
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTest {
    
    @Autowired
    private KafkaTemplate<?, String> kafkaTemplate;
    
    /**
     * 往kafka发送消息测试
     */
    @Test
    public void sendToKafkaTest() {
        kafkaTemplate.send("test", "abc");
    }

}
