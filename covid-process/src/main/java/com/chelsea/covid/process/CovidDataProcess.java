package com.chelsea.covid.process;

import java.util.List;
import java.util.Properties;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import com.alibaba.fastjson.JSON;
import com.chelsea.covid.domain.Covid;

/**
 * 疫情数据处理
 * 
 * @author shevchenko
 *
 */
public class CovidDataProcess {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        // 开启CK，指定使用FsStateBackend
        // 有三种backend：MemoryStateBackend,FsStateBackend,RocksdbStateBackend
        // MemoryStateBackend：用户开发测试
        // FsStateBackend：生产环境建议使用，并搭配hdfs
        // RocksdbStateBackend：生产环境使用，有超大状态保存时可以使用
        env.setStateBackend(new FsStateBackend("file:///C:/Users/Administrator/Desktop/CovidDataProcess"));
        // checkpoint的周期间隔（毫秒），默认是没有开启ck，需要通过指定间隔来开启ck
        // 表示每隔10秒进行一次checkpoint，在所有operation未全部进行checkpoint时，向sink下发的数据都是未提交状态
        env.enableCheckpointing(10000);
        // 设置ck的执行语义
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 设置两次ck之间的最小间隔（毫秒）
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        // 设置ck超时时间（毫秒），如果超时则认为本次ck失败，继续下一次ck
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 设置ck出现问题时，是否让程序继续执行，true：程序报错，false：不报错继续下次ck
        // 如果设置为false，下次ck成功则没有问题，如果下次ck也失败，此时如果程序挂掉需要从ck恢复数据时，可能导致计算错误或是重复计算等问题
        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
        // 设置任务取消时是否保留检查点，retain：保留，delete：删除作业数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 设置同时最多允许几个ck同时执行
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        Properties prop = new Properties();
        // kafka集群
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        // 消费组
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "covid19");
        // 动态分区检测
        prop.setProperty("flink.partition-discovery.interval-millis", "5000");
        // 设置kv的反序列化使用的类
        prop.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        prop.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // 设置默认消费的偏移量起始值（从最新处消费）
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // 设置消费者的隔离级别，默认是读取未提交数据
        // 此处设置为读取已提交数据，也就是两阶段提交之后的数据
        prop.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // 设置kafka的偏移量提交策略，此处设置为false，表示将提交策略交给checkpoint管理
        prop.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // 获取kafkaConsumer对象
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("covid19", new SimpleStringSchema(), prop);
        // 设置kafka消费者偏移量是基于CK成功时提交
        consumer.setCommitOffsetsOnCheckpoints(true);
        DataStreamSource<String> covidStrDs = env.addSource(consumer);
        SingleOutputStreamOperator<Covid> covidMapDs = covidStrDs.map(new MapFunction<String, Covid>() {

            private static final long serialVersionUID = 1L;

            @Override
            public Covid map(String line) throws Exception {
                Covid covid = JSON.parseObject(line, Covid.class);
                return covid;
                
            }});
        // 分离出省份数据
        SingleOutputStreamOperator<Covid> provinceDs = covidMapDs.filter(new FilterFunction<Covid>() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(Covid covid) throws Exception {
                return covid.getStatisticsData() != null;
            }
        });
        // 分离出城市数据
        SingleOutputStreamOperator<Covid> cityDs = covidMapDs.filter(new FilterFunction<Covid>() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(Covid covid) throws Exception {
                return covid.getStatisticsData() == null;
            }
        });
        // 分离出各省份每天的统计数据
        SingleOutputStreamOperator<Covid> statisticsDs = provinceDs.flatMap(new FlatMapFunction<Covid, Covid>() {

            private static final long serialVersionUID = 1L;

            @Override
            public void flatMap(Covid covid, Collector<Covid> out) throws Exception {
                String statisticsData = covid.getStatisticsData();
                List<Covid> covidList = JSON.parseArray(statisticsData, Covid.class);
                for (Covid c : covidList) {
                    c.setProvinceShortName(covid.getProvinceShortName());
                    c.setLocationId(covid.getLocationId());
                    out.collect(c);
                }
            }});
        
        // 统计分析 : 全国疫情汇总信息（按照日期分组）：现有确诊，累计确诊，现有疑似，累计治愈，累计死亡
        SingleOutputStreamOperator<Covid> result1 = provinceDs.keyBy("datetime").reduce(new ReduceFunction<Covid>() {
            
            private static final long serialVersionUID = 1L;
            private Integer currentConfirmedCountOld = 0;
            private Integer confirmedCountOld = 0;
            private Integer suspectedCountOld = 0;
            private Integer curedCountOld = 0;
            private Integer deadCountOld = 0;

            @Override
            public Covid reduce(Covid oldCovid, Covid newCovid) throws Exception {
                Covid covid = new Covid();
                if (oldCovid != null) {
                    currentConfirmedCountOld = oldCovid.getCurrentConfirmedCount();
                    confirmedCountOld = oldCovid.getConfirmedCount();
                    suspectedCountOld = oldCovid.getSuspectedCount();
                    curedCountOld = oldCovid.getCuredCount();
                    deadCountOld = oldCovid.getDeadCount();
                }
                if (newCovid != null) {
                    covid.setDatetime(newCovid.getDatetime());
                    covid.setCurrentConfirmedCount(newCovid.getCurrentConfirmedCount() + currentConfirmedCountOld);
                    covid.setConfirmedCount(newCovid.getConfirmedCount() + confirmedCountOld);
                    covid.setSuspectedCount(newCovid.getSuspectedCount() + suspectedCountOld);
                    covid.setCuredCount(newCovid.getCuredCount() + curedCountOld);
                    covid.setDeadCount(newCovid.getDeadCount() + deadCountOld);
                }
                return covid;
            }
        });
        // 统计分析：全国疫情趋势（按照日期分组）
        SingleOutputStreamOperator<Covid> result2 = statisticsDs.keyBy("dateId").reduce(new ReduceFunction<Covid>() {
            
            private static final long serialVersionUID = 1L;
            private Integer confirmedIncrOld = 0;
            private Integer confirmedCountOld = 0;
            private Integer suspectedCountOld = 0;
            private Integer curedCountOld = 0;
            private Integer deadCountOld = 0;

            @Override
            public Covid reduce(Covid oldCovid, Covid newCovid) throws Exception {
                Covid covid = new Covid();
                if (oldCovid != null) {
                    confirmedIncrOld = oldCovid.getConfirmedIncr();
                    confirmedCountOld = oldCovid.getConfirmedCount();
                    suspectedCountOld = oldCovid.getSuspectedCount();
                    curedCountOld = oldCovid.getCuredCount();
                    deadCountOld = oldCovid.getDeadCount();
                }
                if (newCovid != null) {
                    covid.setDateId(newCovid.getDateId());
                    covid.setConfirmedIncr(newCovid.getConfirmedIncr() + confirmedIncrOld);
                    covid.setConfirmedCount(newCovid.getConfirmedCount() + confirmedCountOld);
                    covid.setSuspectedCount(newCovid.getSuspectedCount() + suspectedCountOld);
                    covid.setCuredCount(newCovid.getCuredCount() + curedCountOld);
                    covid.setDeadCount(newCovid.getDeadCount() + deadCountOld);
                }
                return covid;
            }
        });
        // 统计分析：境外输入排行（按照日期省份分组）
        SingleOutputStreamOperator<Covid> result3 = cityDs.filter(new FilterFunction<Covid>() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(Covid covid) throws Exception {
                return "境外输入".equals(covid.getCityName());
            }
        })
        .keyBy(new KeySelector<Covid, Tuple3<String, String, String>>(){

            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String> getKey(Covid covid) throws Exception {
                return new Tuple3<>(covid.getDatetime(), covid.getProvinceShortName(), covid.getPid());
            }})
        .reduce(new ReduceFunction<Covid>() {
            
            private static final long serialVersionUID = 1L;
            private Integer confirmedCountOld = 0;

            @Override
            public Covid reduce(Covid oldCovid, Covid newCovid) throws Exception {
                Covid covid = new Covid();
                if (oldCovid != null) {
                    confirmedCountOld = oldCovid.getConfirmedCount();
                }
                if (newCovid != null) {
                    covid.setDatetime(newCovid.getDatetime());
                    covid.setProvinceShortName(newCovid.getProvinceShortName());
                    covid.setPid(newCovid.getPid());
                    covid.setConfirmedCount(newCovid.getConfirmedCount() + confirmedCountOld);
                }
                return covid;
            }
        });
        // 统计分析：统计北京市的累计确诊地图
        SingleOutputStreamOperator<Covid> result4 = cityDs.filter(new FilterFunction<Covid>() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(Covid covid) throws Exception {
                return "北京".equals(covid.getProvinceShortName());
            }
        });
        result4.print();
        env.execute();
    }    
}
