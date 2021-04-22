/*
 * channel
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.channel.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.exception.AlertRuntimeException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.RestConstants;
import com.synopsys.integration.rest.body.StringBodyContent;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

@Component
public class RestChannelUtility {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ChannelRestConnectionFactory channelRestConnectionFactory;

    @Autowired
    public RestChannelUtility(ChannelRestConnectionFactory channelRestConnectionFactory) {
        this.channelRestConnectionFactory = channelRestConnectionFactory;
    }

    //TODO: Is this used anywhere? Consider removing it if not, otherwise have it return a RestMessageResponse
    public void sendSingleMessage(Request request, String eventDestination) throws AlertException {
        sendMessage(List.of(request), eventDestination);
    }

    //Idea: Run the same code here as before, move exception handling into sendMessageRequest
    // have it return a new object with success/failure status. Map it to the request
    //return a status object?
    // - I sent this many successfully
    // - This many came back with an error
    public RestMessageResponse sendMessage(List<Request> requests, String eventDestination) {
        IntHttpClient intHttpClient = getIntHttpClient();
        RestMessageResponse messageResponse = new RestMessageResponse();
        for (Request request : requests) {
            RestResponseStatus status = sendMessageRequest(intHttpClient, request, eventDestination);
            messageResponse.add(request, status);
        }
        return messageResponse;
        /*
        try {
            IntHttpClient intHttpClient = getIntHttpClient();
            for (Request request : requests) {
                sendMessageRequest(intHttpClient, request, eventDestination); //have this return a status object
            }
        } catch (AlertException alertException) {
            throw alertException;
        } catch (Exception ex) {
            throw new AlertException(ex);
        }
         */
    }

    public Request createPostMessageRequest(String url, Map<String, String> headers, String jsonString) {
        return createPostMessageRequest(url, headers, null, jsonString);
    }

    public Request createPostMessageRequest(String url, Map<String, String> headers, Map<String, Set<String>> queryParameters) {
        return createPostMessageRequest(url, headers, queryParameters, null);
    }

    public Request createPostMessageRequest(String url, Map<String, String> headers, Map<String, Set<String>> queryParameters, String jsonString) {
        HttpUrl httpUrl;
        try {
            httpUrl = new HttpUrl(url);
        } catch (IntegrationException e) {
            throw new AlertRuntimeException(e);
        }

        Request.Builder requestBuilder = new Request.Builder().method(HttpMethod.POST)
                                             .url(httpUrl);
        requestBuilder.getHeaders().putAll(headers);
        if (queryParameters != null && !queryParameters.isEmpty()) {
            requestBuilder.queryParameters(queryParameters);
        }
        if (jsonString != null) {
            requestBuilder.bodyContent(new StringBodyContent(jsonString));
        }
        return requestBuilder.build();
    }

    //have this return something
    //may not need the AlertException anymore
    //public void sendMessageRequest(IntHttpClient intHttpClient, Request request, String messageType) throws AlertException {
    public RestResponseStatus sendMessageRequest(IntHttpClient intHttpClient, Request request, String messageType) {
        logger.info("Attempting to send a {} message...", messageType);
        try (Response response = sendGenericRequest(intHttpClient, request)) {
            if (RestConstants.OK_200 <= response.getStatusCode() && response.getStatusCode() < RestConstants.MULT_CHOICE_300) {
                logger.info("Successfully sent a {} message!", messageType);
                return RestResponseStatus.success();
            } else {
                //throw new AlertException(String.format("Could not send message: %s. Status code: %s", response.getStatusMessage(), response.getStatusCode()));
                return RestResponseStatus.failure(String.format("Could not send message: %s. Status code: %s", response.getStatusMessage(), response.getStatusCode()));
            }
        } catch (Exception e) {
            logger.error("Error sending request", e);
            //throw new AlertException(e.getMessage(), e);
            return RestResponseStatus.failure(e.getMessage());
        }
    }

    public Response sendGenericRequest(IntHttpClient intHttpClient, Request request) throws IntegrationException {
        Response response = intHttpClient.execute(request);
        logger.trace("Response: {}", response);
        return response;
    }

    public IntHttpClient getIntHttpClient() {
        return channelRestConnectionFactory.createIntHttpClient();
    }

}
