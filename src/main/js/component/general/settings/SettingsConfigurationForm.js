import PasswordInput from 'field/input/PasswordInput';
import CheckboxInput from 'field/input/CheckboxInput';
import TextInput from 'field/input/TextInput';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import CollapsiblePane from 'component/common/CollapsiblePane';
import ConfigButtons from 'component/common/ConfigButtons';
import * as FieldModelUtil from 'util/fieldModelUtilities';


const KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD = 'user.default.admin.password';
const KEY_ENCRYPTION_PASSWORD = 'encryption.password';
const KEY_ENCRYPTION_GLOBAL_SALT = 'encryption.global.salt';

// Proxy Keys
const KEY_PROXY_HOST = 'proxy.host';
const KEY_PROXY_PORT = 'proxy.port';
const KEY_PROXY_USERNAME = 'proxy.username';
const KEY_PROXY_PASSWORD = 'proxy.password';

// LDAP Keys
const KEY_LDAP_ENABLED = 'ldap.enabled';
const KEY_LDAP_SERVER = 'ldap.server';
const KEY_LDAP_MANAGER_DN = 'ldap.manager.dn';
const KEY_LDAP_MANAGER_PASSWORD = 'ldap.manager.password';
const KEY_LDAP_AUTHENTICATION_TYPE = 'ldap.authentication.type';
const KEY_LDAP_REFERRAL = 'ldap.referral';
const KEY_LDAP_USER_SEARCH_BASE = 'ldap.user.search.base';
const KEY_LDAP_USER_SEARCH_FILTER = 'ldap.user.search.filter';
const KEY_LDAP_USER_DN_PATTERNS = 'ldap.user.dn.patterns';
const KEY_LDAP_USER_ATTRIBUTES = 'ldap.user.attributes';
const KEY_LDAP_GROUP_SEARCH_BASE = 'ldap.group.search.base';
const KEY_LDAP_GROUP_SEARCH_FILTER = 'ldap.group.search.filter';
const KEY_LDAP_GROUP_ROLE_ATTRIBUTE = 'ldap.group.role.attribute';
const KEY_LDAP_ROLE_PREFIX = 'ldap.role.prefix';

const fieldNames = [
    KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD,
    KEY_ENCRYPTION_PASSWORD,
    KEY_ENCRYPTION_GLOBAL_SALT,
    KEY_PROXY_HOST,
    KEY_PROXY_PORT,
    KEY_PROXY_USERNAME,
    KEY_PROXY_PASSWORD,
    KEY_LDAP_ENABLED,
    KEY_LDAP_SERVER,
    KEY_LDAP_MANAGER_DN,
    KEY_LDAP_MANAGER_PASSWORD,
    KEY_LDAP_AUTHENTICATION_TYPE,
    KEY_LDAP_REFERRAL,
    KEY_LDAP_USER_SEARCH_BASE,
    KEY_LDAP_USER_SEARCH_FILTER,
    KEY_LDAP_USER_DN_PATTERNS,
    KEY_LDAP_USER_ATTRIBUTES,
    KEY_LDAP_GROUP_SEARCH_BASE,
    KEY_LDAP_GROUP_SEARCH_FILTER,
    KEY_LDAP_GROUP_ROLE_ATTRIBUTE,
    KEY_LDAP_ROLE_PREFIX
];

