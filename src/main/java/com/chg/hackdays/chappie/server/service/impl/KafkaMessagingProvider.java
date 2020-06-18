package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Conversation;
import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.model.MessageId;
import com.chg.hackdays.chappie.server.service.MessagingProvider;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Repository
public class KafkaMessagingProvider implements MessagingProvider {
    private static final Duration TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

    @Autowired
    private KafkaTemplate<String, Message> template;
    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory;

    @Override
    public void post(Message msg) {
        try {
            if(msg.getTopic()==null)
                msg.setTopic(template.getDefaultTopic());
            ListenableFuture<SendResult<String, Message>> send = this.template.send(msg.getTopic(), msg);
            SendResult<String, Message> sendResult = send.completable().get();
            msg.setId(new MessageId(msg.getTopic(),sendResult.getRecordMetadata().offset()));
        } catch (InterruptedException|ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> get(String topic, long first, int count) {
        if(topic==null)
            topic=template.getDefaultTopic();

        // This definitely isn't perfect, but it seems to work
        // TODO: Will block for full timeout period if there are no messages in the topic
        try(Consumer<? super String, ? super Message> consumer = kafkaListenerContainerFactory.getConsumerFactory().createConsumer()){
            List<Message> results = new ArrayList<>();

            AtomicBoolean assigned = new AtomicBoolean(false);
            ConsumerRebalanceListener callback = new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    for (TopicPartition partition : partitions) {
                        consumer.seek(partition, first);
                    }
                    synchronized (assigned){
                        assigned.set(true);
                        assigned.notify();
                    }
                }
            };
            consumer.subscribe(Collections.singleton(topic), callback);
            long timeout = System.currentTimeMillis() + TIMEOUT.toMillis();
            for(;;) {
                while (!assigned.get()){
                    synchronized (assigned) {
                        while (!assigned.get()){
                            try {
                                assigned.wait(10);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if (!assigned.get())
                                poll(consumer,results);
                        }
                    }
                }
                int pollCount = poll(consumer,results);
                if((!results.isEmpty() && pollCount==0) || (count > 0 && results.size() >= count) || System.currentTimeMillis() >= timeout)
                    break;
            }
            // Ensure the results are in the correct order
            return results.stream().sorted(Comparator.comparing(msg->new MessageId(msg.getId()))).collect(Collectors.toList());
        }
    }

    @Override
    public List<Conversation> getConversations(Long id, String participant) {
        return Collections.EMPTY_LIST;
    }

    private int poll(Consumer<? super String, ? super Message> consumer, List<Message> results) {
        ConsumerRecords<? super String, ? super Message> records = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<? super String, ? super Message> record : records) {
            Message msg = (Message) record.value();
            msg.setId(new MessageId(record.topic(),record.offset()));
            results.add(msg);
        }
        consumer.commitAsync();
        return records.count();
    }
}
