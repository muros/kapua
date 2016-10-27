/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech
 *
 *******************************************************************************/
package org.eclipse.kapua.service.user.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.generator.id.IdGeneratorService;
import org.eclipse.kapua.service.generator.id.sequence.IdGeneratorServiceImpl;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.test.KapuaTest;
import org.eclipse.kapua.test.MockedLocator;
import org.mockito.Mockito;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Implementation of Gherkin steps used in UserService.feature scenarios.
 * 
 * MockedLocator is used for Location Service.
 * Mockito is used to mock other services that UserService is dependent on.
 * Dependent services are:
 * - Authorization Service
 * -
 * 
 *
 */
public class UserServiceSteps extends KapuaTest {

    /** User service is mocked in beforeScenario() */
    UserService userService = null;

    public static String DEFAULT_FILTER = "usr_*.sql";
    public static String DROP_FILTER = "usr_*_drop.sql";

    // scopeId is different for each test method so that data does not have to be cleared
    // TODO remove and create scope id for each case
    // final KapuaEid scopeId = new KapuaEid(BigInteger.valueOf(random.nextLong()));

    UserCreator userCreator;

    User user;

    UserListResult queryResult;

    long userCnt;

    boolean isException;

    Scenario scenario;

    @Before
    public void beforeScenario(Scenario scenario) throws Exception {

        this.scenario = scenario;
        this.isException = false;

        enableH2Connection();
        scriptSession((AbstractEntityManagerFactory) UserEntityManagerFactory.getInstance(), DEFAULT_FILTER);

        // Inject actual implementation of UserService
        userService = new UserServiceImpl();
        MockedLocator mockLocator = (MockedLocator) locator;
        mockLocator.setMockedService(org.eclipse.kapua.service.user.UserService.class, userService);

        // Inject mocked Authorization Service method checkPermission
        AuthorizationService mockedAuthorization = mock(AuthorizationService.class);
        Mockito.doNothing().when(mockedAuthorization).checkPermission(any(Permission.class));
        mockLocator.setMockedService(org.eclipse.kapua.service.authorization.AuthorizationService.class, mockedAuthorization);

        // Inject mocked Permission Factory
        PermissionFactory mockedPermissionFactory = mock(PermissionFactory.class);
        mockLocator.setMockedFactory(org.eclipse.kapua.service.authorization.permission.PermissionFactory.class, mockedPermissionFactory);

        // IdGenerator Service
        IdGeneratorService idGenerator = new IdGeneratorServiceImpl();
        mockLocator.setMockedService(org.eclipse.kapua.service.generator.id.IdGeneratorService.class, idGenerator);

        // Create KapuaSession using KapuaSecurtiyUtils and kapua-sys user as logged in user.
        // All operations on database are performed using system user.
        User user = userService.findByName("kapua-sys");
        KapuaSession kapuaSession = new KapuaSession(null, null, user.getScopeId(), user.getId(), user.getName());
        KapuaSecurityUtils.setSession(kapuaSession);
    }

    @After
    public void afterScenario() throws Exception {

        scriptSession((AbstractEntityManagerFactory) UserEntityManagerFactory.getInstance(), DROP_FILTER);

        KapuaSecurityUtils.clearSession();
    }

    public UserServiceSteps() {
    }

