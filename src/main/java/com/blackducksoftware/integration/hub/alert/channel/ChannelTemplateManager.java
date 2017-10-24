/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.alert.channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.alert.AbstractJmsTemplate;
import com.blackducksoftware.integration.hub.alert.event.AbstractChannelEvent;
import com.google.gson.Gson;

public class ChannelTemplateManager {
    private final Map<String, AbstractJmsTemplate> jmsTemplateMap;
    private final Gson gson;

    public ChannelTemplateManager(final Gson gson) {
        jmsTemplateMap = new HashMap<>();
        this.gson = gson;
    }

    public boolean hasTemplate(final String destination) {
        return jmsTemplateMap.containsKey(destination);
    }

    public AbstractJmsTemplate getTemplate(final String destination) {
        return jmsTemplateMap.get(destination);
    }

    public void addTemplate(final String destination, final AbstractJmsTemplate template) {
        jmsTemplateMap.put(destination, template);
    }

    public void sendEvents(final List<AbstractChannelEvent> eventList) {
        if (!eventList.isEmpty()) {
            eventList.forEach(event -> {
                sendEvent(event);

            });
        }
    }

    public boolean sendEvent(final AbstractChannelEvent event) {
        final String destination = event.getTopic();
        if (hasTemplate(destination)) {
            final String jsonMessage = gson.toJson(event);
            final AbstractJmsTemplate template = getTemplate(destination);
            template.convertAndSend(destination, jsonMessage);
            return true;
        } else {
            return false;
        }
    }
}
