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
package org.eclipse.kapua.commons.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * Kapua updatable entity default abstract implementation.
 *
 * @since 1.0
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class AbstractKapuaUpdatableEntity extends AbstractKapuaEntity implements KapuaUpdatableEntity
{
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_on")
    protected Date     modifiedOn;

    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "modified_by"))
    })
    protected KapuaEid modifiedBy;

    @Version
    @Column(name = "optlock")
    protected int      optlock;

    @Basic
    @Column(name = "attributes")
    protected String   attributes;

    @Basic
    @Column(name = "properties")
    protected String   properties;

    /**
     * Constructor
     */
    protected AbstractKapuaUpdatableEntity()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param scopeId
     */
    public AbstractKapuaUpdatableEntity(KapuaId scopeId)
    {
        super(scopeId);
    }

    @Override
    public Date getModifiedOn()
    {
        return modifiedOn;
    }

    @Override
    public KapuaId getModifiedBy()
    {
        return modifiedBy;
    }

    @Override
    public int getOptlock()
    {
        return optlock;
    }

    @Override
    public void setOptlock(int optlock)
    {
        this.optlock = optlock;
    }

    // -------------------------------------------------
    //
    // Attributes APIs
    //
    // -------------------------------------------------

    @Override
    public Properties getEntityAttributes()
        throws KapuaException
    {
        Properties props = new Properties();
        if (attributes != null) {
            try {
                props.load(new StringReader(attributes));
            }
            catch (IOException e) {
                KapuaException.internalError(e);
            }
        }
        return props;
    }

    @Override
    public void setEntityAttributes(Properties props)
        throws KapuaException
    {
        if (props != null) {
            try {
                StringWriter writer = new StringWriter();
                props.store(writer, null);
                attributes = writer.toString();
            }
            catch (IOException e) {
                KapuaException.internalError(e);
            }
        }
    }

    // -------------------------------------------------
    //
    // Properties APIs
    //
    // -------------------------------------------------

    @Override
    public Properties getEntityProperties()
        throws KapuaException
    {
        Properties props = new Properties();
        if (properties != null) {
            try {
                props.load(new StringReader(properties));
            }
            catch (IOException e) {
                KapuaException.internalError(e);
            }
        }
        return props;
    }

    @Override
    public void setEntityProperties(Properties props)
        throws KapuaException
    {
        if (props != null) {
            try {
                StringWriter writer = new StringWriter();
                props.store(writer, null);
                properties = writer.toString();
            }
            catch (IOException e) {
                KapuaException.internalError(e);
            }
        }
    }

    @Override
    protected void prePersistsAction()
        throws KapuaException
    {
        super.prePersistsAction();
        this.modifiedBy = this.createdBy;
        this.modifiedOn = this.createdOn;
    }

    /**
     * Before update action to correctly set the modified on and modified by fields
     * 
     * @throws KapuaException
     */
    @PreUpdate
    protected void preUpdateAction()
        throws KapuaException
    {
        this.modifiedBy = (KapuaEid) KapuaSecurityUtils.getSession().getUserId();
        this.modifiedOn = new Date();
    }
    
    /**
     * Set the modified on date
     * 
     * @param modifiedOn
     */
    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
    
    /**
     * Set the modified by identifier
     * 
     * @param modifiedBy
     */
    public void setModifiedBy(KapuaId modifiedBy) {
        this.modifiedBy = (KapuaEid)modifiedBy;
    }
}
