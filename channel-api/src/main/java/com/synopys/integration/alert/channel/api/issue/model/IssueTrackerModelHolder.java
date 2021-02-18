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
package com.synopys.integration.alert.channel.api.issue.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

public class IssueTrackerModelHolder<T extends Serializable> {
    private final List<IssueCreationModel> issueCreationModels;
    private final List<IssueTransitionModel<T>> issueTransitionModels;
    private final List<IssueCommentModel<T>> issueCommentModels;

    public static <T extends Serializable> IssueTrackerModelHolder<T> reduce(IssueTrackerModelHolder<T> lhs, IssueTrackerModelHolder<T> rhs) {
        List<IssueCreationModel> unifiedIssueCreationModels = ListUtils.union(lhs.getIssueCreationModels(), rhs.getIssueCreationModels());
        List<IssueTransitionModel<T>> unifiedIssueTransitionModels = ListUtils.union(lhs.getIssueTransitionModels(), rhs.getIssueTransitionModels());
        List<IssueCommentModel<T>> unifiedIssueCommentModels = ListUtils.union(lhs.getIssueCommentModels(), rhs.getIssueCommentModels());
        return new IssueTrackerModelHolder<>(unifiedIssueCreationModels, unifiedIssueTransitionModels, unifiedIssueCommentModels);
    }

    public IssueTrackerModelHolder(List<IssueCreationModel> issueCreationModels, List<IssueTransitionModel<T>> issueTransitionModels,
        List<IssueCommentModel<T>> issueCommentModels) {
        this.issueCreationModels = issueCreationModels;
        this.issueTransitionModels = issueTransitionModels;
        this.issueCommentModels = issueCommentModels;
    }

    public List<IssueCreationModel> getIssueCreationModels() {
        return issueCreationModels;
    }

    public List<IssueTransitionModel<T>> getIssueTransitionModels() {
        return issueTransitionModels;
    }

    public List<IssueCommentModel<T>> getIssueCommentModels() {
        return issueCommentModels;
    }

}