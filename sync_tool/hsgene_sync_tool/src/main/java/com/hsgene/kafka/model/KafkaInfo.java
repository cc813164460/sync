package com.hsgene.kafka.model;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 16:24 2017/11/7
 * @Modified By:
 */
public class KafkaInfo {
    /**
     * 192.168.1.110:9092,192.168.1.111:9093,192.168.1.112:9094
     */
    private String metadataBrokerList;
    /**
     * 192.168.1.110:2181,192.168.1.111:2181,192.168.1.112:2181
     */
    private String zookeeperConnect;
    /**
     * kafka.serializer.StringEncoder
     */
    private String serializerClass = "kafka.serializer.StringEncoder";
    /**
     * idoall.testkafka.Partitionertest
     */
    private String partitionerClass;
    /**
     * 1
     */
    private String requestRequiredAcks;
    /**
     * 6
     */
    private String numPartitions;

    private  String topic;

    public String getMetadataBrokerList() {
        return metadataBrokerList;
    }

    public void setMetadataBrokerList(String metadataBrokerList) {
        this.metadataBrokerList = metadataBrokerList;
    }

    public String getZookeeperConnect() {
        return zookeeperConnect;
    }

    public void setZookeeperConnect(String zookeeperConnect) {
        this.zookeeperConnect = zookeeperConnect;
    }

    public String getSerializerClass() {
        return serializerClass;
    }

    public void setSerializerClass(String serializerClass) {
        this.serializerClass = serializerClass;
    }

    public String getPartitionerClass() {
        return partitionerClass;
    }

    public void setPartitionerClass(String partitionerClass) {
        this.partitionerClass = partitionerClass;
    }

    public String getRequestRequiredAcks() {
        return requestRequiredAcks;
    }

    public void setRequestRequiredAcks(String requestRequiredAcks) {
        this.requestRequiredAcks = requestRequiredAcks;
    }

    public String getNumPartitions() {
        return numPartitions;
    }

    public void setNumPartitions(String numPartitions) {
        this.numPartitions = numPartitions;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "KafkaInfo{" +
               "metadataBrokerList='" + metadataBrokerList + '\'' +
               ", zookeeperConnect='" + zookeeperConnect + '\'' +
               ", serializerClass='" + serializerClass + '\'' +
               ", partitionerClass='" + partitionerClass + '\'' +
               ", requestRequiredAcks='" + requestRequiredAcks + '\'' +
               ", numPartitions='" + numPartitions + '\'' +
               ", topic='" + topic + '\'' +
               '}';
    }
}
