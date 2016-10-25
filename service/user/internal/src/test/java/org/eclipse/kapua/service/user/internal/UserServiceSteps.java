package org.eclipse.kapua.service.user.internal;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.test.KapuaTest;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class UserServiceSteps extends KapuaTest {

    UserService userService = KapuaLocator.getInstance().getService(UserService.class);

    public static String DEFAULT_FILTER = "usr_*.sql";
    public static String DROP_FILTER = "usr_*_drop.sql";

    // scopeId is different for each test method so that data does not have to be cleared
    final KapuaEid scopeId = new KapuaEid(BigInteger.valueOf(random.nextLong()));

    UserCreator userCreator;
    
    User user;

    @Before
    public void beforeScenario() throws Exception {

        enableH2Connection();
        scriptSession((AbstractEntityManagerFactory) UserEntityManagerFactory.getInstance(), DEFAULT_FILTER);
        
        super.setUp();
    }

    @After
    public void afterScenario() throws Exception {

        scriptSession((AbstractEntityManagerFactory) UserEntityManagerFactory.getInstance(), DROP_FILTER);
        
        super.tearDown();
    }

    public UserServiceSteps() {
    }

    @Given("^User with name \"(.*)\"$")
    public void crateUserWitName(String userName) {
        long now = (new Date()).getTime();
        String username = userName;
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);

        userCreator = new UserFactoryImpl().newCreator(scopeId, username);

        userCreator.setDisplayName(displayName);
        userCreator.setEmail(userEmail);
        userCreator.setPhoneNumber("+1 555 123 4567");
    }

    @When("^I create user$")
    public void createUser() throws Exception {
        user = userService.create(userCreator);
    }

    @Then("^I find user with name \"(.*)\"$")
    public void loginButton(String userName) throws Exception {
        User foundUser = userService.find(user.getScopeId(), user.getId());
        
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

}
