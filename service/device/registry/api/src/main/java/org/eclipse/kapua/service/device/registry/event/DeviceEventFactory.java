/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.event;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;

import java.util.Date;

/**
 * Device event factory service definition.
 * 
 * @since 1.0
 *
 */
public interface DeviceEventFactory extends KapuaObjectFactory
{

    /**
     * Creates a new device event creator
     * 
     * @param scopeId
     * @param deviceId
     * @param receivedOn
     * @param resource
     * @return
     */
    DeviceEventCreator newCreator(KapuaId scopeId, KapuaId deviceId, Date receivedOn, String resource);

    /**
     * Creates a new device event query based on provided scope identifier
     * 
     * @param scopeId
     * @return
     */
    DeviceEventQuery newQuery(KapuaId scopeId);

    /**
     * Creates a new {@link DeviceEvent}
     * 
     * @return
     */
    DeviceEvent newDeviceEvent();

    /**
     * Creates a new device event list result
     * 
     * @return
     */
    DeviceEventListResult newDeviceListResult();
}
