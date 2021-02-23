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
package com.synopsys.integration.alert.channel.api.issue.model;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.alert.common.message.model.LinkableItem;
import com.synopsys.integration.alert.processor.api.extract.model.ProviderDetails;
import com.synopsys.integration.alert.processor.api.extract.model.ProviderMessage;
import com.synopsys.integration.alert.processor.api.extract.model.project.BomComponentDetails;

public class ProjectIssueModel extends ProviderMessage<ProjectIssueModel> {
    private final LinkableItem project;
    private final LinkableItem projectVersion;
    private final BomComponentDetails bomComponent;

    public ProjectIssueModel(ProviderDetails providerDetails, LinkableItem project, LinkableItem projectVersion, BomComponentDetails bomComponent) {
        super(providerDetails);
        this.project = project;
        this.projectVersion = projectVersion;
        this.bomComponent = bomComponent;
    }

    public LinkableItem getProject() {
        return project;
    }

    public Optional<LinkableItem> getProjectVersion() {
        return Optional.ofNullable(projectVersion);
    }

    public BomComponentDetails getBomComponent() {
        return bomComponent;
    }

    @Override
    public List<ProjectIssueModel> combine(ProjectIssueModel otherModel) {
        List<ProjectIssueModel> uncombinedModels = List.of(this, otherModel);

        if (!project.equals(otherModel.project)) {
            return uncombinedModels;
        }

        if (projectVersion != null && !projectVersion.equals(otherModel.projectVersion)) {
            return uncombinedModels;
        }

        if (!bomComponent.equals(otherModel.bomComponent)) {
            return uncombinedModels;
        }

        return List.of(this);
    }

}