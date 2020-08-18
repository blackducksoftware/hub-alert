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
package com.synopsys.integration.alert.channel.azure.boards.oauth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Preconditions;

public class AzureAuthorizationCodeFlow extends AuthorizationCodeFlow {
    public static final String DEFAULT_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    public static final String DEFAULT_CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private final String clientSecret;
    private final String redirectUri;

    public AzureAuthorizationCodeFlow(Credential.AccessMethod method, HttpTransport transport, JsonFactory jsonFactory,
        GenericUrl tokenServerUrl, HttpExecuteInterceptor clientAuthentication, String clientId, String authorizationServerEncodedUrl, String clientSecret, String redirectUri) {
        super(method, transport, jsonFactory, tokenServerUrl, clientAuthentication, clientId, authorizationServerEncodedUrl);
        this.clientSecret = Preconditions.checkNotNull(clientSecret);
        this.redirectUri = Preconditions.checkNotNull(redirectUri);
    }

    protected AzureAuthorizationCodeFlow(Builder builder) {
        super(builder);
        this.clientSecret = Preconditions.checkNotNull(builder.clientSecret);
        this.redirectUri = Preconditions.checkNotNull(builder.redirectUri);
    }

    @Override
    public AuthorizationCodeTokenRequest newTokenRequest(String authorizationCode) {
        AuthorizationCodeTokenRequest request = super.newTokenRequest(authorizationCode);
        request.setGrantType(DEFAULT_GRANT_TYPE);
        request.setResponseClass(AzureTokenResponse.class);
        request.put("assertion", authorizationCode);
        request.put("client_assertion_type", DEFAULT_CLIENT_ASSERTION_TYPE);
        request.put("client_assertion", clientSecret);
        request.put("redirect_uri", redirectUri);
        return request;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public static class Builder extends AuthorizationCodeFlow.Builder {
        private String clientSecret;
        private String redirectUri;

        public Builder(Credential.AccessMethod method, HttpTransport transport, JsonFactory jsonFactory, GenericUrl tokenServerUrl, HttpExecuteInterceptor clientAuthentication, String clientId,
            String authorizationServerEncodedUrl, String clientSecret, String redirectUri) {
            super(method, transport, jsonFactory, tokenServerUrl, clientAuthentication, clientId, authorizationServerEncodedUrl);
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
        }

        @Override
        public AuthorizationCodeFlow build() {
            return new AzureAuthorizationCodeFlow(this);
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public Builder setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }
    }
}