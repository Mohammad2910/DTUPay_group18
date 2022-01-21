Feature: Customer account
  Scenario: Create a new customer account
    When A customer wants to register to DTU Pay with name "G18 Customer"
    And customer cpr "456789-3456"
    And a customer DTUBank account
    Then the customer is added on DTUPay
    And Cleanup

  Scenario: Delete a customer account from DTU Pay
    When a customer's name is "G18 Customer", cpr is "456789-3456" and has a DTUBank account
    And customer is registered to DTU Pay
    And the customer wants to delete their account
    Then the customer's account is deleted and gets a response
    And Cleanup