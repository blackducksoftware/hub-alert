/*
 * channel
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.alert.channel.email2;

import org.springframework.stereotype.Component;

import com.synopys.integration.alert.channel.api.convert.ChannelMessageFormatter;

@Component
public class EmailChannelMessageFormatter extends ChannelMessageFormatter {
    private static final int MAX_EMAIL_BODY_LENGTH = Integer.MAX_VALUE;
    private static final String EMAIL_LINE_SEPARATOR = "<br/>";
    private static final String EMAIL_NON_BREAKING_SPACE = "&nbsp;";

    public EmailChannelMessageFormatter() {
        super(MAX_EMAIL_BODY_LENGTH, EMAIL_LINE_SEPARATOR, ChannelMessageFormatter.DEFAULT_SECTION_SEPARATOR, EMAIL_NON_BREAKING_SPACE);
    }

    @Override
    public String encode(String txt) {
        return txt;
    }

    @Override
    public String emphasize(String txt) {
        return String.format("<strong>%s</strong>", txt);
    }

    @Override
    protected String createLink(String txt, String url) {
        return String.format("<a href=\"%s\">%s</a>", url, txt);
    }

}