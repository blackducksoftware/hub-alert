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
package com.blackducksoftware.integration.alert.channel.hipchat.descriptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.alert.common.ContentConverter;
import com.blackducksoftware.integration.alert.common.descriptor.config.TypeConverter;
import com.blackducksoftware.integration.alert.database.channel.hipchat.HipChatGlobalConfigEntity;
import com.blackducksoftware.integration.alert.database.entity.DatabaseEntity;
import com.blackducksoftware.integration.alert.web.channel.model.HipChatGlobalConfig;
import com.blackducksoftware.integration.alert.web.model.Config;

@Component
public class HipChatGlobalContentConverter extends TypeConverter {

    @Autowired
    public HipChatGlobalContentConverter(final ContentConverter contentConverter) {
        super(contentConverter);
    }

    @Override
    public Config getConfigFromJson(final String json) {
        return getContentConverter().getJsonContent(json, HipChatGlobalConfig.class);
    }

    @Override
    public DatabaseEntity populateEntityFromConfig(final Config restModel) {
        final HipChatGlobalConfig hipChatRestModel = (HipChatGlobalConfig) restModel;
        final HipChatGlobalConfigEntity hipChatEntity = new HipChatGlobalConfigEntity(hipChatRestModel.getApiKey(), hipChatRestModel.getHostServer());
        addIdToEntityPK(hipChatRestModel.getId(), hipChatEntity);
        return hipChatEntity;
    }

    @Override
    public Config populateConfigFromEntity(final DatabaseEntity entity) {
        final HipChatGlobalConfigEntity hipChatEntity = (HipChatGlobalConfigEntity) entity;
        final String id = getContentConverter().getStringValue(hipChatEntity.getId());
        final boolean isApiKeySet = StringUtils.isNotBlank(hipChatEntity.getApiKey());
        final HipChatGlobalConfig hipChatRestModel = new HipChatGlobalConfig(id, hipChatEntity.getApiKey(), isApiKeySet, hipChatEntity.getHostServer());
        return hipChatRestModel;
    }

}
