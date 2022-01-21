Feature: Token scenarios
  Scenario: Request a token for a new account
    When A customer wants to register to DTU Pay with name "John"
    And cpr "123456-1234"
    And a DTUBank account
    Then It is added on the account list
    And Cleanup