    @Given("^User with name \"(.*)\" in scope with id (\\d+)$")
    public void crateUserWithName(String userName, int scopeId) {
        long now = (new Date()).getTime();
        String username = userName;
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));

        userCreator = new UserFactoryImpl().newCreator(scpId, username);

        userCreator.setDisplayName(displayName);
        userCreator.setEmail(userEmail);
        userCreator.setPhoneNumber("+1 555 123 4567");

        scenario.write("User " + userName + " created.");
    }

    @When("^I create user$")
    public void createUser() throws Exception {
        user = userService.create(userCreator);
    }

    @When("^I change name to \"(.*)\"$")
    public void changeUserName(String userName) throws Exception {
        user.setName(userName);
        user = userService.update(user);
    }

    @When("^I delete user$")
    public void deleteUser() throws Exception {
        try {
            userService.delete(user);
        } catch (KapuaException ke) {
            isException = true;
        }
    }

    @When("^I search for user with name \"(.*)\"$")
    public void searchUserWithName(String userName) throws Exception {
        user = userService.findByName(userName);
    }

    @Then("^I find user with name \"(.*)\"$")
    public void findUserWithName(String userName) throws Exception {
        assertNotNull(user.getId());
        assertNotNull(user.getId().getId());
        assertTrue(user.getOptlock() >= 0);
        assertNotNull(user.getScopeId());
        assertEquals(userName, user.getName());
        assertNotNull(user.getCreatedOn());
        assertNotNull(user.getCreatedBy());
        assertNotNull(user.getModifiedOn());
        assertNotNull(user.getModifiedBy());
        assertEquals(userCreator.getDisplayName(), user.getDisplayName());
        assertEquals(userCreator.getEmail(), user.getEmail());
        assertEquals(userCreator.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(UserStatus.ENABLED, user.getStatus());
    }

    @Then("^I don't find user with name \"(.*)\"$")
    public void dontFindUserWithName(String userName) throws Exception {
        user = userService.findByName(userName);

        assertNull(user);
    }

    @Then("^I find no user$")
    public void noUserFound() {

        assertNull(user);
    }

    @When("^I search for user with id (\\d+) in scope with id (\\d+)$")
    public void searchUserWithIdAndScopeId(int userId, int scopeId) throws Exception {
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));
        KapuaEid usrId = new KapuaEid(BigInteger.valueOf(userId));
        user = userService.find(scpId, usrId);
    }

    @When("^I query for users in scope with id (\\d+)$")
    public void queryForUsers(int scopeId) throws Exception {
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));

        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scpId);
        queryResult = userService.query(query);
    }

    @When("^I count for users in scope with id (\\d+)$")
    public void countForUsers(int scopeId) throws Exception {
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));

        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scpId);
        userCnt = userService.count(query);
    }

    @When("^I count users in scope (\\d+)$")
    public void countUsersInScope(int scopeId) throws Exception {
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));
        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scpId);
        userCnt = userService.count(query);
    }

    @Then("^I count (\\d+) (?:user|users)$")
    public void countUserCount(int cnt) {
        assertEquals(cnt, userCnt);
    }

    @Then("^I count (\\d+) (?:user|users) as query result list$")
    public void countUserQuery(int cnt) {
        assertEquals(cnt, queryResult.getSize());
    }

    @Then("^I create same user$")
    public void createSameUser() throws Exception {
        try {
            user = userService.create(userCreator);
        } catch (KapuaException ke) {
            isException = true;
        }
    }

    @Then("^I get Kapua exception$")
    public void getKapuaException() throws Exception {
        if (!isException) {
            fail("Should fail with KapuaException.");
        }
    }

    @Given("^User that doesn't exist$")
    public void createNonexistentUser() {
        user = createUserInstance(3234123, 1354133);
    }

    @When("^I update nonexistent user$")
    public void updateNonexistenUser() {
        try {
            userService.update(user);
        } catch (KapuaException ke) {
            isException = true;
        }
    }

    @When("^I delete nonexistent user$")
    public void deleteNonexistenUser() {
        try {
            userService.delete(user);
        } catch (KapuaException ke) {
            isException = true;
        }
    }

    @Given("^I have following users$")
    public void followingUsers(DataTable table) throws Exception {
        for (Map<String, String> map : table.asMaps(String.class, String.class)) {
            String username = map.get("username");
            String scopeId = map.get("scopeId");
            KapuaEid scpId = new KapuaEid(BigInteger.valueOf(Integer.valueOf(scopeId)));

            UserCreator userCreator = userCreatorCreator(username, scpId);
            userService.create(userCreator);
        }
    }

    // *******************
    // * Private Helpers *
    // *******************
    /**
     * Create User object with user data filed with quasi random data for user name,
     * email, display name. Scope id and user id is set to test wide id.
     * 
     * @return User instance
     */
    private User createUserInstance(int userId, int scopeId) {
        long now = (new Date()).getTime();
        String username = MessageFormat.format("aaa_test_username_{0,number,#}", now);
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);
        KapuaEid usrId = new KapuaEid(BigInteger.valueOf(userId));
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));

        User user = new UserImpl(scpId, username);

        user.setId(usrId);
        user.setName(username);
        user.setDisplayName(displayName);
        user.setEmail(userEmail);

        return user;
    }

    /**
     * Create userCreator instance with quasi random data for user name,
     * email and display name.
     * 
     * @return UserCreator instance for creating user
     */
    private UserCreator userCreatorCreator(String userName, KapuaEid scopeId) {

        long now = (new Date()).getTime();
        String username = userName;
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);

        UserCreator userCreator = new UserFactoryImpl().newCreator(scopeId, username);

        userCreator.setDisplayName(displayName);
        userCreator.setEmail(userEmail);
        userCreator.setPhoneNumber("+1 555 123 4567");

        return userCreator;
    }
}
