Feature: User Service
  User Service is responsible for CRUD operations on User objects in Kapua
  database.

Scenario: Creating user
  Create user with name kapua-user and persist it in database. Then try to
  find it.

  Given User with name "kapua-user"
  When I create user
  Then I find user with name "kapua-user"

Scenario: Update user
  First create user with name kapua-user and persist him in database,
  then find that same user and modify its name to kapua-modified and persist
  this user to database. At the end check that user name is changed to 
  kapua-modified.

  Given User with name "kapua-user"
  When I create user
    And I find user with name "kapua-user"
    And I change name to "kapua-modified"
  Then I find user with name "kapua-modified"

Scenario: Delete user
  Create user with name kapua-user. Then delete this user and check it is
  deleted. This means that if trying to search user, no such user is found.

  Given User with name "kapua-user"
  When I create user
    And I delete user
  Then I don't find user with name "kapua-user"

Scenario: Query user
  Create user with name kapua-user, than issue query for user based on scopeId.
  List of matching users should match single user.

  Given User with name "kapua-user"
  When I create user
    And I query for users in scope
  Then I count single user as query result list

Scenario: Count user
  Create user with name kapua-user, than issue count based on query that has
  scopeId specified. It is same as Query user, just that it only retrives count
  of results and not list of users. Count should match just one user.

  Given User with name "kapua-user"
  When I create user
    And I count for users in scope
  Then I count 1 user

Scenario: Create user that allready exist
  Create user wiht name kapua-user and than try to persiste it two times.
  KapuaException shuld be thrown in such scenario.

  Given User with name "kapua-user"
  When I create user
    And I create same user
  Then I get Kapua exception

Scenario: Update user that doesn't exist
  Create user that is not persisted and than run update statement on that user.
  As user doesn't exist KapuaException should be thrown.

  Given User that doesn't exist
  When I update nonexistent user
  Then I get Kapua exception

Scenario: Delete user that doesn't exist
  Create user that is not persised and than try to delete that user. As user
  doesn't exist KapuaException should be thrown.

  Given User that doesn't exist
  When I delete nonexistent user
  Then I get Kapua exception

Scenario: Find user with id and scope id that doesn't exist
  Try to find user that doesn't exist in database. As no user is present for current scopeId,
  issuing find by scopeId and unused user id, should return no user.

  When I search for user with id 123 in scope with id 456
  Then I find no user

Scenario: Find user by name that doesn't exist
  Search for user with name kapua-user. That user doesn't exist in database. As a result no
  user should be retruned.

  When I search for user with name "kapua-user"
  Then I find no user

Scenario: Delete Kapua system user
  Deletion of user with name "kapua-sys" should not be allowed. This is system user. So search
  for "kapua-sys" user and than delete it. KapuaException should be thrown.

  Given I search for user with name "kapua-sys"
  When I delete user
  Then I get Kapua exception

Scenario: Create multiple users
  Create three ordinar users in same scopeId and then count to see if there are 3 users in
  that scopeId.

  Given I have following users
    | username | scopeId |
    | kapua-u1 |    42    |
    | kapua-u2 |    42    |
    | kapua-u3 |    42    |
  When I count users in scope 42
  Then I count 3 users