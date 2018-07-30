/**
 * blackduck-alert
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.alert.channel.email;

import java.util.Map;
import java.util.Set;

import javax.jms.MessageListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.alert.channel.event.ChannelEvent;
import com.blackducksoftware.integration.alert.common.descriptor.ChannelDescriptor;
import com.blackducksoftware.integration.alert.database.channel.email.EmailDistributionRepositoryAccessor;
import com.blackducksoftware.integration.alert.database.channel.email.EmailGlobalConfigEntity;
import com.blackducksoftware.integration.alert.database.channel.email.EmailGlobalRepositoryAccessor;
import com.blackducksoftware.integration.alert.database.channel.email.EmailGroupDistributionConfigEntity;
import com.blackducksoftware.integration.alert.database.entity.DatabaseEntity;
import com.blackducksoftware.integration.alert.database.entity.EntityPropertyMapper;
import com.blackducksoftware.integration.alert.web.channel.model.EmailGlobalConfig;
import com.blackducksoftware.integration.alert.web.channel.model.EmailDistributionConfig;
import com.blackducksoftware.integration.alert.web.model.CommonDistributionConfig;
import com.blackducksoftware.integration.alert.web.model.Config;
import com.blackducksoftware.integration.alert.workflow.startup.AlertStartupProperty;
import com.blackducksoftware.integration.exception.IntegrationException;

@Component
public class EmailDescriptor extends ChannelDescriptor {
    public static final String NOT_AN_INTEGER = "Not an Integer.";
    private final EmailGroupChannel emailGroupChannel;
    private final EntityPropertyMapper entityPropertyMapper;

    @Autowired
    public EmailDescriptor(final EmailGroupChannel emailGroupChannel, final EmailGlobalContentConverter emailGlobalContentConverter, final EmailGlobalRepositoryAccessor emailGlobalRepositoryAccessor,
            final EmailDistributionContentConverter emailDistributionContentConverter, final EmailDistributionRepositoryAccessor emailDistributionRepositoryAccessor, final EntityPropertyMapper entityPropertyMapper) {
        super(EmailGroupChannel.COMPONENT_NAME, EmailGroupChannel.COMPONENT_NAME, emailGlobalContentConverter, emailGlobalRepositoryAccessor, emailDistributionContentConverter, emailDistributionRepositoryAccessor);
        this.emailGroupChannel = emailGroupChannel;
        this.entityPropertyMapper = entityPropertyMapper;
    }

    @Override
    public void validateDistributionConfig(final CommonDistributionConfig restModel, final Map<String, String> fieldErrors) {
        final EmailDistributionConfig emailRestModel = (EmailDistributionConfig) restModel;
        if (StringUtils.isBlank(emailRestModel.getGroupName())) {
            fieldErrors.put("groupName", "A group must be specified.");
        }
    }

    @Override
    public void testDistributionConfig(final CommonDistributionConfig restModel, final ChannelEvent event) throws IntegrationException {
        final EmailGroupDistributionConfigEntity emailEntity = (EmailGroupDistributionConfigEntity) super.getDistributionContentConverter().populateDatabaseEntityFromRestModel(restModel);
        emailGroupChannel.sendAuditedMessage(event, emailEntity);
    }

    @Override
    public MessageListener getChannelListener() {
        return emailGroupChannel;
    }

    @Override
    public void validateGlobalConfig(final Config restModel, final Map<String, String> fieldErrors) {
        final EmailGlobalConfig emailRestModel = (EmailGlobalConfig) restModel;

        if (StringUtils.isNotBlank(emailRestModel.getMailSmtpPort()) && !StringUtils.isNumeric(emailRestModel.getMailSmtpPort())) {
            fieldErrors.put("mailSmtpPort", NOT_AN_INTEGER);
        }
        if (StringUtils.isNotBlank(emailRestModel.getMailSmtpConnectionTimeout()) && !StringUtils.isNumeric(emailRestModel.getMailSmtpConnectionTimeout())) {
            fieldErrors.put("mailSmtpConnectionTimeout", NOT_AN_INTEGER);
        }
        if (StringUtils.isNotBlank(emailRestModel.getMailSmtpTimeout()) && !StringUtils.isNumeric(emailRestModel.getMailSmtpTimeout())) {
            fieldErrors.put("mailSmtpTimeout", NOT_AN_INTEGER);
        }
    }

    @Override
    public void testGlobalConfig(final DatabaseEntity entity) throws IntegrationException {
        throw new IntegrationException("Error, can't test global email configuration");
    }

    @Override
    public Set<AlertStartupProperty> getGlobalEntityPropertyMapping() {
        return entityPropertyMapper.mapEntityToProperties(getName(), EmailGlobalConfigEntity.class);
    }

    @Override
    public Config getGlobalRestModelObject() {
        return new EmailGlobalConfig();
    }

}
