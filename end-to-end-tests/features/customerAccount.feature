Feature: Customer account
  Scenario: Create a new customer account
    When A customer wants to register to DTU Pay with name "John"
    And customer cpr "123456-1234"
    And a customer DTUBank account
    Then the customer is added on DTUPay
    And Cleanup