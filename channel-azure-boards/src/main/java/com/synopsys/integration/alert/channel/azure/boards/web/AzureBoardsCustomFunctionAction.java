/*
 * channel-azure-boards
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.channel.azure.boards.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.channel.azure.boards.AzureBoardsProperties;
import com.synopsys.integration.alert.channel.azure.boards.AzureRedirectUrlCreator;
import com.synopsys.integration.alert.channel.azure.boards.descriptor.AzureBoardsDescriptor;
import com.synopsys.integration.alert.channel.azure.boards.oauth.OAuthRequestValidator;
import com.synopsys.integration.alert.channel.azure.boards.oauth.storage.AzureBoardsCredentialDataStoreFactory;
import com.synopsys.integration.alert.common.action.ActionResponse;
import com.synopsys.integration.alert.common.action.CustomFunctionAction;
import com.synopsys.integration.alert.common.action.api.ConfigResourceActions;
import com.synopsys.integration.alert.common.descriptor.DescriptorMap;
import com.synopsys.integration.alert.common.descriptor.config.field.endpoint.oauth.OAuthEndpointResponse;
import com.synopsys.integration.alert.common.descriptor.config.field.validation.FieldValidationUtility;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.accessor.FieldUtility;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationModel;
import com.synopsys.integration.alert.common.persistence.util.ConfigurationFieldModelConverter;
import com.synopsys.integration.alert.common.rest.AlertWebServerUrlManager;
import com.synopsys.integration.alert.common.rest.HttpServletContentWrapper;
import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.common.rest.proxy.ProxyManager;
import com.synopsys.integration.alert.common.security.authorization.AuthorizationManager;
import com.synopsys.integration.azure.boards.common.http.AzureHttpServiceFactory;
import com.synopsys.integration.azure.boards.common.oauth.AzureOAuthScopes;
import com.synopsys.integration.rest.proxy.ProxyInfo;

@Component
public class AzureBoardsCustomFunctionAction extends CustomFunctionAction<OAuthEndpointResponse> {
    private final Logger logger = LoggerFactory.getLogger(AzureBoardsCustomFunctionAction.class);

    private final ConfigurationAccessor configurationAccessor;
    private final ConfigurationFieldModelConverter modelConverter;
    private final AzureBoardsCredentialDataStoreFactory azureBoardsCredentialDataStoreFactory;
    private final AzureRedirectUrlCreator azureRedirectUrlCreator;
    private final ProxyManager proxyManager;
    private final OAuthRequestValidator oAuthRequestValidator;
    private final ConfigResourceActions configActions;
    private final AlertWebServerUrlManager alertWebServerUrlManager;

    @Autowired
    public AzureBoardsCustomFunctionAction(
        ConfigurationAccessor configurationAccessor,
        ConfigurationFieldModelConverter modelConverter,
        AzureBoardsCredentialDataStoreFactory azureBoardsCredentialDataStoreFactory,
        AzureRedirectUrlCreator azureRedirectUrlCreator,
        ProxyManager proxyManager, OAuthRequestValidator oAuthRequestValidator,
        ConfigResourceActions configActions,
        AuthorizationManager authorizationManager,
        DescriptorMap descriptorMap,
        FieldValidationUtility fieldValidationUtility,
        AlertWebServerUrlManager alertWebServerUrlManager) {
        super(AzureBoardsDescriptor.KEY_OAUTH, authorizationManager, descriptorMap, fieldValidationUtility);
        this.configurationAccessor = configurationAccessor;
        this.modelConverter = modelConverter;
        this.azureBoardsCredentialDataStoreFactory = azureBoardsCredentialDataStoreFactory;
        this.azureRedirectUrlCreator = azureRedirectUrlCreator;
        this.proxyManager = proxyManager;
        this.oAuthRequestValidator = oAuthRequestValidator;
        this.configActions = configActions;
        this.alertWebServerUrlManager = alertWebServerUrlManager;
    }

    @Override
    public ActionResponse<OAuthEndpointResponse> createActionResponse(FieldModel fieldModel, HttpServletContentWrapper servletContentWrapper) {
        try {
            Optional<FieldModel> savedFieldModel = saveIfValid(fieldModel);
            if (!savedFieldModel.isPresent()) {
                String error = "The configuration is invalid. Please test the configuration.";
                return new ActionResponse<>(HttpStatus.BAD_REQUEST, error, createErrorResponse(error));
            }
            FieldUtility fieldUtility = createFieldAccessor(savedFieldModel.get());
            Optional<String> clientId = fieldUtility.getString(AzureBoardsDescriptor.KEY_CLIENT_ID);
            if (!clientId.isPresent()) {
                return new ActionResponse<>(HttpStatus.BAD_REQUEST, createErrorResponse("App ID not found."));
            }

            Optional<String> alertServerUrl = alertWebServerUrlManager.getServerUrl();
            if (!alertServerUrl.isPresent()) {
                return new ActionResponse<>(HttpStatus.BAD_REQUEST, createErrorResponse("Could not determine the alert server url for the callback."));
            }

            String requestKey = createRequestKey();
            // since we have only one OAuth channel now remove any other requests.
            // if we have more OAuth clients then the "remove requests" will have to be removed from here.
            // beginning authentication process create the request id at the start.
            Instant fiveMinutesAgo = Instant.now().minusSeconds(300);
            oAuthRequestValidator.removeRequestsOlderThanInstant(fiveMinutesAgo);
            oAuthRequestValidator.addAuthorizationRequest(requestKey);

            logger.info("OAuth authorization request created: {}", requestKey);
            String authUrl = createAuthURL(clientId.get(), requestKey);
            logger.debug("Authenticating Azure OAuth URL: " + authUrl);
            return new ActionResponse<>(HttpStatus.OK, new OAuthEndpointResponse(isAuthenticated(fieldUtility), authUrl, "Authenticating..."));
        } catch (Exception ex) {
            logger.error("Error activating Azure Boards", ex);
            return new ActionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, createErrorResponse("Error activating azure oauth."));
        }
    }

    private OAuthEndpointResponse createErrorResponse(String errorMessage) {
        oAuthRequestValidator.removeAllRequests();
        return new OAuthEndpointResponse(false, "", errorMessage);
    }

    private Optional<FieldModel> saveIfValid(FieldModel fieldModel) {
        if (StringUtils.isNotBlank(fieldModel.getId())) {
            Long id = Long.parseLong(fieldModel.getId());
            ActionResponse<FieldModel> response = configActions.update(id, fieldModel);
            return response.getContent();
        } else {
            ActionResponse<FieldModel> response = configActions.create(fieldModel);
            return response.getContent();
        }
    }

    private FieldUtility createFieldAccessor(FieldModel fieldModel) {
        Map<String, ConfigurationFieldModel> fields = new HashMap<>(modelConverter.convertToConfigurationFieldModelMap(fieldModel));
        // check if a configuration exists because the client id is a sensitive field and won't have a value in the field model if updating.
        if (StringUtils.isNotBlank(fieldModel.getId())) {
            configurationAccessor.getConfigurationById(Long.valueOf(fieldModel.getId()))
                .map(ConfigurationModel::getCopyOfKeyToFieldMap)
                .ifPresent(fields::putAll);
        }
        return new FieldUtility(fields);
    }

    private boolean isAuthenticated(FieldUtility fieldUtility) {
        AzureBoardsProperties properties = AzureBoardsProperties.fromFieldAccessor(azureBoardsCredentialDataStoreFactory, azureRedirectUrlCreator.createOAuthRedirectUri(), fieldUtility);
        ProxyInfo proxy = proxyManager.createProxyInfoForHost(AzureHttpServiceFactory.DEFAULT_BASE_URL);
        return properties.hasOAuthCredentials(proxy);
    }

    private String createAuthURL(String clientId, String requestKey) {
        StringBuilder authUrlBuilder = new StringBuilder(300);
        authUrlBuilder.append(AzureHttpServiceFactory.DEFAULT_AUTHORIZATION_URL);
        authUrlBuilder.append(createQueryString(clientId, requestKey));
        return authUrlBuilder.toString();
    }

    private String createQueryString(String clientId, String requestKey) {
        List<String> scopes = List.of(AzureOAuthScopes.PROJECTS_READ.getScope(), AzureOAuthScopes.WORK_FULL.getScope());
        String authorizationUrl = azureRedirectUrlCreator.createOAuthRedirectUri();
        StringBuilder queryBuilder = new StringBuilder(250);
        queryBuilder.append("&client_id=");
        queryBuilder.append(clientId);
        queryBuilder.append("&state=");
        queryBuilder.append(requestKey);
        queryBuilder.append("&scope=");
        queryBuilder.append(URLEncoder.encode(StringUtils.join(scopes, " "), StandardCharsets.UTF_8));
        queryBuilder.append("&redirect_uri=");
        queryBuilder.append(URLEncoder.encode(authorizationUrl, StandardCharsets.UTF_8));
        return queryBuilder.toString();
    }

    private String createRequestKey() {
        UUID requestID = UUID.randomUUID();
        return String.format("%s-%s", "alert-oauth-request", requestID.toString());
    }

}
