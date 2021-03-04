/*
 * channel
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.channel.jira2.server.delegate;

import java.util.List;

import com.synopsys.integration.alert.channel.api.issue.model.IssueCreationModel;
import com.synopsys.integration.alert.channel.api.issue.send.AlertIssueOriginCreator;
import com.synopsys.integration.alert.channel.jira.server.descriptor.JiraServerDescriptor;
import com.synopsys.integration.alert.channel.jira2.cloud.JiraIssueAlertPropertiesManager;
import com.synopsys.integration.alert.channel.jira2.common.JiraErrorMessageUtility;
import com.synopsys.integration.alert.channel.jira2.common.JiraIssueCreationRequestCreator;
import com.synopsys.integration.alert.channel.jira2.common.delegate.JiraIssueCreator;
import com.synopsys.integration.alert.channel.jira2.common.model.JiraCustomFieldReplacementValues;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.persistence.model.job.details.JiraServerJobDetailsModel;
import com.synopsys.integration.alert.descriptor.api.JiraServerChannelKey;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.jira.common.model.components.ProjectComponent;
import com.synopsys.integration.jira.common.model.request.builder.IssueRequestModelFieldsMapBuilder;
import com.synopsys.integration.jira.common.model.response.IssueCreationResponseModel;
import com.synopsys.integration.jira.common.model.response.IssueResponseModel;
import com.synopsys.integration.jira.common.server.model.IssueCreationRequestModel;
import com.synopsys.integration.jira.common.server.service.IssueService;
import com.synopsys.integration.jira.common.server.service.ProjectService;

public class JiraServerIssueCreator extends JiraIssueCreator<IssueCreationRequestModel> {
    private final JiraServerJobDetailsModel distributionDetails;
    private final IssueService issueService;
    private final ProjectService projectService;
    private final JiraIssueCreationRequestCreator jiraIssueCreationRequestCreator;

    public JiraServerIssueCreator(
        JiraServerChannelKey jiraServerChannelKey,
        JiraServerJobDetailsModel distributionDetails,
        IssueService issueService,
        ProjectService projectService,
        JiraIssueCreationRequestCreator jiraIssueCreationRequestCreator,
        JiraServerIssueCommenter jiraServerIssueCommenter,
        JiraIssueAlertPropertiesManager issuePropertiesManager,
        JiraErrorMessageUtility jiraErrorMessageUtility,
        AlertIssueOriginCreator alertIssueOriginCreator
    ) {
        super(
            jiraServerChannelKey,
            jiraServerIssueCommenter,
            alertIssueOriginCreator,
            jiraErrorMessageUtility,
            issuePropertiesManager,
            JiraServerDescriptor.KEY_ISSUE_CREATOR
        );
        this.distributionDetails = distributionDetails;
        this.issueService = issueService;
        this.projectService = projectService;
        this.jiraIssueCreationRequestCreator = jiraIssueCreationRequestCreator;
    }

    @Override
    public IssueCreationRequestModel createIssueCreationRequest(IssueCreationModel alertIssueCreationModel) throws AlertException {
        ProjectComponent jiraProject = retrieveProjectComponent();
        JiraCustomFieldReplacementValues replacementValues = alertIssueCreationModel.getSource()
                                                                 .map(this::createCustomFieldReplacementValues)
                                                                 .orElse(JiraCustomFieldReplacementValues.trivial(alertIssueCreationModel.getProvider()));
        IssueRequestModelFieldsMapBuilder fieldsBuilder = jiraIssueCreationRequestCreator.createIssueRequestModel(
            alertIssueCreationModel.getTitle(),
            alertIssueCreationModel.getDescription(),
            jiraProject.getId(),
            distributionDetails.getIssueType(),
            replacementValues,
            distributionDetails.getCustomFields()
        );
        return new IssueCreationRequestModel(
            distributionDetails.getIssueCreatorUsername(),
            distributionDetails.getIssueType(),
            distributionDetails.getProjectNameOrKey(),
            fieldsBuilder
        );
    }

    @Override
    protected IssueCreationResponseModel createIssue(IssueCreationRequestModel alertIssueCreationModel) throws IntegrationException {
        return issueService.createIssue(alertIssueCreationModel);
    }

    @Override
    protected IssueResponseModel fetchIssue(String issueIdOrKey) throws IntegrationException {
        return issueService.getIssue(issueIdOrKey);
    }

    @Override
    protected String getReporter(IssueCreationRequestModel creationRequest) {
        return creationRequest.getReporterUsername();
    }

    private ProjectComponent retrieveProjectComponent() throws AlertException {
        String jiraProjectName = distributionDetails.getProjectNameOrKey();
        List<ProjectComponent> foundProjectComponents;
        try {
            foundProjectComponents = projectService.getProjectsByName(jiraProjectName);
        } catch (IntegrationException e) {
            throw new AlertException("Failed to retrieve projects from Jira", e);
        }

        return foundProjectComponents
                   .stream()
                   .findAny()
                   .orElseThrow(() -> new AlertException(String.format("Unable to find project matching '%s'", jiraProjectName)));
    }

}
