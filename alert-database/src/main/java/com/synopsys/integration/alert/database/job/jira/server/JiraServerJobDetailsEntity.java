/**
 * alert-database
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
package com.synopsys.integration.alert.database.job.jira.server;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.synopsys.integration.alert.database.job.jira.server.custom_field.JiraServerJobCustomFieldEntity;

@Entity
@Table(schema = "alert", name = "jira_server_job_details")
public class JiraServerJobDetailsEntity {
    @Id
    @Column(name = "job_id")
    private UUID jobId;

    @Column(name = "add_comments")
    private Boolean addComments;

    @Column(name = "issue_creator_username")
    private String issueCreatorUsername;

    @Column(name = "project_name_or_key")
    private String projectNameOrKey;

    @Column(name = "issue_type")
    private String issueType;

    @Column(name = "resolve_transition")
    private String resolveTransition;

    @Column(name = "reopen_transition")
    private String reopenTransition;

    @OneToMany
    @JoinColumn(name = "job_id", referencedColumnName = "job_id", insertable = false, updatable = false)
    private List<JiraServerJobCustomFieldEntity> jobCustomFields;

    public JiraServerJobDetailsEntity() {
    }

    public JiraServerJobDetailsEntity(UUID jobId, Boolean addComments, String issueCreatorUsername, String projectNameOrKey, String issueType, String resolveTransition, String reopenTransition) {
        this.jobId = jobId;
        this.addComments = addComments;
        this.issueCreatorUsername = issueCreatorUsername;
        this.projectNameOrKey = projectNameOrKey;
        this.issueType = issueType;
        this.resolveTransition = resolveTransition;
        this.reopenTransition = reopenTransition;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public Boolean getAddComments() {
        return addComments;
    }

    public void setAddComments(Boolean addComments) {
        this.addComments = addComments;
    }

    public String getIssueCreatorUsername() {
        return issueCreatorUsername;
    }

    public void setIssueCreatorUsername(String issueCreatorUsername) {
        this.issueCreatorUsername = issueCreatorUsername;
    }

    public String getProjectNameOrKey() {
        return projectNameOrKey;
    }

    public void setProjectNameOrKey(String projectNameOrKey) {
        this.projectNameOrKey = projectNameOrKey;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getResolveTransition() {
        return resolveTransition;
    }

    public void setResolveTransition(String resolveTransition) {
        this.resolveTransition = resolveTransition;
    }

    public String getReopenTransition() {
        return reopenTransition;
    }

    public void setReopenTransition(String reopenTransition) {
        this.reopenTransition = reopenTransition;
    }

    public List<JiraServerJobCustomFieldEntity> getJobCustomFields() {
        return jobCustomFields;
    }

    public void setJobCustomFields(List<JiraServerJobCustomFieldEntity> jobCustomFields) {
        this.jobCustomFields = jobCustomFields;
    }

}
