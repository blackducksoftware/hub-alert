import React, { useEffect, useState } from 'react';
import * as PropTypes from 'prop-types';
import CommonGlobalConfiguration from 'common/global/CommonGlobalConfiguration';
import CheckboxInput from 'common/input/CheckboxInput';
import SelectInput from 'common/input/DynamicSelectInput';
import {
    DISTRIBUTION_COMMON_FIELD_KEYS,
    DISTRIBUTION_FREQUENCY_OPTIONS,
    DISTRIBUTION_NOTIFICATION_TYPE_OPTIONS,
    DISTRIBUTION_POLICY_SELECT_COLUMNS,
    DISTRIBUTION_PROJECT_SELECT_COLUMNS,
    DISTRIBUTION_TEST_FIELD_KEYS,
    DISTRIBUTION_URLS
} from 'page/distribution/DistributionModel';
import EndpointSelectField from 'common/input/EndpointSelectField';
import TextInput from 'common/input/TextInput';
import CollapsiblePane from 'common/CollapsiblePane';
import TableSelectInput from 'common/input/TableSelectInput';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import * as FieldModelUtilities from 'common/util/fieldModelUtilities';
import { CONTEXT_TYPE } from 'common/util/descriptorUtilities';
import CommonDistributionConfigurationForm from 'page/distribution/CommonDistributionConfigurationForm';
import * as DistributionRequestUtility from 'page/distribution/DistributionRequestUtility';
import * as HttpErrorUtilities from 'common/util/httpErrorUtilities';
import { AZURE_INFO } from 'page/channel/azure/AzureModel';
import { BLACKDUCK_INFO } from 'page/provider/blackduck/BlackDuckModel';
import { EMAIL_INFO } from 'page/channel/email/EmailModels';
import { JIRA_CLOUD_INFO } from 'page/channel/jira/cloud/JiraCloudModel';
import { JIRA_SERVER_INFO } from 'page/channel/jira/server/JiraServerModel';
import { MSTEAMS_INFO } from 'page/channel/msteams/MSTeamsModel';
import { SLACK_INFO } from 'page/channel/slack/SlackModels';
import AzureDistributionConfiguration from 'page/channel/azure/AzureDistributionConfiguration';
import EmailDistributionConfiguration from 'page/channel/email/EmailDistributionConfiguration';
import JiraCloudDistributionConfiguration from 'page/channel/jira/cloud/JiraCloudDistributionConfiguration';
import JiraServerDistributionConfiguration from 'page/channel/jira/server/JiraServerDistributionConfiguration';
import MsTeamsDistributionConfiguration from 'page/channel/msteams/MsTeamsDistributionConfiguration';
import SlackDistributionConfiguration from 'page/channel/slack/SlackDistributionConfiguration';

