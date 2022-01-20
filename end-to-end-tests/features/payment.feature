Feature: Payment related
  Scenario: Add a payment successfully
    When We add a payment with token "customer_token"
    And merchant id "mid1"
    And amount "10" kr
    Then It is added on the payment list