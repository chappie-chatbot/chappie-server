package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.model.Message;
import com.chg.hackdays.chappie.model.MessageId;
import com.chg.hackdays.chappie.server.service.ChatbotProvider;
import com.chg.hackdays.chappie.server.service.ChatbotService;
import com.chg.hackdays.chappie.server.service.MessageService;
import com.chg.hackdays.chappie.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    private static final String TOPIC_CHAT = "chat";
    private static final String KAFKA_GROUP_ID = "chappie-chat";
    private static final String ATTR_SKIP = "chat.skip";
    private static final String USER_CHAPPIE = "chappie";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    MessageService messageService;
    @Autowired
    ChatbotProvider chatbotProvider;

    @Override
    public List<Message> exchange(List<Message> messages) {
        // Skip these messages when they are subsequently consumed by the chat listener also in this class
        for (Message message : messages) {
            message.getAttributes().put(ATTR_SKIP, "1");
            message.setTarget(USER_CHAPPIE);
            message.setTopic(TOPIC_CHAT);
        }
        messageService.postMessages(messages);
        return processMessages(messages);
    }

    @KafkaListener(topics = TOPIC_CHAT, groupId = KAFKA_GROUP_ID)
    public void listen(GenericMessage<Message> genericMessage, @Payload Message message) {
        try {
            message.setId(new MessageId(message.getTopic(), (Long) genericMessage.getHeaders().get("kafka_offset")));
            boolean isFromChatBot = message.getSource() != null && message.getSource().toLowerCase().startsWith(USER_CHAPPIE.toLowerCase());
            boolean isToChatBot = message.getTarget() == null || message.getTarget().toLowerCase().startsWith(USER_CHAPPIE.toLowerCase());
            boolean skip = StringUtil.isTruthy(message.getAttributes().getOrDefault(ATTR_SKIP, null));
            if (!isFromChatBot && isToChatBot && !skip) {
                processMessages(Collections.singleton(message));
            }
        } catch (Exception e) {
            log.error("Failed to process chat message", e);
        }
    }

    private List<Message> processMessages(Collection<Message> messages) {
        List<Message> replies = chatbotProvider.exchange(messages);
        for (Message reply : replies) {
            if (reply.getSource() == null) {
                reply.setSource(USER_CHAPPIE);
            }
        }
        messageService.postMessages(replies);
        return replies;
    }
}
