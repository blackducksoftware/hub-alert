/*
 * channel-api
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
package com.synopys.integration.alert.channel.api.issue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.alert.common.channel.issuetracker.message.IssueTrackerIssueResponseModel;
import com.synopsys.integration.alert.common.channel.issuetracker.message.IssueTrackerResponse;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.function.ThrowingFunction;
import com.synopys.integration.alert.channel.api.issue.model.IssueTrackerModelHolder;

public class IssueTrackerMessageSender<T extends Serializable> {
    private final IssueTrackerIssueCreator issueCreator;
    private final IssueTrackerIssueTransitioner<T> issueTransitioner;
    private final IssueTrackerIssueCommentCreator<T> issueCommentCreator;

    public IssueTrackerMessageSender(IssueTrackerIssueCreator issueCreator, IssueTrackerIssueTransitioner<T> issueTransitioner, IssueTrackerIssueCommentCreator<T> issueCommentCreator) {
        this.issueCreator = issueCreator;
        this.issueTransitioner = issueTransitioner;
        this.issueCommentCreator = issueCommentCreator;
    }

    public final IssueTrackerResponse sendMessages(List<IssueTrackerModelHolder<T>> channelMessages) throws AlertException {
        List<IssueTrackerIssueResponseModel> responses = new LinkedList<>();
        for (IssueTrackerModelHolder<T> channelMessage : channelMessages) {
            List<IssueTrackerIssueResponseModel> creationResponses = sendMessages(channelMessage.getIssueCreationModels(), issueCreator::createIssue);
            responses.addAll(creationResponses);

            List<IssueTrackerIssueResponseModel> transitionResponses = sendOptionalMessages(channelMessage.getIssueTransitionModels(), issueTransitioner::transitionIssue);
            responses.addAll(transitionResponses);

            List<IssueTrackerIssueResponseModel> commentResponses = sendOptionalMessages(channelMessage.getIssueCommentModels(), issueCommentCreator::commentOnIssue);
            responses.addAll(commentResponses);
        }
        return new IssueTrackerResponse("Success", responses);
    }

    private <U> List<IssueTrackerIssueResponseModel> sendMessages(List<U> messages, ThrowingFunction<U, IssueTrackerIssueResponseModel, AlertException> sendMessage) throws AlertException {
        List<IssueTrackerIssueResponseModel> responses = new LinkedList<>();
        for (U message : messages) {
            IssueTrackerIssueResponseModel response = sendMessage.apply(message);
            responses.add(response);
        }
        return responses;
    }

    private <U> List<IssueTrackerIssueResponseModel> sendOptionalMessages(List<U> messages, ThrowingFunction<U, Optional<IssueTrackerIssueResponseModel>, AlertException> sendMessage) throws AlertException {
        List<IssueTrackerIssueResponseModel> responses = new LinkedList<>();
        for (U message : messages) {
            sendMessage
                .apply(message)
                .ifPresent(responses::add);
        }
        return responses;
    }

}