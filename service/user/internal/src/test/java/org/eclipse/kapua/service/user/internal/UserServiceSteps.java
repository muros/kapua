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

public class UserServiceSteps extends KapuaTest {

    UserService userService = null; // KapuaLocator.getInstance().getService(UserService.class);

    public static String DEFAULT_FILTER = "usr_*.sql";
    public static String DROP_FILTER = "usr_*_drop.sql";

    // scopeId is different for each test method so that data does not have to be cleared
    final KapuaEid scopeId = new KapuaEid(BigInteger.valueOf(random.nextLong()));

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

        // Set real UserService implementation
        userService = new UserServiceImpl();
        MockedLocator mockLocator = (MockedLocator) locator;
        mockLocator.setMockedService(org.eclipse.kapua.service.user.UserService.class, userService);

        AuthenticationService mockedAuthentication = mock(AuthenticationService.class);
        mockLocator.setMockedService(org.eclipse.kapua.service.authentication.AuthenticationService.class, mockedAuthentication);

        AuthorizationService mockedAuthorization = mock(AuthorizationService.class);
        Mockito.doNothing().when(mockedAuthorization).checkPermission(any(Permission.class));
        mockLocator.setMockedService(org.eclipse.kapua.service.authorization.AuthorizationService.class, mockedAuthorization);

        PermissionFactory mockedPermissionFactory = mock(PermissionFactory.class);
        mockLocator.setMockedFactory(org.eclipse.kapua.service.authorization.permission.PermissionFactory.class, mockedPermissionFactory);

        IdGeneratorService idGenerator = new IdGeneratorServiceImpl();
        mockLocator.setMockedService(org.eclipse.kapua.service.generator.id.IdGeneratorService.class, idGenerator);

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

    @Given("^User with name \"(.*)\"$")
    public void crateUserWithName(String userName) {
        long now = (new Date()).getTime();
        String username = userName;
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);

        userCreator = new UserFactoryImpl().newCreator(scopeId, username);

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

    @Then("^I find user with name \"(.*)\"$")
    public void findUserWithName(String userName) throws Exception {
        user = userService.find(user.getScopeId(), user.getId());

        assertNotNull(user.getId());
        assertNotNull(user.getId().getId());
        assertTrue(user.getOptlock() >= 0);
        assertEquals(scopeId, user.getScopeId());
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
        user = userService.find(user.getScopeId(), user.getId());

        assertNull(user);
    }

    @Then("^I find no user$")
    public void noUserFound() {

        assertNull(user);
    }

    @When("^I search for user with id (\\d+) in scope with id (\\d+)$")
    public void dontFindUserWithName(int userId, int scopeId) throws Exception {
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));
        KapuaEid usrId = new KapuaEid(BigInteger.valueOf(userId));
        user = userService.find(scpId, usrId);
    }

    @When("^I query for users in scope$")
    public void queryForUsers() throws Exception {
        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scopeId);
        queryResult = userService.query(query);
    }

    @When("^I count for users in scope$")
    public void countForUsers() throws Exception {
        KapuaQuery<User> query = new UserFactoryImpl().newQuery(scopeId);
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

    @Then("^I count single user as query result list$")
    public void countUserQuery() {
        assertEquals(1, queryResult.getSize());
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
        user = createUserInstance();
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

    @When("^I search for user with name \"(.*)\"$")
    public void findUserByName(String userName) throws Exception {
        user = userService.findByName(userName);
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
