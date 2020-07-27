/**
 * blackduck-alert
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.alert.channel.azure.boards.service;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.synopsys.integration.alert.channel.azure.boards.AzureBoardsContext;
import com.synopsys.integration.alert.common.channel.issuetracker.message.IssueTrackerRequest;
import com.synopsys.integration.alert.common.channel.issuetracker.message.IssueTrackerResponse;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.rest.ProxyManager;
import com.synopsys.integration.azure.boards.common.http.AzureHttpService;
import com.synopsys.integration.azure.boards.common.http.AzureHttpServiceFactory;
import com.synopsys.integration.azure.boards.common.service.workitem.AzureWorkItemService;
import com.synopsys.integration.exception.IntegrationException;

public class AzureBoardsRequestDelegator {
    private final Gson gson;
    private final ProxyManager proxyManager;
    private final AzureBoardsContext context;
    private final AzureBoardsMessageParser azureBoardsMessageParser;

    public AzureBoardsRequestDelegator(Gson gson, ProxyManager proxyManager, AzureBoardsContext context, AzureBoardsMessageParser azureBoardsMessageParser) {
        this.gson = gson;
        this.proxyManager = proxyManager;
        this.context = context;
        this.azureBoardsMessageParser = azureBoardsMessageParser;
    }

    public IssueTrackerResponse sendRequests(List<IssueTrackerRequest> requests) throws IntegrationException {
        AzureBoardsProperties azureBoardsProperties = context.getIssueTrackerConfig();
        NetHttpTransport httpTransport = azureBoardsProperties.createHttpTransport(createJavaProxy());

        Credential oAuthCredential;
        try {
            AuthorizationCodeFlow oAuthFlow = azureBoardsProperties.createOAuthFlow(httpTransport);
            oAuthCredential = azureBoardsProperties.getExistingOAuthCredential(oAuthFlow)
                                  .orElseThrow(() -> new AlertException("No stored OAuth credential for Azure Boards exists"));
        } catch (IOException e) {
            throw new IntegrationException("Cannot initialize OAuth for Azure Boards", e);
        }

        AzureHttpService azureHttpService = AzureHttpServiceFactory.withCredential(httpTransport, oAuthCredential, gson);
        AzureWorkItemService azureWorkItemService = new AzureWorkItemService(azureHttpService);

        // TODO consider creating an Alert custom field if one does not exist for issue lookup

        AzureBoardsIssueHandler issueHandler = new AzureBoardsIssueHandler(azureBoardsProperties, azureBoardsMessageParser, azureWorkItemService);
        return issueHandler.createOrUpdateIssues(context.getIssueConfig(), requests);
    }

    private Proxy createJavaProxy() {
        return proxyManager.createProxyInfo()
                   .getProxy()
                   .orElse(Proxy.NO_PROXY);
    }

}