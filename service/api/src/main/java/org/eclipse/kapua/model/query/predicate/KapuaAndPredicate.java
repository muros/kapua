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
package org.eclipse.kapua.model.query.predicate;

import java.util.List;

/**
 * Kapua sql and predicate definition.
 * 
 * @since 1.0
 * 
 */
public interface KapuaAndPredicate extends KapuaPredicate
{
    /**
     * Creates an "and predicate" from a generic predicate
     * 
     * @param predicate
     * @return
     */
    public KapuaAndPredicate and(KapuaPredicate predicate);

    /**
     * Returns a list of predicates
     * 
     * @return
     */
    public List<KapuaPredicate> getPredicates();
}
