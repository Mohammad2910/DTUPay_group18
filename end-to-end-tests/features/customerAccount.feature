Feature: Customer account
  Scenario: Create a new customer account
    When A customer wants to register to DTU Pay with name "John"
    And customer cpr "123456-1234"
    And a customer DTUBank account
    Then the customer is added on DTUPay
    And Cleanup

  Scenario: Delete a customer account from DTU Pay
    When a customer's name is "John", cpr is "123456-1234" and has a DTUBank account
    And customer is registered to DTU Pay
    And the customer wants to delete their account
    Then the customer's account is deleted and gets a response