package com.chg.hackdays.chappie.server.service.impl;

import com.chg.hackdays.chappie.server.service.SessionService;
import com.chg.hackdays.chappie.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class SessionServiceImpl implements SessionService {
    private static final String ATTR_USERID = "chappie.userId";
    private static final String ATTR_USERNAME = "chappie.username";
    private static final String ATTR_CONVERSATION = "chappie.conversation";

    @Autowired
    HttpSession session;

    @Override
    public String getSessionId(){
        return session.getId();
    }

    @Override
    public Long getCurrentConversationId() {
        return getAttributeLong(ATTR_CONVERSATION, 1L);
    }

    @Override
    public void setCurrentConversationId(long conversationId) {
        session.setAttribute(ATTR_CONVERSATION, Long.toString(conversationId));
    }

    @Override
    public String getUsername() {
        return getAttribute(ATTR_USERNAME,"user");
    }

    @Override
    public void setUsername(String username) {
        session.setAttribute(ATTR_USERNAME, username);
    }

    @Override
    public Long getUserId() {
        return getAttributeLong(ATTR_USERID,2L);
    }

    @Override
    public void setUserId(long id) {
        session.setAttribute(ATTR_USERID, Long.toString(id));
    }

    private String getAttribute(String attrName, String defaultValue) {
        String value = StringUtil.toString(session.getAttribute(attrName));
        if (value == null)
            return defaultValue;
        return value;
    }

    private long getAttributeLong(String attrName, long defaultValue) {
        String value = StringUtil.toString(session.getAttribute(attrName));
        if (value == null)
            return defaultValue;
        return Long.parseLong(value);
    }
}
