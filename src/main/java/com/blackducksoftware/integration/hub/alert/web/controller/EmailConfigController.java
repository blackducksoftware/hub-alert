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
package com.blackducksoftware.integration.hub.alert.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.alert.channel.email.EmailChannel;
import com.blackducksoftware.integration.hub.alert.datasource.entity.EmailConfigEntity;
import com.blackducksoftware.integration.hub.alert.datasource.repository.EmailRepository;
import com.blackducksoftware.integration.hub.alert.web.actions.EmailConfigActions;
import com.blackducksoftware.integration.hub.alert.web.model.EmailConfigRestModel;

@RestController
public class EmailConfigController implements ConfigController<EmailConfigEntity, EmailConfigRestModel> {
    private final EmailConfigActions configActions;
    private final CommonConfigController<EmailConfigEntity, EmailConfigRestModel> commonConfigController;

    EmailConfigController(final EmailConfigActions configActions) {
        this.configActions = configActions;
        commonConfigController = new CommonConfigController<>(EmailConfigEntity.class, EmailConfigRestModel.class, configActions);
    }

    @GetMapping(value = "/configuration/email")
    public List<EmailConfigRestModel> getConfig(@RequestParam(value = "id", required = false) final Long id) throws IntegrationException {
        return configActions.getConfig(id);
    }

    @Override
    @PostMapping(value = "/configuration/email")
    public ResponseEntity<String> postConfig(@RequestAttribute(value = "emailConfig", required = true) @RequestBody final EmailConfigRestModel emailConfig) throws IntegrationException {
        return commonConfigController.postConfig(emailConfig);
    }

    @Override
    @PutMapping(value = "/configuration/email")
    public ResponseEntity<String> putConfig(@RequestAttribute(value = "emailConfig", required = true) @RequestBody final EmailConfigRestModel emailConfig) throws IntegrationException {
        return commonConfigController.putConfig(emailConfig);
    }

    @Override
    public ResponseEntity<String> validateConfig(final EmailConfigRestModel emailConfig) {
        // TODO
        return null;
    }

    @Override
    @DeleteMapping(value = "/configuration/email")
    public ResponseEntity<String> deleteConfig(@RequestAttribute(value = "emailConfig", required = true) @RequestBody final EmailConfigRestModel emailConfig) {
        return commonConfigController.deleteConfig(emailConfig);
    }

    @Override
    @PostMapping(value = "/configuration/email/test")
    public ResponseEntity<String> testConfig(@RequestAttribute(value = "emailConfig", required = true) final EmailConfigRestModel emailConfig) throws IntegrationException {
        final Long id = configActions.objectTransformer.stringToLong(emailConfig.getId());
        final EmailChannel channel = new EmailChannel(null, null, (EmailRepository) configActions.repository);
        final String responseMessage = channel.testMessage(configActions.objectTransformer.transformObject(emailConfig, EmailConfigEntity.class));
        return commonConfigController.createResponse(HttpStatus.OK, id, responseMessage);
    }

}
