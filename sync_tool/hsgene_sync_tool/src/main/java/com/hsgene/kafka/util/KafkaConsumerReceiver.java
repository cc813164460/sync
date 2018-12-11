package com.hsgene.kafka.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 14:38 2017/10/24
 * @Modified By:
 */
public class KafkaConsumerReceiver {

    private final ConsumerConnector consumer;
    private final String topic;
    private ExecutorService executor;

    public KafkaConsumerReceiver(String zookeeper, String groupid, String aTopic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(ConsumerProps(zookeeper, groupid));
        this.topic = aTopic;
    }

    public void run(int threads) {
        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        topicMap.put(topic, new Integer(threads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        executor = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, new
            LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder().setNameFormat("pool-%d").build(), new
            ThreadPoolExecutor.AbortPolicy());
        int numThread = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new Receiver(stream, numThread));
            numThread++;
        }
    }

    private static ConsumerConfig ConsumerProps(String zookeeper, String groupid) {
        Properties properties = new Properties(); // config properties file
        properties.put("zookeeper.connect", zookeeper);
        properties.put("group.id", groupid);
        properties.put("zookeeper.session.timeout.ms", "400");
        properties.put("zookeeper.sync.time.ms", "200");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "smallest");
        return new ConsumerConfig(properties);
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
        }
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }

}

class Receiver implements Runnable {

    private KafkaStream aStream;
    private int aThread;

    public Receiver(KafkaStream stream, int thread) {
        aStream = stream;
        aThread = thread;
    }

    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> iterator = aStream.iterator();
        while (iterator.hasNext()) {
            System.out.println("Thread " + aThread + ": " + new String(iterator.next().message()));
        }
        System.out.println("Shutting down Thread: " + aThread);
    }
}
