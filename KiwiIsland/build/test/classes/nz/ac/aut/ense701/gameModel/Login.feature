Feature: Login

Scenario outline: Player is able to Login the game with correct existance account details.

Scenario: Login Successed
    Given input username:"123"
    And input password:"123"
    When Click at Login button
    Then Login Success

Scenario: Login Failed
    Given input username: "12345"
    And input password: "pwd3"
    When Click at Login button
    Then Login fail

