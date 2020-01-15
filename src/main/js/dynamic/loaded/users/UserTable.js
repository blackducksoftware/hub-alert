import React, { Component } from 'react';
import PropTypes from 'prop-types';
import TableDisplay from 'field/TableDisplay';
import TextInput from 'field/input/TextInput';
import PasswordInput from 'field/input/PasswordInput';
import { connect } from 'react-redux';
import { clearUserFieldErrors, createNewUser, deleteUser, fetchUsers, updateUser } from 'store/actions/users';
import DynamicSelectInput from 'field/input/DynamicSelect';
import { fetchRoles } from 'store/actions/roles';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

class UserTable extends Component {
    constructor(props) {
        super(props);

        this.retrieveData = this.retrieveData.bind(this);
        this.createColumns = this.createColumns.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.onSave = this.onSave.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.checkIfPasswordsMatch = this.checkIfPasswordsMatch.bind(this);
        this.onConfigClose = this.onConfigClose.bind(this);
        this.createModalFields = this.createModalFields.bind(this);
        this.retrieveRoles = this.retrieveRoles.bind(this);
        this.clearModalFieldState = this.clearModalFieldState.bind(this);
        this.onEdit = this.onEdit.bind(this);

        this.state = {
            user: {}
        };
    }

    createColumns() {
        return [
            {
                header: 'id',
                headerLabel: 'Id',
                isKey: true,
                hidden: true
            },
            {
                header: 'external',
                headerLabel: 'External',
                isKey: false,
                hidden: true
            },
            {
                header: 'username',
                headerLabel: 'Username',
                isKey: false,
                hidden: false
            },
            {
                header: 'emailAddress',
                headerLabel: 'Email',
                isKey: false,
                hidden: false
            }
        ];
    }

    retrieveData() {
        this.props.getUsers();
    }

    handleChange(e) {
        const { name, value, type, checked } = e.target;
        const { user } = this.state;

        const updatedValue = type === 'checkbox' ? checked.toString()
            .toLowerCase() === 'true' : value;
        const newUser = Object.assign(user, { [name]: updatedValue });
        this.setState({
            user: newUser
        });
    }

    onSave() {
        const { user } = this.state;
        if (!this.checkIfPasswordsMatch(user)) {
            this.props.createUser(user);
            return true;
        }
        return false;
    }

    onUpdate() {
        const { user } = this.state;
        if (!this.checkIfPasswordsMatch(user)) {
            debugger;
            this.props.updateUser(user);
            return true;
        }
        debugger;
        return false;
    }

    checkIfPasswordsMatch(user) {
        const passwordKey = 'password';
        const confirmPasswordKey = 'confirmPassword';
        const confirmPasswordError = 'confirmPasswordError';

        let passwordError = '';
        let error = false;
        if ((user[passwordKey] || user[confirmPasswordKey]) && (user[passwordKey] !== user[confirmPasswordKey])) {
            passwordError = 'Passwords do not match.';
            error = true;
        }
        debugger;
        const newUser = Object.assign(user, { [confirmPasswordError]: passwordError });
        this.setState({
            user: newUser
        });
        return error;
    }

    onDelete(usersToDelete) {
        if (usersToDelete) {
            usersToDelete.forEach(userId => {
                this.props.deleteUser(userId);
            });
        }
        this.retrieveData();
    }

    onConfigClose() {
        this.props.clearFieldErrors();
    }

    clearModalFieldState() {
        if (this.state.user && Object.keys(this.state.user).length > 0) {
            this.setState({
                user: {}
            });
        }
    }

    retrieveRoles() {
        return this.props.roles.map(role => {
            const rolename = role.roleName;
            return {
                label: rolename,
                value: rolename
            };
        });
    }

    onEdit(selectedRow) {
        this.setState({
            user: selectedRow
        });
    }

