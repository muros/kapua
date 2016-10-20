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

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.test.KapuaTest;
import org.eclipse.kapua.test.annotations.TestCase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserServiceTest extends KapuaTest {

    UserService userService = KapuaLocator.getInstance().getService(UserService.class);

    public static String DEFAULT_FILTER = "usr_*.sql";
    public static String DROP_FILTER = "usr_*_drop.sql";

    // scopeId is different for each test method so that data does not have to be cleared
    final KapuaEid scopeId = new KapuaEid(BigInteger.valueOf(random.nextLong()));

    @Before
    public void before() {
    }

    @BeforeClass
    public static void beforeClass() throws KapuaException {

        enableH2Connection();
        scriptSession((AbstractEntityManagerFactory) UserEntityManagerFactory.getInstance(), DEFAULT_FILTER);
    }

    @AfterClass
    public static void afterClass() throws KapuaException {

        scriptSession((AbstractEntityManagerFactory) UserEntityManagerFactory.getInstance(), DROP_FILTER);
    }

    @Test
    @TestCase(caseId = "KAPUA_0001")
    public void createUser() throws Exception {

        UserCreator userCreator = userCreatorCreator();

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
    }

    @Test
    @TestCase(caseId = "KAPUA_0002")
    public void updateUser() throws Exception {

        UserCreator userCreator = userCreatorCreator();

        User user = userService.create(userCreator);
        User userToBeUpdated = userService.find(user.getScopeId(), user.getId());

        user.setDisplayName("changedUserName");
        User updatedUser = userService.update(user);

        assertEquals("changedUserName", updatedUser.getDisplayName());
        assertTrue(updatedUser.getOptlock() > user.getOptlock());
        assertTrue(updatedUser.getModifiedOn().after(userToBeUpdated.getModifiedOn()));
    }

    @Test
    @TestCase(caseId = "KAPUA_0003")
    public void deleteUser() throws Exception {

        UserCreator userCreator = userCreatorCreator();

        User user = userService.create(userCreator);

        userService.delete(user);
        User deletedUser = userService.find(user.getScopeId(), user.getId());
        assertNull(deletedUser);
    }

    @Test
    @TestCase(caseId = "KAPUA_0004")
    public void queryUser() throws Exception {

        UserCreator userCreator = userCreatorCreator();

        userService.create(userCreator);

        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scopeId);
        UserListResult queryResult = userService.query(query);

        assertEquals(1, queryResult.getSize());
    }

    @Test
    @TestCase(caseId = "KAPUA_0005")
    public void countUser() throws Exception {

        UserCreator userCreator = userCreatorCreator();

        userService.create(userCreator);

        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scopeId);
        long userCnt = userService.count(query);

        assertEquals(1, userCnt);
    }

    @Test(expected = KapuaException.class)
    @TestCase(caseId = "KAPUA_0006")
    public void createUserThatExists() throws Exception {

        UserCreator userCreator = userCreatorCreator();

        userService.create(userCreator);
        userService.create(userCreator);
    }

    @Test(expected = KapuaException.class)
    @TestCase(caseId = "KAPUA_0007")
    public void updateNonExistentUser() throws Exception {

        User user = createUserInstance();

        userService.update(user);
    }

    @Test(expected = KapuaException.class)
    @TestCase(caseId = "KAPUA_0008")
    public void deleteNonExistentUser() throws Exception {

        User user = createUserInstance();

        userService.delete(user);
    }

    @Test
    @TestCase(caseId = "KAPUA_0009")
    public void findNonExistentUser() throws Exception {

        User user = userService.find(scopeId, scopeId);
        assertNull(user);

    }

    @Test
    @TestCase(caseId = "KAPUA_0010")
    public void findNonExistentUserByName() throws Exception {

        User user = userService.findByName("Nonexistent");
        assertNull(user);
    }

    @Test(expected = KapuaException.class)
    @TestCase(caseId = "KAPUA_0011")
    public void deleteSystemUser() throws Exception {

        User user = userService.findByName("kapua-sys");

        userService.delete(user);
        User sysUser = userService.findByName("kapua-sys");

        assertNotNull(sysUser);
    }

    @Test
    @TestCase(caseId = "KAPUA_0013")
    public void createMultipleUsers() throws Exception {

        for (int userCnt = 0; userCnt < 3; userCnt++) {
            UserCreator userCreator = userCreatorCreator();
            userService.create(userCreator);
        }

        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scopeId);
        long userCnt = userService.count(query);

        assertEquals(3, userCnt);
    }

    /**
     * Create userCreator instance with quasi random data for user name,
     * email and display name.
     * 
     * @return UserCreator instance for creating user
     */
    private UserCreator userCreatorCreator() {

        long now = (new Date()).getTime();
        String username = MessageFormat.format("aaa_test_username_{0,number,#}", now);
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);

        UserCreator userCreator = new UserFactoryImpl().newCreator(scopeId, username);

        userCreator.setDisplayName(displayName);
        userCreator.setEmail(userEmail);
        userCreator.setPhoneNumber("+1 555 123 4567");

        return userCreator;
    }

    /**
     * Create User object with user data filed with quasi random data for user name,
     * email, display name. Scope id and user id is set to test wide id.
     * 
     * @return User instance
     */
    private User createUserInstance() {
        long now = (new Date()).getTime();
        String username = MessageFormat.format("aaa_test_username_{0,number,#}", now);
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);

        User user = new UserImpl(scopeId, username);

        user.setId(scopeId);
        user.setName(username);
        user.setDisplayName(displayName);
        user.setEmail(userEmail);

        return user;
    }

}