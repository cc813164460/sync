package com.hsgene.kafka.util;

import com.hsgene.constant.ConstantSymbol;
import com.hsgene.kafka.model.KafkaInfo;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 14:15 2017/10/24
 * @Modified By:
 */
public class KafkaProducerSender {

    private final static Logger LOGGER = Logger.getLogger(KafkaProducerSender.class);
    private Producer<String, String> producer;

    public KafkaProducerSender(KafkaInfo kafkaInfo) {
        try {
            String metadataBrokerList = kafkaInfo.getMetadataBrokerList();
            String zookeeperConnect = kafkaInfo.getZookeeperConnect();
            String serializerClass = kafkaInfo.getSerializerClass();
            String partitionerClass = kafkaInfo.getPartitionerClass();
            String requestRequiredAcks = kafkaInfo.getRequestRequiredAcks();
            String numPartitions = kafkaInfo.getNumPartitions();
            Properties properties = new Properties();
            if (metadataBrokerList != null) {
                properties.put(ConstantSymbol.METADATA_BROKER_LIST, metadataBrokerList);
            } else {
                throw new IllegalAccessException("metadata.broker.list can not be null");
            }
            if (zookeeperConnect != null) {
                properties.put(ConstantSymbol.ZOOKEEPER_CONNECT, zookeeperConnect);
            }
            if (serializerClass != null) {
                properties.put(ConstantSymbol.SERIALIZER_CLASS, serializerClass);
            }
            if (partitionerClass != null) {
                properties.put(ConstantSymbol.PARTITIONER_CLASS, partitionerClass);
            }
            if (requestRequiredAcks != null) {
                properties.put(ConstantSymbol.REQUEST_REQUIRED_ACKS, requestRequiredAcks);
            }
            if (numPartitions != null) {
                properties.put(ConstantSymbol.NUM_PARTITIONS, numPartitions);
            }
            ProducerConfig producerConfig = new ProducerConfig(properties);
            producer = new <String, String>Producer(producerConfig);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    public void send(List<KeyedMessage<String, String>> producerRecordList, ExecutorService pool) {
        Sender sender = new Sender(producer, producerRecordList);
        pool.submit(sender);
    }

    public void close() {
        producer.close();
    }

}

class Sender implements Runnable {

    private Producer<String, String> producer;
    private List<KeyedMessage<String, String>> keyedMessageList;

    protected Sender(Producer<String, String> producer, List<KeyedMessage<String, String>> keyedMessageList) {
        this.producer = producer;
        this.keyedMessageList = keyedMessageList;
    }

    @Override
    public void run() {
        try {
            producer.send(keyedMessageList);
        } catch (Exception e) {
            throw e;
        }
    }
}