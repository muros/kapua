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
package org.eclipse.kapua.service.user.internal;

import static org.eclipse.kapua.commons.security.KapuaSecurityUtils.doPriviledge;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.test.KapuaTest;
import org.eclipse.kapua.test.annotations.TestCase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserServiceTest  extends KapuaTest {

	UserService userService = KapuaLocator.getInstance().getService(UserService.class);
	
	public static String DEFAULT_FILTER = "usr_*.sql";
    public static String DROP_FILTER = "usr_*_drop.sql";
 
    KapuaEid scopeId = new KapuaEid(BigInteger.valueOf(random.nextLong()));
    
    @Before
    public void before() {
    }
    
    @BeforeClass
    public static void beforeClass() throws KapuaException {
        enableH2Connection();
        scriptSession((AbstractEntityManagerFactory)UserEntityManagerFactory.getInstance(), DEFAULT_FILTER);
    }

    @AfterClass
    public static void afterClass() throws KapuaException {
        scriptSession((AbstractEntityManagerFactory)UserEntityManagerFactory.getInstance(), DROP_FILTER);
    }
    
    @Test
    @TestCase(caseId="KAPUA_0001")
    public void createUser() throws Exception {
        doPriviledge(() -> {
        	long now = (new Date()).getTime();
            String username = MessageFormat.format("aaa_test_username_{0,number,#}", now);
            String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
            String displayName = MessageFormat.format("User Display Name {0}", now);
        	
            UserCreator userCreator = new UserFactoryImpl().newCreator(scopeId, username);
            
            userCreator.setDisplayName(displayName);
            userCreator.setEmail(userEmail);
            userCreator.setPhoneNumber("+1 555 123 4567");
            
            User user = userService.create(userCreator);
            user = userService.find(user.getScopeId(), user.getId());
            
            assertNotNull(user.getId());
            assertNotNull(user.getId().getId());
            assertTrue(user.getOptlock() >= 0);
            assertEquals(scopeId, user.getScopeId());
            assertEquals(userCreator.getName(), user.getName());
            assertNotNull(user.getCreatedOn());
            assertNotNull(user.getCreatedBy());
            assertNotNull(user.getModifiedOn());
            assertNotNull(user.getModifiedBy());
            assertEquals(userCreator.getDisplayName(), user.getDisplayName());
            assertEquals(userCreator.getEmail(), user.getEmail());
            assertEquals(userCreator.getPhoneNumber(), user.getPhoneNumber());
            assertEquals(UserStatus.ENABLED, user.getStatus());
             
        	return null;
        });
    }

    @Test
    @TestCase(caseId="KAPUA_0002")
    public void updateUser() {
    	fail("Not implemented.");
    }

    @Test
    @TestCase(caseId = "KAPUA_0003")
    public void deleteUser() {
        fail("Not implemented.");
    }
    
    @Test
    @TestCase(caseId = "KAPUA_0004")
    public void findAllUsers() throws Exception {
        fail("Not implemented.");
    }
    
    @Test
    @TestCase(caseId = "KAPUA_0005")
    public void updatePermisionsRoles() throws Exception {
        fail("Not implemented.");
    }
    
    @Test
    @TestCase(caseId = "KAPUA_0006")
    public void selfViewManagePermission() throws Exception {
        fail("Not implemented.");
    }

}
