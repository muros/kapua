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
package org.eclipse.kapua.broker.core.converter;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.message.CamelKapuaMessage;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor.MESSAGE_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

/**
 * Kapua message converter used to convert life cycle messages.
 * 
 * @since 1.0
 */
public class KapuaLifeCycleConverter extends AbstractKapuaConverter
{

    public static final Logger logger = LoggerFactory.getLogger(KapuaLifeCycleConverter.class);

    private Counter metricConverterAppMessage;
    private Counter metricConverterBirthMessage;
    private Counter metricConverterDcMessage;
    private Counter metricConverterMissingMessage;
    private Counter metricConverterNotifyMessage;

    public KapuaLifeCycleConverter()
    {
        super();
        metricConverterAppMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "app", "count");
        metricConverterBirthMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "birth", "count");
        metricConverterDcMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "dc", "count");
        metricConverterMissingMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "missing", "count");
        metricConverterNotifyMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "notify", "count");
    }

    /**
     * Convert incoming message to a Kapua application (life cycle) message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains application message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    @Converter
    public CamelKapuaMessage<?> convertToApps(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterAppMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.app);
    }

    /**
     * Convert incoming message to a Kapua birth (life cycle) message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains birth message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    @Converter
    public CamelKapuaMessage<?> convertToBirth(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterBirthMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.birth);
    }

    /**
     * Convert incoming message to a Kapua disconnect (life cycle) message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains disconnect message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    @Converter
    public CamelKapuaMessage<?> convertToDisconnect(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterDcMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.disconnect);
    }

    /**
     * Convert incoming message to a Kapua missing (life cycle) message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains missing message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    @Converter
    public CamelKapuaMessage<?> convertToMissing(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterMissingMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.missing);
    }

    /**
     * Convert incoming message to a Kapua notify (life cycle) message
     * 
     * @param exchange
     * @param value
     * @return
     * @throws KapuaException
     */
    @Converter
    public CamelKapuaMessage<?> convertToNotify(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterNotifyMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.notify);
    }

    /**
     * Convert incoming message to a Kapua unmatched (life cycle) message
     * 
     * @param exchange
     * @param value
     * @return
     * @throws KapuaException
     */
    @Converter
    public CamelKapuaMessage<?> convertToUnmatched(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterNotifyMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.unmatched);
    }

}
