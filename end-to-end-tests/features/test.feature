Feature: Customer account
  Scenario: Create a new customer account
    When A customer wants to register to DTU Pay with name "John"
    And cpr "123456-1234"
    And a DTUBank account
    Then It is added on the account list
    And Cleanup