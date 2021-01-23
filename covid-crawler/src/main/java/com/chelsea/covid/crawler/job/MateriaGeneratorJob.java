package com.chelsea.covid.crawler.job;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.chelsea.covid.common.MaterialSourceConst;
import com.chelsea.covid.domain.Materia;

/**
 * 疫情物资生成定时job
 * 
 * @author shevchenko
 *
 */
@Component
public class MateriaGeneratorJob {

    private String[] wzmc = new String[] {"N95口罩/个", "医用外科口罩/个", "84消毒液/瓶", "电子体温计/个", "一次性手套/副", "护目镜/副", "医用防护服/套"};
    private String[] wzly = new String[] {MaterialSourceConst.CAIGOU, MaterialSourceConst.XIABO, MaterialSourceConst.JUANZENG, MaterialSourceConst.XIAOHAO, MaterialSourceConst.XUQIU};
    
    @Autowired
    private KafkaTemplate<?, String> kafkaTemplate;
    
    /**
     * 疫情物资随机生成
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void execute() {
        generator();
    }

    public void generator() {
        Random random = new Random();
        for(int i = 0; i < 10; i++) {
            Materia materia = new Materia(wzmc[random.nextInt(wzmc.length)], wzly[random.nextInt(wzly.length)], random.nextInt(1000));
            String jsonString = JSON.toJSONString(materia);
            kafkaTemplate.send("covid19_wz", jsonString);
        }
    }

}