    createModalFields() {
        const { user } = this.state;
        const { fieldErrors } = this.props;

        const usernameKey = 'username';
        const passwordKey = 'password';
        const confirmPasswordKey = 'confirmPassword';
        const confirmPasswordError = 'confirmPasswordError';
        const emailKey = 'emailAddress';
        const roleNames = 'roleNames';
        const passwordSetKey = 'passwordSet';
        const externalKey = 'external';
        const external = user[externalKey];
        const externalNote = (
            <div className="form-group">
                <label className="col-sm-3 col-form-label text-right warningStatus" />
                <div className="d-inline-flex">
                    <span className="warningStatus">
                        <FontAwesomeIcon icon="exclamation-triangle" className="alert-icon" size="lg" />
                    </span>
                </div>
                <div className="d-inline-flex p-2 col-sm-8 warningStatus">
                    This user is managed by a system external to Alert. Only roles can be assigned.
                </div>
            </div>
        );
        let passwordConfirmField = null;
        if (!external) {
            passwordConfirmField = (<PasswordInput
                name={confirmPasswordKey} label="Confirm Password" description="The users password." readOnly={false}
                required onChange={this.handleChange} value={user[confirmPasswordKey]}
                errorName={confirmPasswordKey} errorValue={user[confirmPasswordError]}
            />);
        }

        return (
            <div>
                {external && externalNote}
                <TextInput
                    name={usernameKey} label="Username" description="The users username." readOnly={external}
                    required={!external} onChange={this.handleChange} value={user[usernameKey]}
                    errorName={usernameKey}
                    errorValue={fieldErrors[usernameKey]} />
                <PasswordInput
                    name={passwordKey} label="Password" description="The users password." readOnly={external}
                    required={!external} onChange={this.handleChange} value={user[passwordKey]}
                    isSet={user[passwordSetKey]}
                    errorName={passwordKey}
                    errorValue={fieldErrors[passwordKey]} />
                {passwordConfirmField}
                <TextInput
                    name={emailKey} label="Email" description="The users email." readOnly={external}
                    required={!external} onChange={this.handleChange} value={user[emailKey]} errorName={emailKey}
                    errorValue={fieldErrors[emailKey]} />
                <DynamicSelectInput
                    name={roleNames}
                    id={roleNames}
                    label="Roles"
                    description="Select the roles you want associated with the user."
                    onChange={this.handleChange}
                    multiSelect={true}
                    options={this.retrieveRoles()}
                    value={user[roleNames]}
                    onFocus={this.props.getRoles} />
            </div>
        );
    }

    render() {
        const { canCreate, canDelete, fieldErrors, userDeleteError, inProgress, fetching } = this.props;
        const fieldErrorKeys = Object.keys(fieldErrors);
        const hasErrors = fieldErrorKeys && fieldErrorKeys.length > 0;
        return (
            <div>
                <div>
                    <TableDisplay
                        newConfigFields={this.createModalFields}
                        modalTitle="User"
                        clearModalFieldState={this.clearModalFieldState}
                        onConfigSave={this.onSave}
                        onConfigUpdate={this.onUpdate}
                        onConfigDelete={this.onDelete}
                        onConfigClose={this.onConfigClose}
                        refreshData={this.retrieveData}
                        editState={this.onEdit}
                        data={this.props.users}
                        columns={this.createColumns()}
                        newButton={canCreate}
                        deleteButton={canDelete}
                        hasFieldErrors={hasErrors}
                        errorDialogMessage={userDeleteError}
                        inProgress={inProgress}
                        fetching={fetching}
                    />
                </div>
            </div>
        );
    }
}

UserTable.defaultProps = {
    canCreate: true,
    canDelete: true,
    userDeleteError: null,
    fieldErrors: {},
    inProgress: false,
    fetching: false
};

UserTable.propTypes = {
    createUser: PropTypes.func.isRequired,
    updateUser: PropTypes.func.isRequired,
    deleteUser: PropTypes.func.isRequired,
    getUsers: PropTypes.func.isRequired,
    getRoles: PropTypes.func.isRequired,
    clearFieldErrors: PropTypes.func.isRequired,
    canCreate: PropTypes.bool,
    canDelete: PropTypes.bool,
    userDeleteError: PropTypes.string,
    fieldErrors: PropTypes.object,
    inProgress: PropTypes.bool,
    fetching: PropTypes.bool
};

const mapStateToProps = state => ({
    users: state.users.data,
    roles: state.roles.data,
    userDeleteError: state.users.userDeleteError,
    fieldErrors: state.users.fieldErrors,
    inProgress: state.users.inProgress,
    fetching: state.users.fetching
});

const mapDispatchToProps = dispatch => ({
    createUser: user => dispatch(createNewUser(user)),
    updateUser: user => dispatch(updateUser(user)),
    deleteUser: userId => dispatch(deleteUser(userId)),
    getUsers: () => dispatch(fetchUsers()),
    getRoles: () => dispatch(fetchRoles()),
    clearFieldErrors: () => dispatch(clearUserFieldErrors())
});

export default connect(mapStateToProps, mapDispatchToProps)(UserTable);
