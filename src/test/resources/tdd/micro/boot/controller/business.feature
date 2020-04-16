Feature: Transactions

Scenario: A Get inexisting transaction
    Given A transaction that is not stored in our system
    When I check the status from any channel
    Then The system returns the status 'INVALID'

Scenario: B Get Transaction Before Today CLIENT
    Given A transaction that is stored in our system
    When I check the status from CLIENT channel
    And the transaction date is before today
    Then The system returns the status 'SETTLED'
    And the amount substracting the fee

Scenario: B Get Transaction Before Today ATM
    Given A transaction that is stored in our system
    When I check the status from ATM channel
    And the transaction date is before today
    Then The system returns the status 'SETTLED'
    And the amount substracting the fee