import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { BootstrapTable, TableHeaderColumn } from 'react-bootstrap-table';
import ReadOnlyField from 'common/input/field/ReadOnlyField';
import { getAboutInfo } from 'store/actions/about';
import ConfigurationLabel from 'common/ConfigurationLabel';
import { NavLink } from 'react-router-dom';
import LabeledField from 'common/input/field/LabeledField';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { BLACKDUCK_INFO } from 'page/provider/blackduck/BlackDuckModel';
import { AZURE_INFO } from 'page/channel/azure/AzureModel';
import { EMAIL_INFO } from 'page/channel/email/EmailModels';
import { JIRA_SERVER_INFO } from 'page/channel/jira/server/JiraServerModel';
import { JIRA_CLOUD_INFO } from 'page/channel/jira/cloud/JiraCloudModel';
import { MSTEAMS_INFO } from 'page/channel/msteams/MSTeamsModel';
import { SLACK_INFO } from 'page/channel/slack/SlackModels';

const AboutInfo = ({
    getAbout, version, projectUrl, documentationUrl, globalDescriptorMap, distributionDescriptorMap
}) => {
    useEffect(() => {
        getAbout();
    }, []);

    const createDescriptorTable = (id, tableData, uriPrefix, tableName) => {
        const nameRenderer = (cell, row) => {
            const nameId = `aboutNameKey-${cell}`;
            const url = `${uriPrefix}${row.urlName}`;
            return <NavLink to={url} id={nameId}>{cell}</NavLink>;
        };
        const tableOptions = {
            defaultSortName: 'name',
            defaultSortOrder: 'asc',
            noDataText: 'No data found'
        };

        return (
            <div className="form-group">
                <div className="form-group">
                    <label className="col-sm-3 col-form-label text-right">{tableName}</label>
                    <div className="d-inline-flex p-2 col-sm-8">
                        <div className="form-control-static">
                            <div id={id} className="form-group">
                                <BootstrapTable
                                    version="4"
                                    data={tableData}
                                    options={tableOptions}
                                    headerContainerClass="scrollable"
                                    bodyContainerClass="scrollable"
                                >
                                    <TableHeaderColumn dataField="name" isKey dataFormat={nameRenderer}>
                                        Name
                                    </TableHeaderColumn>
                                </BootstrapTable>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    };

    const existingProviders = {
        [BLACKDUCK_INFO.key]: BLACKDUCK_INFO
    };
    const existingChannels = {
        [AZURE_INFO.key]: AZURE_INFO,
        [EMAIL_INFO.key]: EMAIL_INFO,
        [JIRA_CLOUD_INFO.key]: JIRA_CLOUD_INFO,
        [JIRA_SERVER_INFO.key]: JIRA_SERVER_INFO,
        [MSTEAMS_INFO.key]: MSTEAMS_INFO,
        [SLACK_INFO.key]: SLACK_INFO
    };

    const createTableData = (descriptorMapping, existingData) => Object.values(descriptorMapping)
        .filter((descriptor) => existingData[descriptor.name])
        .map((descriptor) => {
            const descriptorModel = existingData[descriptor.name];
            return {
                name: descriptorModel.label,
                urlName: descriptorModel.url
            };
        });

    const providerData = createTableData(globalDescriptorMap, existingProviders);
    const channelData = createTableData(distributionDescriptorMap, existingChannels);
    const providerTable = createDescriptorTable('about-providers', providerData, '/alert/providers/', 'Providers');
    const channelTable = createDescriptorTable('about-channels', channelData, '/alert/channels/', 'Distribution Channels');
    const providersMissing = !providerData || providerData.length <= 0;
    const channelsMissing = !channelData || channelData.length <= 0;

    return (
        <div>
            <ConfigurationLabel configurationName="About" />
            <div className="form-horizontal">
                <ReadOnlyField
                    id="about-description"
                    label="Description"
                    name="description"
                    readOnly="true"
                    value="This application provides the ability to send notifications from a provider to various distribution channels."
                />
                <ReadOnlyField id="about-version" label="Version" name="version" readOnly="true" value={version} />
                <ReadOnlyField
                    id="about-url"
                    label="Project URL"
                    name="projectUrl"
                    readOnly="true"
                    value={projectUrl}
                    url={projectUrl}
                />
                <ReadOnlyField
                    id="about-documentation-url"
                    label="API Documentation (Preview)"
                    name="documentationUrl"
                    readOnly="true"
                    value="Swagger UI"
                    url={documentationUrl}
                />
                <LabeledField
                    id="about-view-distribution"
                    label="View Distributions"
                    name="distribution"
                    readOnly="true"
                >
                    <div className="d-inline-flex p-2 col-sm-8">
                        <NavLink to="/alert/jobs/distribution">
                            All Distributions
                        </NavLink>
                    </div>
                </LabeledField>
                {providersMissing && channelsMissing
                && (
                    <div className="form-group">
                        <div className="form-group">
                            <label className="col-sm-3 col-form-label text-right" />
                            <div className="d-inline-flex p-2 col-sm-8 missingData">
                                <FontAwesomeIcon icon="exclamation-triangle" className="alert-icon" size="lg" />
                                The current user cannot view Distribution Channel or Provider data!
                            </div>
                        </div>
                    </div>
                )}
                {!providersMissing && providerTable}
                {!channelsMissing && channelTable}
            </div>
        </div>
    );
};

AboutInfo.propTypes = {
    getAbout: PropTypes.func.isRequired,
    version: PropTypes.string.isRequired,
    projectUrl: PropTypes.string.isRequired,
    documentationUrl: PropTypes.string.isRequired,
    globalDescriptorMap: PropTypes.object.isRequired,
    distributionDescriptorMap: PropTypes.object.isRequired
};

const mapStateToProps = (state) => ({
    version: state.about.version,
    projectUrl: state.about.projectUrl,
    documentationUrl: state.about.documentationUrl
});

const mapDispatchToProps = (dispatch) => ({
    getAbout: () => dispatch(getAboutInfo())
});

export default connect(mapStateToProps, mapDispatchToProps)(AboutInfo);
