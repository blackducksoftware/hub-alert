import {
    SYSTEM_LATEST_MESSAGES_FETCH_ERROR,
    SYSTEM_LATEST_MESSAGES_FETCHED,
    SYSTEM_LATEST_MESSAGES_FETCHING,
    SYSTEM_SETUP_FETCH_ERROR,
    SYSTEM_SETUP_FETCH_REDIRECTED,
    SYSTEM_SETUP_FETCHED,
    SYSTEM_SETUP_FETCHING,
    SYSTEM_SETUP_UPDATE_ERROR,
    SYSTEM_SETUP_UPDATED,
    SYSTEM_SETUP_UPDATING
} from 'store/actions/types';
import { verifyLoginByStatus } from 'store/actions/session';
import * as ConfigRequestBuilder from 'util/configurationRequestBuilder';

const LATEST_MESSAGES_URL = '/alert/api/system/messages/latest';
const INITIAL_SYSTEM_SETUP_URL = '/alert/api/system/setup/initial';

function fetchingLatestSystemMessages() {
    return {
        type: SYSTEM_LATEST_MESSAGES_FETCHING
    };
}

/**
 * Triggers Confirm config was fetched
 * @returns {{type}}
 */
function latestSystemMessagesFetched(latestMessages) {
    return {
        type: SYSTEM_LATEST_MESSAGES_FETCHED,
        latestMessages
    };
}

function latestSystemMessagesError() {
    return {
        type: SYSTEM_LATEST_MESSAGES_FETCH_ERROR
    };
}

function fetchingSystemSetup() {
    return {
        type: SYSTEM_SETUP_FETCHING
    };
}

function fetchSetupRedirected() {
    return {
        type: SYSTEM_SETUP_FETCH_REDIRECTED
    };
}

function systemSetupFetched(settingsData) {
    return {
        type: SYSTEM_SETUP_FETCHED,
        settingsData
    };
}

function systemSetupFetchError(message) {
    return {
        type: SYSTEM_SETUP_FETCH_ERROR,
        message
    };
}

function updatingSystemSetup() {
    return {
        type: SYSTEM_SETUP_UPDATING
    };
}

function systemSetupUpdated() {
    return {
        type: SYSTEM_SETUP_UPDATED
    };
}

function systemSetupUpdateError(message, errors) {
    return {
        type: SYSTEM_SETUP_UPDATE_ERROR,
        message,
        errors
    };
}

export function getLatestMessages() {
    return (dispatch) => {
        dispatch(fetchingLatestSystemMessages());
        fetch(LATEST_MESSAGES_URL)
            .then((response) => {
                if (response.ok) {
                    response.json().then((body) => {
                        dispatch(latestSystemMessagesFetched(body));
                    });
                } else {
                    dispatch(verifyLoginByStatus(response.status));
                }
            })
            .catch(console.error);
    };
}


export function getInitialSystemSetup() {
    return (dispatch) => {
        dispatch(fetchingSystemSetup());
        fetch(INITIAL_SYSTEM_SETUP_URL)
            .then((response) => {
                if (response.redirected) {
                    dispatch(fetchSetupRedirected());
                } else if (response.ok) {
                    response.json().then((body) => {
                        dispatch(systemSetupFetched(body));
                    });
                } else {
                    dispatch(systemSetupFetchError(response.statusText));
                }
            })
            .catch(console.error);
    };
}

export function getSystemSetup() {
    return (dispatch, getState) => {
        dispatch(fetchingSystemSetup());
        const { csrfToken } = getState().session;
        const request = ConfigRequestBuilder.createReadAllGlobalContextRequest(csrfToken, 'component_settings');
        request.then((response) => {
            if (response.ok) {
                response.json().then((body) => {
                    if (body.length === 1) {
                        dispatch(systemSetupFetched(body[0]));
                    } else {
                        dispatch(systemSetupFetchError('System Settings not found'));
                    }
                });
            } else {
                dispatch(systemSetupFetchError(response.statusText));
            }
        })
            .catch(console.error);
    };
}

export function saveInitialSystemSetup(setupData) {
    return (dispatch, getState) => {
        dispatch(updatingSystemSetup());
        const { csrfToken } = getState().session;
        const options = {
            credentials: 'same-origin',
            method: 'POST',
            body: JSON.stringify(setupData),
            headers: {
                'content-type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            }
        };

        fetch(INITIAL_SYSTEM_SETUP_URL, options)
            .then((response) => {
                if (response.ok) {
                    dispatch(systemSetupUpdated());
                    dispatch(getInitialSystemSetup());
                } else {
                    response.json().then((body) => {
                        const jsonErrors = body.errors;
                        if (jsonErrors) {
                            const errors = {};
                            Object.keys(jsonErrors).forEach((key) => {
                                if (jsonErrors[key]) {
                                    const value = jsonErrors[key];
                                    errors[key] = value;
                                }
                            });
                            dispatch(systemSetupUpdateError(body.message, errors));
                        }
                    });
                }
            })
            .catch(console.error);
    };
}

export function saveSystemSetup(setupData) {
    return (dispatch, getState) => {
        dispatch(updatingSystemSetup());
        const { csrfToken } = getState().session;
        const request = ConfigRequestBuilder.createUpdateRequest(csrfToken, setupData.id, setupData);
        request.then((response) => {
            if (response.ok) {
                dispatch(systemSetupUpdated());
                dispatch(getSystemSetup());
            } else {
                response.json().then((body) => {
                    const jsonErrors = body.errors;
                    if (jsonErrors) {
                        const errors = {};
                        Object.keys(jsonErrors).forEach((key) => {
                            if (jsonErrors[key]) {
                                const value = jsonErrors[key];
                                errors[key] = value;
                            }
                        });
                        dispatch(systemSetupUpdateError(body.message, errors));
                    }
                });
            }
        })
            .catch(console.error);
    };
}
