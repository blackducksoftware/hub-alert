/**
 * alert-common
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
package com.synopsys.integration.alert.common.persistence.model.job.details;

import java.util.List;

import com.synopsys.integration.alert.descriptor.api.model.ChannelKey;

public class JiraCloudJobDetailsModel extends DistributionJobDetailsModel {
    private final boolean addComments;
    private final String issueCreatorEmail;
    private final String projectNameOrKey;
    private final String issueType;
    private final String resolveTransition;
    private final String reopenTransition;
    private final List<JiraJobCustomFieldModel> customFields;

    public JiraCloudJobDetailsModel(
        boolean addComments,
        String issueCreatorEmail,
        String projectNameOrKey,
        String issueType,
        String resolveTransition,
        String reopenTransition,
        List<JiraJobCustomFieldModel> customFields
    ) {
        super(ChannelKey.JIRA_CLOUD);
        this.addComments = addComments;
        this.issueCreatorEmail = issueCreatorEmail;
        this.projectNameOrKey = projectNameOrKey;
        this.issueType = issueType;
        this.resolveTransition = resolveTransition;
        this.reopenTransition = reopenTransition;
        this.customFields = customFields;
    }

    public boolean isAddComments() {
        return addComments;
    }

    public String getIssueCreatorEmail() {
        return issueCreatorEmail;
    }

    public String getProjectNameOrKey() {
        return projectNameOrKey;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getResolveTransition() {
        return resolveTransition;
    }

    public String getReopenTransition() {
        return reopenTransition;
    }

    public List<JiraJobCustomFieldModel> getCustomFields() {
        return customFields;
    }

}
