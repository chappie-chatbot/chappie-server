package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Repository;

import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Repository
public class KafkaMessagingProvider implements MessagingProvider {
    private static final Duration TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

    @Autowired
    private KafkaTemplate<String, Message> template;
    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory;

    @Override
    public void post(Message msg) {
        if(msg.getTopic()==null)
            msg.setTopic(template.getDefaultTopic());
        this.template.send(msg.getTopic(), msg);
    }

    @Override
    public List<Message> get(String topic, int first, int count) {
        if(topic==null)
            topic=template.getDefaultTopic();

        // This definitely isn't perfect, but it seems to work
        // TODO: Will block for full timeout period if there are no messages in the topic
        try(Consumer<? super String, ? super Message> consumer = kafkaListenerContainerFactory.getConsumerFactory().createConsumer()){
            consumer.subscribe(Collections.singleton(topic));
            List<Message> results = new ArrayList<>();
            long timeout = System.currentTimeMillis() + TIMEOUT.toMillis();
            for(;;) {
                consumer.seekToBeginning(consumer.assignment());
                ConsumerRecords<? super String, ? super Message> records = consumer.poll(TIMEOUT.dividedBy(100));
                for (ConsumerRecord<? super String, ? super Message> record : records) {
                    results.add((Message) record.value());
                }
                if((!results.isEmpty() && records.isEmpty()) || System.currentTimeMillis() >= timeout)
                    break;
                consumer.commitAsync();
            }
            return results;
        }
    }

    public class MyListener implements ConsumerSeekAware {
        List<Message> messages = new LinkedList<>();
        boolean done = false;

        @KafkaListener(topics = "${kafka.topic}", containerFactory = "kafkaListenerContainerFactoryListener")
        public void receiveMessage(final Message message) {
            messages.add(message);
        }

        @Override
        public void registerSeekCallback(final ConsumerSeekCallback consumerSeekCallback) {}

        @Override
        public void onPartitionsAssigned(final Map<TopicPartition, Long> assignments, final ConsumerSeekCallback consumerSeekCallback) {
            try(Consumer<? super String, ? super Message> consumer = kafkaListenerContainerFactory.getConsumerFactory().createConsumer()) {
                final Map<TopicPartition, Long> topicPartitionLongMap = consumer.endOffsets(assignments.keySet());
                assignments.forEach((topic, action) -> consumerSeekCallback.seekToBeginning(consumer.assignment()));
            }
        }

        @Override
        public void onIdleContainer(final Map<TopicPartition, Long> map, final ConsumerSeekCallback consumerSeekCallback) {
            done=true;
        }
    }
}
