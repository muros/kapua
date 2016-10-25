Feature: CucumberJava

@KAPUA_0001
Scenario: Creating user
Given User with name "Uros"
When I create user
Then I find user with name "Uros"