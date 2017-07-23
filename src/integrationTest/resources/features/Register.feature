Feature: register as a new user

  Scenario: Register a new user
    When I register a new user
    Then the user should have the "USER" role

  Scenario: new user must have a valid email address
    When I register user:
      | emailAddress |
      | invalid      |
    Then the response status is 400
    And the response has error "The email address is invalid."