class SettingsConfigurationForm extends Component {
    constructor(props) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.state = {
            settingsData: FieldModelUtil.createEmptyFieldModel(fieldNames)
        };
    }

    componentWillMount() {
        this.props.getSettings();
    }

    componentWillReceiveProps(nextProps) {
        if ((nextProps.fetchingSetupStatus === 'SYSTEM_SETUP_FETCHED' && nextProps.updateStatus === 'FETCHED') ||
            (this.props.fetchingSetupStatus === 'SYSTEM_SETUP_FETCHED' && this.props.updateStatus === 'FETCHED')) {
            const newState = FieldModelUtil.checkModelOrCreateEmpty(nextProps.settingsData, fieldNames);
            this.setState({
                settingsData: newState
            });
        }
    }

    handleChange({ target }) {
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const newState = FieldModelUtil.updateFieldModelSingleValue(this.state.settingsData, target.name, value);
        this.setState({
            settingsData: newState
        });
    }

    handleSubmit(evt) {
        evt.preventDefault();
        this.props.saveSettings(this.state.settingsData);
    }

    render() {
        const fieldModel = this.state.settingsData;
        return (
            <form method="POST" className="form-horizontal loginForm" onSubmit={this.handleSubmit}>
                <div className="form-group">
                    <div className="col-sm-12">
                        <h2>Default System Administrator Configuration</h2>
                        <PasswordInput
                            id={KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD}
                            label="Password"
                            name={KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD}
                            value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD)}
                            isSet={FieldModelUtil.isFieldModelValueSet(fieldModel, KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD)}
                            onChange={this.handleChange}
                            errorName={FieldModelUtil.createFieldModelErrorKey(KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD)}
                            errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_DEFAULT_SYSTEM_ADMIN_PASSWORD)]}
                        />
                    </div>
                </div>
                <div className="form-group">
                    <div className="col-sm-12">
                        <h2>Encryption Configuration</h2>
                        <PasswordInput
                            id={KEY_ENCRYPTION_PASSWORD}
                            label="Password"
                            name={KEY_ENCRYPTION_PASSWORD}
                            value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_ENCRYPTION_PASSWORD)}
                            isSet={FieldModelUtil.isFieldModelValueSet(fieldModel, KEY_ENCRYPTION_PASSWORD)}
                            onChange={this.handleChange}
                            errorName={FieldModelUtil.createFieldModelErrorKey(KEY_ENCRYPTION_PASSWORD)}
                            errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_ENCRYPTION_PASSWORD)]}
                        />
                        <PasswordInput
                            id={KEY_ENCRYPTION_GLOBAL_SALT}
                            label="Salt"
                            name={KEY_ENCRYPTION_GLOBAL_SALT}
                            value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_ENCRYPTION_GLOBAL_SALT)}
                            isSet={FieldModelUtil.isFieldModelValueSet(fieldModel, KEY_ENCRYPTION_GLOBAL_SALT)}
                            onChange={this.handleChange}
                            errorName={FieldModelUtil.createFieldModelErrorKey(KEY_ENCRYPTION_GLOBAL_SALT)}
                            errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_ENCRYPTION_GLOBAL_SALT)]}
                        />
                    </div>
                </div>
                <div className="form-group">
                    <div className="col-sm-12">
                        <CollapsiblePane title="Proxy Configuration">
                            <TextInput
                                id={KEY_PROXY_HOST}
                                label="Host Name"
                                name={KEY_PROXY_HOST}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_PROXY_HOST)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_HOST)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_HOST)]}
                            />
                            <TextInput
                                id={KEY_PROXY_PORT}
                                label="Port"
                                name={KEY_PROXY_PORT}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_PROXY_PORT)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_PORT)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_PORT)]}
                            />
                            <TextInput
                                id={KEY_PROXY_USERNAME}
                                label="Username"
                                name={KEY_PROXY_USERNAME}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_PROXY_USERNAME)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_USERNAME)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_USERNAME)]}
                            />
                            <PasswordInput
                                id={KEY_PROXY_PASSWORD}
                                label="Password"
                                name={KEY_PROXY_PASSWORD}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_PROXY_PASSWORD)}
                                isSet={FieldModelUtil.isFieldModelValueSet(fieldModel, KEY_PROXY_PASSWORD)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_PASSWORD)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_PROXY_PASSWORD)]}
                            />
                        </CollapsiblePane>
                    </div>
                </div>
                <div className="form-group">
                    <div className="col-sm-12">
                        <CollapsiblePane title="LDAP Configuration">
                            <CheckboxInput
                                id={KEY_LDAP_ENABLED}
                                label="Enabled"
                                name={KEY_LDAP_ENABLED}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_ENABLED)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_ENABLED)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_ENABLED)]}
                            />
                            <TextInput
                                id={KEY_LDAP_SERVER}
                                label="Server"
                                name={KEY_LDAP_SERVER}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_SERVER)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_SERVER)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_SERVER)]}
                            />
                            <TextInput
                                id={KEY_LDAP_MANAGER_DN}
                                label="Manager DN"
                                name={KEY_LDAP_MANAGER_DN}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_MANAGER_DN)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_MANAGER_DN)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_MANAGER_DN)]}
                            />
                            <PasswordInput
                                id={KEY_LDAP_MANAGER_PASSWORD}
                                label="Manager Password"
                                name={KEY_LDAP_MANAGER_PASSWORD}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_MANAGER_PASSWORD)}
                                isSet={FieldModelUtil.isFieldModelValueSet(fieldModel, KEY_LDAP_MANAGER_PASSWORD)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_MANAGER_PASSWORD)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_MANAGER_PASSWORD)]}
                            />
                            <TextInput
                                id={KEY_LDAP_AUTHENTICATION_TYPE}
                                label="Authentication Type"
                                name={KEY_LDAP_AUTHENTICATION_TYPE}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_AUTHENTICATION_TYPE)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_AUTHENTICATION_TYPE)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_AUTHENTICATION_TYPE)]}
                            />
                            <TextInput
                                id={KEY_LDAP_REFERRAL}
                                label="Referral"
                                name={KEY_LDAP_REFERRAL}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_REFERRAL)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_REFERRAL)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_REFERRAL)]}
                            />
                            <TextInput
                                id={KEY_LDAP_USER_SEARCH_BASE}
                                label="User Search Base"
                                name={KEY_LDAP_USER_SEARCH_BASE}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_USER_SEARCH_BASE)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_SEARCH_BASE)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_SEARCH_BASE)]}
                            />
                            <TextInput
                                id={KEY_LDAP_USER_SEARCH_FILTER}
                                label="User Search Filter"
                                name={KEY_LDAP_USER_SEARCH_FILTER}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_USER_SEARCH_FILTER)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_SEARCH_FILTER)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_SEARCH_FILTER)]}
                            />
                            <TextInput
                                id={KEY_LDAP_USER_DN_PATTERNS}
                                label="User DN Patterns"
                                name={KEY_LDAP_USER_DN_PATTERNS}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_USER_DN_PATTERNS)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_DN_PATTERNS)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_DN_PATTERNS)]}
                            />
                            <TextInput
                                id={KEY_LDAP_USER_ATTRIBUTES}
                                label="User Attributes"
                                name={KEY_LDAP_USER_ATTRIBUTES}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_USER_ATTRIBUTES)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_ATTRIBUTES)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_USER_ATTRIBUTES)]}
                            />
                            <TextInput
                                id={KEY_LDAP_GROUP_SEARCH_BASE}
                                label="Group Search Base"
                                name={KEY_LDAP_GROUP_SEARCH_BASE}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_GROUP_SEARCH_BASE)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_GROUP_SEARCH_BASE)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_GROUP_SEARCH_BASE)]}
                            />
                            <TextInput
                                id={KEY_LDAP_GROUP_SEARCH_FILTER}
                                label="Group Search Filter"
                                name={KEY_LDAP_GROUP_SEARCH_FILTER}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_GROUP_SEARCH_FILTER)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_GROUP_SEARCH_FILTER)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_GROUP_SEARCH_FILTER)]}
                            />
                            <TextInput
                                id={KEY_LDAP_GROUP_ROLE_ATTRIBUTE}
                                label="Group Role Attribute"
                                name={KEY_LDAP_GROUP_ROLE_ATTRIBUTE}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_GROUP_ROLE_ATTRIBUTE)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_GROUP_ROLE_ATTRIBUTE)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_GROUP_ROLE_ATTRIBUTE)]}
                            />
                            <TextInput
                                id={KEY_LDAP_ROLE_PREFIX}
                                label="Role Prefix"
                                name={KEY_LDAP_ROLE_PREFIX}
                                value={FieldModelUtil.getFieldModelSingleValue(fieldModel, KEY_LDAP_ROLE_PREFIX)}
                                onChange={this.handleChange}
                                errorName={FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_ROLE_PREFIX)}
                                errorValue={this.props.fieldErrors[FieldModelUtil.createFieldModelErrorKey(KEY_LDAP_ROLE_PREFIX)]}
                            />
                        </CollapsiblePane>
                    </div>
                </div>
                <ConfigButtons isFixed={false} includeSave type="submit" />
            </form>
        );
    }
}

SettingsConfigurationForm.propTypes = {
    fetchingSetupStatus: PropTypes.string.isRequired,
    getSettings: PropTypes.func.isRequired,
    saveSettings: PropTypes.func.isRequired,
    updateStatus: PropTypes.string,
    settingsData: PropTypes.object,
    fieldErrors: PropTypes.object
};

SettingsConfigurationForm.defaultProps = {
    fieldErrors: {},
    settingsData: {},
    updateStatus: ''
};

export default SettingsConfigurationForm;

