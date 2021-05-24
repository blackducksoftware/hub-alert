/*
 * api-event
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.api.event;

import javax.jms.MessageListener;

public interface AlertEventListener extends MessageListener {
    String getDestinationName();

}
