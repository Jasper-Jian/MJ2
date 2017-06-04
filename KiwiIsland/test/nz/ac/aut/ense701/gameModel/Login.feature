Feature: Login

  Scenario outline: Player is able to Login the game with correct existance account details.
    Scenario: Login Successed
Given input username: "123"
    And input password: "123"
    When Click at Login button
    Then Login Success

  Scenario: Login Failed
    Given input username: "1234"
    And input password: "12312321"
    When Click at Login button
    Then Login Fail
