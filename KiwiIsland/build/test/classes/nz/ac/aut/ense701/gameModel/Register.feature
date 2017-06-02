Feature: Register
  Scenario outline: Player is able to Login the game with correct existance account details.
   
Scenario: Register Successed
    Given input register username: "1234"
    And input register password: "123"
    When Click at Login button
    Then Login Success

  Scenario: Register Failed
    Given input register username: "123"
    And input register password: "123"
    When Click at Login button
    Then Login Fail
