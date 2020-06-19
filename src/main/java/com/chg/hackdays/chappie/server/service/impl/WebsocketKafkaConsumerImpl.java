package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.model.MessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
public class WebsocketKafkaConsumerImpl {
    private static final String TOPIC_CHAT = "chat";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = TOPIC_CHAT)
    public void listen(GenericMessage<Message> genericMessage, @Payload Message message) {
        try {
            message.setId(new MessageId(message.getTopic(), (Long) genericMessage.getHeaders().get("kafka_offset")));
            log.debug("Message: " + message.getId());
            simpMessagingTemplate.convertAndSend("/topic/messages/" + message.getConversation(), message);
        } catch (Exception e) {
            log.error("Failed to process chat message", e);
        }
    }
}
