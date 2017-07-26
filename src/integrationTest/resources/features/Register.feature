Feature: register as a new user

  Scenario: Register a new user
    When I register a new user
    Then the user should have the "USER" role

  Scenario: new user must provide an email address
    When I register user:
      | emailAddress | password  | passwordConfirm |
      |              | Password1 | Password1       |
    Then the response status is 400
    And the response contains error:
      | field        | message                         |
      | emailAddress | Email Address must be provided. |

  Scenario: new user must have a valid email address
    When I register user:
      | emailAddress | password  | passwordConfirm |
      | invalid      | Password1 | Password1       |
    Then the response status is 400
    And the response contains error:
      | field        | message                       |
      | emailAddress | The email address is invalid. |

  Scenario: new user must provide a unique email address
    Given I register user:
      | emailAddress  | password  | passwordConfirm |
      | dave@test.com | Password1 | Password1       |
    When I register user:
      | emailAddress  | password  | passwordConfirm |
      | dave@test.com | Password1 | Password1       |
    Then the response status is 400
    And the response contains error:
      | field        | message                              |
      | emailAddress | The email address is already in use. |

  Scenario: password must conform to requirements (uppercase, lowercase, digit and at least 8 characters)
    When I register user:
      | emailAddress  | password | passwordConfirm |
      | test@test.com | invalid  | invalid         |
    Then the response status is 400
    And the response contains error:
      | field    | message                    |
      | password | The password is not valid. |