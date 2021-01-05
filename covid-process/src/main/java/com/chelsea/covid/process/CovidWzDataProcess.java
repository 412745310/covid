package com.chelsea.covid.process;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chelsea.covid.common.MaterialSourceConst;

/**
 * 疫情物资数据处理
 * 
 * @author shevchenko
 *
 */
public class CovidWzDataProcess {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        // 开启CK，指定使用FsStateBackend
        // 有三种backend：MemoryStateBackend,FsStateBackend,RocksdbStateBackend
        // MemoryStateBackend：用户开发测试
        // FsStateBackend：生产环境建议使用，并搭配hdfs
        // RocksdbStateBackend：生产环境使用，有超大状态保存时可以使用
        env.setStateBackend(new FsStateBackend("file:///C:/Users/Administrator/Desktop/CovidWzDataProcess"));
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
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "covid19_wz");
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
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("covid19_wz", new SimpleStringSchema(), prop);
        // 设置kafka消费者偏移量是基于CK成功时提交
        consumer.setCommitOffsetsOnCheckpoints(true);
        DataStreamSource<String> ds = env.addSource(consumer);
        // 将json字符串{name:xx, from:xx, count:xx}转换为元组<String, <Integer, Integer, Integer, Integer, Integer, Integer>>
        SingleOutputStreamOperator<Tuple2<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>> wzDs =
                ds.map(new MapFunction<String, Tuple2<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> map(String jsonStr)
                            throws Exception {
                        JSONObject jsonObj = JSON.parseObject(jsonStr);
                        String name = jsonObj.getString("name");
                        String from = jsonObj.getString("from");
                        Integer count = jsonObj.getInteger("count");
                        return createWzTuple(name, from, count);
                    }
                });
//        wzDs.print();
        // 将元组按照key进行聚合
        SingleOutputStreamOperator<Tuple2<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>> map =
                wzDs.keyBy(0).map(new RichMapFunction<Tuple2<String,Tuple6<Integer,Integer,Integer,Integer,Integer,Integer>>, Tuple2<String,Tuple6<Integer,Integer,Integer,Integer,Integer,Integer>>>() {

                    private static final long serialVersionUID = 1L;
                    
                    private ValueState<Integer> caigouValueState = null;
                    private ValueState<Integer> xiaboValueState = null;
                    private ValueState<Integer> juanzengValueState = null;
                    private ValueState<Integer> xiaohaoValueState = null;
                    private ValueState<Integer> xuqiuValueState = null;
                    private ValueState<Integer> kucunValueState = null;
                    
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        ValueStateDescriptor<Integer> caigouValueStateDescriptor = new ValueStateDescriptor<>("caigou", Integer.class, 0);
                        caigouValueState = getRuntimeContext().getState(caigouValueStateDescriptor);
                        
                        ValueStateDescriptor<Integer> xiaboValueStateDescriptor = new ValueStateDescriptor<>("xiabo", Integer.class, 0);
                        xiaboValueState = getRuntimeContext().getState(xiaboValueStateDescriptor);
                        
                        ValueStateDescriptor<Integer> juanzengValueStateDescriptor = new ValueStateDescriptor<>("juanzeng", Integer.class, 0);
                        juanzengValueState = getRuntimeContext().getState(juanzengValueStateDescriptor);
                        
                        ValueStateDescriptor<Integer> xiaohaoValueStateDescriptor = new ValueStateDescriptor<>("xiaohao", Integer.class, 0);
                        xiaohaoValueState = getRuntimeContext().getState(xiaohaoValueStateDescriptor);
                        
                        ValueStateDescriptor<Integer> xuqiuValueStateDescriptor = new ValueStateDescriptor<>("xuqiu", Integer.class, 0);
                        xuqiuValueState = getRuntimeContext().getState(xuqiuValueStateDescriptor);
                        
                        ValueStateDescriptor<Integer> kucunValueStateDescriptor = new ValueStateDescriptor<>("kucun", Integer.class, 0);
                        kucunValueState = getRuntimeContext().getState(kucunValueStateDescriptor);
                    }

                    @Override
                    public Tuple2<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> map(
                            Tuple2<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> in) throws Exception {
                        Integer caigouValue = caigouValueState.value();
                        caigouValueState.update(in.f1.f0 + caigouValue);
                        
                        Integer xiaboValue = xiaboValueState.value();
                        xiaboValueState.update(in.f1.f1 + xiaboValue);
                        
                        Integer juanzengValue = juanzengValueState.value();
                        juanzengValueState.update(in.f1.f2 + juanzengValue);
                        
                        Integer xiaohaoValue = xiaohaoValueState.value();
                        xiaohaoValueState.update(in.f1.f3 + xiaohaoValue);
                        
                        Integer xuqiuValue = xuqiuValueState.value();
                        xuqiuValueState.update(in.f1.f4 + xuqiuValue);
                        
                        Integer kucunValue = kucunValueState.value();
                        kucunValueState.update(in.f1.f5 + kucunValue);
                        return new Tuple2<>(in.f0, new Tuple6<>(caigouValueState.value(), xiaboValueState.value(), juanzengValueState.value(),xiaohaoValueState.value(),xuqiuValueState.value(),kucunValueState.value()));
                    }});
        map.print();
        env.execute();
    }
    
    /**
     * 构建物资元组数据
     * 
     * @param name
     * @param from
     * @param count
     */
    public static Tuple2<String, Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> createWzTuple(String name, String from, Integer count) {
        Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple;
        switch (from) {
            case MaterialSourceConst.CAIGOU:
                tuple = new Tuple6<>(count, 0, 0, 0, 0, count);
                break;
            case MaterialSourceConst.XIABO:
                tuple = new Tuple6<>(0, count, 0, 0, 0, count);
                break;
            case MaterialSourceConst.JUANZENG:
                tuple = new Tuple6<>(0, 0, count, 0, 0, count);
                break;
            case MaterialSourceConst.XIAOHAO:
                tuple = new Tuple6<>(0, 0, 0, -count, 0, -count);
                break;
            case MaterialSourceConst.XUQIU:
                tuple = new Tuple6<>(0, 0, 0, 0, -count, -count);
                break;
            default:
                tuple = new Tuple6<>();
                break;
        }
        return new Tuple2<>(name, tuple);
    }
    
}