const DistributionConfigurationForm = ({
    csrfToken, errorHandler, descriptors, lastUpdated
}) => {
    const { id } = useParams();
    const history = useHistory();
    const location = useLocation();
    const [formData, setFormData] = useState({});
    const [errors, setErrors] = useState(HttpErrorUtilities.createEmptyErrorObject());
    const channelFieldKeys = {};
    channelFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.enabled] = {};
    channelFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.name] = {};
    channelFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.channelName] = {};
    channelFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.processingType] = {};
    channelFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.frequency] = {};
    channelFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.providerName] = {};
    const providerFieldKeys = {};
    providerFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId] = {};
    providerFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.providerName] = {};
    providerFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.processingType] = {};
    providerFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes] = {};
    providerFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.filterByProject] = {};
    providerFieldKeys[DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects] = {};
    const [channelModel, setChannelModel] = useState(FieldModelUtilities.createEmptyFieldModel(channelFieldKeys, CONTEXT_TYPE.DISTRIBUTION, AZURE_INFO.key));
    const [providerModel, setProviderModel] = useState(FieldModelUtilities.createEmptyFieldModel(providerFieldKeys, CONTEXT_TYPE.DISTRIBUTION, BLACKDUCK_INFO.key));
    const [channelSelectionModel, setChannelSelectionModel] = useState(FieldModelUtilities.createEmptyFieldModel(channelFieldKeys, CONTEXT_TYPE.DISTRIBUTION, AZURE_INFO.key));
    const [testFieldModel, setTestFieldModel] = useState({});
    const [channelFields, setChannelFields] = useState(null);
    const [providerHasChannelName, setProviderHasChannelName] = useState(false);
    const [hasProvider, setHasProvider] = useState(false);
    const [hasNotificationTypes, setHasNotificationTypes] = useState(false);
    const [filterByProject, setFilterByProject] = useState(false);
    const [readonly, setReadonly] = useState(false);
    const retrieveData = async () => {
        const data = await DistributionRequestUtility.getDataById(id, csrfToken, errorHandler, setErrors);
        return data;
    };

    const createDistributionData = (channelModelData, providerModelData) => {
        const providerConfigToSave = JSON.parse(JSON.stringify(providerModelData));
        let configuredProviderProjects = [];

        const fieldConfiguredProjects = providerModelData.keyToValues[DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects];
        if (fieldConfiguredProjects && fieldConfiguredProjects.values && fieldConfiguredProjects.values.length > 0) {
            configuredProviderProjects = fieldConfiguredProjects.values.map((selectedValue) => {
                let valueObject = selectedValue;
                if (typeof selectedValue === 'string') {
                    valueObject = JSON.parse(selectedValue);
                }

                return {
                    name: valueObject.name,
                    href: valueObject.href
                };
            });

            // Related fields need this to have a value in order to validate successfully
            providerConfigToSave.keyToValues[DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects].values = ['undefined'];
        }

        return {
            jobId: formData.jobId,
            fieldModels: [
                channelModelData,
                providerConfigToSave
            ],
            configuredProviderProjects
        };
    };

    const updateJobData = () => createDistributionData(channelModel, providerModel);

    const createTestData = () => {
        const channelFieldModel = FieldModelUtilities.combineFieldModels(channelModel, testFieldModel);
        return createDistributionData(channelFieldModel, providerModel);
    };

    const createAdditionalEmailRequestBody = () => {
        const providerName = FieldModelUtilities.getFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName);
        const providerConfigId = FieldModelUtilities.getFieldModelSingleValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId);
        const updatedModel = FieldModelUtilities.updateFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName, providerName);
        return FieldModelUtilities.updateFieldModelSingleValue(updatedModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId, providerConfigId);
    };

    const createProjectRequestBody = () => {
        const providerName = FieldModelUtilities.getFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName);
        const providerConfigId = FieldModelUtilities.getFieldModelSingleValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId);
        const updatedModel = FieldModelUtilities.updateFieldModelSingleValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName, providerName);
        return FieldModelUtilities.updateFieldModelSingleValue(updatedModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId, providerConfigId);
    };
    const createPolicyFilterRequestBody = () => {
        const providerName = FieldModelUtilities.getFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName);
        const providerConfigId = FieldModelUtilities.getFieldModelSingleValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId);
        const notificationTypes = FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes);
        const providerNameModel = FieldModelUtilities.updateFieldModelSingleValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName, providerName);
        const providerInfoModel = FieldModelUtilities.updateFieldModelSingleValue(providerNameModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId, providerConfigId);
        return FieldModelUtilities.updateFieldModelValues(providerInfoModel, DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes, notificationTypes);
    };

    const createProcessingTypeRequestBody = () => {
        const channelName = FieldModelUtilities.getFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName);
        return FieldModelUtilities.updateFieldModelSingleValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName, channelName);
    };

    const createVulnerabilityFilterRequestBody = () => {
        const notificationTypes = FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes);
        return FieldModelUtilities.updateFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes, notificationTypes);
    };

    useEffect(() => {
        const channelFieldModel = (formData && formData.fieldModels) ? formData.fieldModels.find((model) => FieldModelUtilities.hasKey(model, DISTRIBUTION_COMMON_FIELD_KEYS.channelName)) : {};
        const providerName = FieldModelUtilities.getFieldModelSingleValue(channelFieldModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName);
        const providerFieldModel = (formData && formData.fieldModels) ? formData.fieldModels.find((model) => providerName === model.descriptorName) : {};
        const channelKey = FieldModelUtilities.getFieldModelSingleValue(channelFieldModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName);
        const channelSelectionFieldModel = FieldModelUtilities.createEmptyFieldModel(channelFieldKeys, CONTEXT_TYPE.DISTRIBUTION, channelKey);
        setChannelModel(channelFieldModel);
        setProviderModel(providerFieldModel);
        setChannelSelectionModel(FieldModelUtilities.updateFieldModelSingleValue(channelSelectionFieldModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName, channelKey));
        if (descriptors[channelKey]) {
            setReadonly(descriptors[channelKey].readOnly);
        }
    }, [formData]);

    const onChannelSelectChange = (event) => {
        const { target } = event;
        const { name, value } = target;
        DistributionRequestUtility.checkDescriptorForGlobalConfig({
            csrfToken, descriptorName: value, errorHandler, fieldName: name, errors, setErrors
        });
        FieldModelUtilities.handleChange(channelSelectionModel, setChannelSelectionModel)(event);
        if (descriptors[value]) {
            setReadonly(descriptors[value].readOnly);
        }
    };

    useEffect(() => {
        const channelKey = FieldModelUtilities.getFieldModelSingleValue(channelSelectionModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName);
        const newFieldModel = FieldModelUtilities.createEmptyFieldModel(channelFieldKeys, CONTEXT_TYPE.DISTRIBUTION, channelKey);
        const newChannelModel = FieldModelUtilities.combineFieldModels(newFieldModel, channelModel);
        setChannelModel(FieldModelUtilities.updateFieldModelSingleValue(newChannelModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName, channelKey));
    }, [channelSelectionModel]);

    useEffect(() => {
        const channelKey = FieldModelUtilities.getFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName);
        const providerFound = FieldModelUtilities.hasValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName);
        setProviderHasChannelName(true);
        switch (channelKey) {
            case AZURE_INFO.key: {
                setChannelFields(<AzureDistributionConfiguration data={channelModel} setData={setChannelModel} errors={errors} readonly={readonly} />);
                break;
            }
            case EMAIL_INFO.key: {
                setChannelFields(<EmailDistributionConfiguration
                    csrfToken={csrfToken}
                    createAdditionalEmailRequestBody={createAdditionalEmailRequestBody}
                    data={channelModel}
                    setData={setChannelModel}
                    errors={errors}
                    readonly={readonly}
                />);
                break;
            }
            case JIRA_CLOUD_INFO.key: {
                setChannelFields(<JiraCloudDistributionConfiguration csrfToken={csrfToken} data={channelModel} setData={setChannelModel} errors={errors} readonly={readonly} />);
                break;
            }
            case JIRA_SERVER_INFO.key: {
                setChannelFields(<JiraServerDistributionConfiguration csrfToken={csrfToken} data={channelModel} setData={setChannelModel} errors={errors} readonly={readonly} />);
                break;
            }
            case MSTEAMS_INFO.key: {
                setChannelFields(<MsTeamsDistributionConfiguration data={channelModel} setData={setChannelModel} errors={errors} readonly={readonly} />);
                break;
            }
            case SLACK_INFO.key: {
                setChannelFields(<SlackDistributionConfiguration data={channelModel} setData={setChannelModel} errors={errors} readonly={readonly} />);
                break;
            }
            default: {
                setProviderHasChannelName(false);
                setChannelFields(null);
            }
        }
        setHasProvider(providerFound);
    }, [channelModel]);

    useEffect(() => {
        setFilterByProject(FieldModelUtilities.getFieldModelBooleanValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.filterByProject));
        setHasNotificationTypes(FieldModelUtilities.hasValue(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes));
    }, [providerModel]);

    if (location.pathname.includes('/copy') && FieldModelUtilities.getFieldModelId(formData)) {
        delete formData.id;
        setFormData(formData);
    }

    if (!FieldModelUtilities.hasKey(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.enabled)) {
        const defaultValueModel = FieldModelUtilities.updateFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.enabled, true);
        setChannelModel(defaultValueModel);
    }

    if (!FieldModelUtilities.hasKey(testFieldModel, DISTRIBUTION_TEST_FIELD_KEYS.topic) && !FieldModelUtilities.hasKey(testFieldModel, DISTRIBUTION_TEST_FIELD_KEYS.message)) {
        const topicFieldModel = FieldModelUtilities.updateFieldModelSingleValue(testFieldModel, DISTRIBUTION_TEST_FIELD_KEYS.topic, 'Alert Test Message');
        const messageFieldModel = FieldModelUtilities.updateFieldModelSingleValue(topicFieldModel, DISTRIBUTION_TEST_FIELD_KEYS.message, 'Test Message Content');
        setTestFieldModel(messageFieldModel);
    }

    const testFields = (
        <div>
            <TextInput
                id={DISTRIBUTION_TEST_FIELD_KEYS.topic}
                label="Topic"
                name={DISTRIBUTION_TEST_FIELD_KEYS.topic}
                required
                onChange={FieldModelUtilities.handleChange(testFieldModel, setTestFieldModel)}
                value={FieldModelUtilities.getFieldModelSingleValue(testFieldModel, DISTRIBUTION_TEST_FIELD_KEYS.topic)}
                errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_TEST_FIELD_KEYS.topic)}
                errorValue={errors.fieldErrors[DISTRIBUTION_TEST_FIELD_KEYS.topic]}
            />
            <TextInput
                id={DISTRIBUTION_TEST_FIELD_KEYS.message}
                label="Message"
                name={DISTRIBUTION_TEST_FIELD_KEYS.message}
                required
                onChange={FieldModelUtilities.handleChange(testFieldModel, setTestFieldModel)}
                value={FieldModelUtilities.getFieldModelSingleValue(testFieldModel, DISTRIBUTION_TEST_FIELD_KEYS.message)}
                errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_TEST_FIELD_KEYS.message)}
                errorValue={errors.fieldErrors[DISTRIBUTION_TEST_FIELD_KEYS.message]}
            />
        </div>
    );

    // TODO need to provide finer grain control with permissions.
    return (
        <CommonGlobalConfiguration
            label="Distribution Configuration"
            description="Configure the Distribution Job for Alert to send updates."
            lastUpdated={lastUpdated}
        >
            <CommonDistributionConfigurationForm
                setErrors={(error) => setErrors(error)}
                formData={formData}
                setFormData={setFormData}
                testFields={testFields}
                testFormData={testFieldModel}
                csrfToken={csrfToken}
                displaySave={!readonly}
                displayTest={!readonly}
                displayDelete={false}
                afterSuccessfulSave={() => history.push(DISTRIBUTION_URLS.distributionTableUrl)}
                retrieveData={retrieveData}
                createDataToSend={updateJobData}
                createDataToTest={createTestData}
                errorHandler={errorHandler}
            >
                <CheckboxInput
                    id={DISTRIBUTION_COMMON_FIELD_KEYS.enabled}
                    name={DISTRIBUTION_COMMON_FIELD_KEYS.enabled}
                    label="Enabled"
                    description="If selected, this job will be used for processing provider notifications, otherwise, this job will not be used."
                    readOnly={readonly}
                    onChange={FieldModelUtilities.handleChange(channelModel, setChannelModel)}
                    isChecked={FieldModelUtilities.getFieldModelBooleanValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.enabled)}
                    errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.enabled)}
                    errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.enabled]}
                />
                <EndpointSelectField
                    id={DISTRIBUTION_COMMON_FIELD_KEYS.channelName}
                    csrfToken={csrfToken}
                    endpoint={DISTRIBUTION_URLS.endpointSelectPath}
                    fieldKey={DISTRIBUTION_COMMON_FIELD_KEYS.channelName}
                    label="Channel Type"
                    description="Select the channel. Notifications generated through Alert will be sent through this channel."
                    clearable={false}
                    readOnly={readonly}
                    required
                    createRequestBody={() => channelSelectionModel}
                    onChange={onChannelSelectChange}
                    value={FieldModelUtilities.getFieldModelValues(channelSelectionModel, DISTRIBUTION_COMMON_FIELD_KEYS.channelName)}
                    errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.channelName)}
                    errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.channelName]}
                />
                <TextInput
                    id={DISTRIBUTION_COMMON_FIELD_KEYS.name}
                    name={DISTRIBUTION_COMMON_FIELD_KEYS.name}
                    label="Name"
                    description="The name of the distribution job. Must be unique"
                    readOnly={readonly}
                    required
                    onChange={FieldModelUtilities.handleChange(channelModel, setChannelModel)}
                    value={FieldModelUtilities.getFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.name)}
                    errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.name)}
                    errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.name]}
                />
                <SelectInput
                    id={DISTRIBUTION_COMMON_FIELD_KEYS.frequency}
                    label="Frequency"
                    description="Select how frequently this job should check for notifications to send."
                    options={DISTRIBUTION_FREQUENCY_OPTIONS}
                    readOnly={readonly}
                    required
                    onChange={FieldModelUtilities.handleChange(channelModel, setChannelModel)}
                    value={FieldModelUtilities.getFieldModelValues(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.frequency)}
                    errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.frequency)}
                    errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.frequency]}
                />
                <EndpointSelectField
                    id={DISTRIBUTION_COMMON_FIELD_KEYS.providerName}
                    csrfToken={csrfToken}
                    endpoint={DISTRIBUTION_URLS.endpointSelectPath}
                    fieldKey={DISTRIBUTION_COMMON_FIELD_KEYS.providerName}
                    label="Provider Type"
                    description="Select the provider. Only notifications for that provider will be processed in this distribution job."
                    clearable={false}
                    readOnly={readonly}
                    required
                    createRequestBody={() => channelModel}
                    onChange={FieldModelUtilities.handleChange(channelModel, setChannelModel)}
                    value={FieldModelUtilities.getFieldModelValues(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerName)}
                    errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.providerName)}
                    errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.providerName]}
                />
                <EndpointSelectField
                    id={DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigI}
                    csrfToken={csrfToken}
                    endpoint={DISTRIBUTION_URLS.endpointSelectPath}
                    fieldKey={DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId}
                    label="Provider Configuration"
                    description="The provider configuration to use with this distribution job."
                    clearable={false}
                    readOnly={readonly}
                    required
                    createRequestBody={() => providerModel}
                    onChange={FieldModelUtilities.handleChange(providerModel, setProviderModel)}
                    value={FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId)}
                    errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId)}
                    errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.providerConfigId]}
                />
                {channelFields}
                {hasProvider && providerHasChannelName && (
                    <div>
                        <SelectInput
                            id={DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes}
                            label="Notification Types"
                            description="Select one or more of the notification types. Only these notification types will be included for this distribution job."
                            options={DISTRIBUTION_NOTIFICATION_TYPE_OPTIONS}
                            multiSelect
                            readOnly={readonly}
                            removeSelected
                            required
                            onChange={FieldModelUtilities.handleChange(providerModel, setProviderModel)}
                            value={FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes)}
                            errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes)}
                            errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.notificationTypes]}
                        />
                        <EndpointSelectField
                            id={DISTRIBUTION_COMMON_FIELD_KEYS.processingType}
                            csrfToken={csrfToken}
                            endpoint={DISTRIBUTION_URLS.endpointSelectPath}
                            fieldKey={DISTRIBUTION_COMMON_FIELD_KEYS.processingType}
                            label="Processing"
                            description="Select the way messages will be processed: <TODO create the dynamic description>"
                            readOnly={readonly}
                            required
                            createRequestBody={createProcessingTypeRequestBody}
                            onChange={FieldModelUtilities.handleChange(providerModel, setProviderModel)}
                            value={FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.processingType)}
                            errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.processingType)}
                            errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.processingType]}
                        />
                        <CheckboxInput
                            id={DISTRIBUTION_COMMON_FIELD_KEYS.filterByProject}
                            name={DISTRIBUTION_COMMON_FIELD_KEYS.filterByProject}
                            label="Filter By Project"
                            description="If selected, only notifications from the selected Projects table will be processed. Otherwise notifications from all Projects are processed."
                            readOnly={readonly}
                            onChange={FieldModelUtilities.handleChange(providerModel, setProviderModel)}
                            isChecked={filterByProject}
                            errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.filterByProject)}
                            errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.filterByProject]}
                        />
                    </div>
                )}
                {filterByProject && (
                    <div>
                        <TextInput
                            id={DISTRIBUTION_COMMON_FIELD_KEYS.projectNamePattern}
                            key={DISTRIBUTION_COMMON_FIELD_KEYS.projectNamePattern}
                            name={DISTRIBUTION_COMMON_FIELD_KEYS.projectNamePattern}
                            label="Project Name Pattern"
                            description="The regular expression to use to determine what Projects to include. These are in addition to the Projects selected in the table."
                            readOnly={readonly}
                            required
                            onChange={FieldModelUtilities.handleChange(channelModel, setChannelModel)}
                            value={FieldModelUtilities.getFieldModelSingleValue(channelModel, DISTRIBUTION_COMMON_FIELD_KEYS.projectNamePattern)}
                            errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.projectNamePattern)}
                            errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.projectNamePattern]}
                        />
                        <TableSelectInput
                            id={DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects}
                            csrfToken={csrfToken}
                            endpoint={DISTRIBUTION_URLS.endpointSelectPath}
                            fieldKey={DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects}
                            columns={DISTRIBUTION_PROJECT_SELECT_COLUMNS}
                            label="Projects"
                            description="Select a project or projects that will be used to retrieve notifications from your provider."
                            readOnly={readonly}
                            paged
                            searchable
                            useRowAsValue
                            createRequestBody={createProjectRequestBody}
                            onChange={FieldModelUtilities.handleChange(providerModel, setProviderModel)}
                            value={FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects)}
                            errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects)}
                            errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.configuredProjects]}
                        />
                    </div>
                )}
                {hasNotificationTypes && (
                    <CollapsiblePane
                        id="distribution-notification-filtering"
                        title="Black Duck Notification Filtering"
                        expanded={false}
                    >
                        <TableSelectInput
                            id={DISTRIBUTION_COMMON_FIELD_KEYS.policyFilter}
                            csrfToken={csrfToken}
                            endpoint={DISTRIBUTION_URLS.endpointSelectPath}
                            fieldKey={DISTRIBUTION_COMMON_FIELD_KEYS.policyFilter}
                            columns={DISTRIBUTION_POLICY_SELECT_COLUMNS}
                            label="Policy Notification Type Filter"
                            description="List of Policies you can choose from to further filter which notifications you want sent via this job (You must have a policy notification selected for this filter to apply)."
                            readOnly={readonly}
                            paged
                            searchable
                            createRequestBody={createPolicyFilterRequestBody}
                            onChange={FieldModelUtilities.handleChange(providerModel, setProviderModel)}
                            value={FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.policyFilter)}
                            errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.policyFilter)}
                            errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.policyFilter]}
                        />
                        <EndpointSelectField
                            id={DISTRIBUTION_COMMON_FIELD_KEYS.vulnerabilitySeverityFilter}
                            csrfToken={csrfToken}
                            endpoint={DISTRIBUTION_URLS.endpointSelectPath}
                            fieldKey={DISTRIBUTION_COMMON_FIELD_KEYS.vulnerabilitySeverityFilter}
                            label="Vulnerability Notification Type Filter"
                            description="List of Vulnerability severities you can choose from to further filter which notifications you want sent via this job (You must have a vulnerability notification selected for this filter to apply)."
                            multiSelect
                            readOnly={readonly}
                            createRequestBody={createVulnerabilityFilterRequestBody}
                            onChange={FieldModelUtilities.handleChange(providerModel, setProviderModel)}
                            value={FieldModelUtilities.getFieldModelValues(providerModel, DISTRIBUTION_COMMON_FIELD_KEYS.vulnerabilitySeverityFilter)}
                            errorName={FieldModelUtilities.createFieldModelErrorKey(DISTRIBUTION_COMMON_FIELD_KEYS.vulnerabilitySeverityFilter)}
                            errorValue={errors.fieldErrors[DISTRIBUTION_COMMON_FIELD_KEYS.vulnerabilitySeverityFilter]}
                        />
                    </CollapsiblePane>
                )}
            </CommonDistributionConfigurationForm>
        </CommonGlobalConfiguration>
    );
};

DistributionConfigurationForm.propTypes = {
    csrfToken: PropTypes.string.isRequired,
    errorHandler: PropTypes.object.isRequired,
    descriptors: PropTypes.array.isRequired,
    lastUpdated: PropTypes.string
};

DistributionConfigurationForm.defaultProps = {
    lastUpdated: null
};

export default DistributionConfigurationForm;
