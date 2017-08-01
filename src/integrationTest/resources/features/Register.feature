Feature: register as a new user

  Scenario: Register a new user
    When I register a new user
    Then the user should have the "USER" role

  Scenario: new user must provide an email address
    When I register user:
      | emailAddress | password  | passwordConfirm | firstName | lastName |
      |              | Password1 | Password1       | Dave      | Johnston |
    Then the response status is 400
    And the response contains error:
      | field        | message                         |
      | emailAddress | Email Address must be provided. |

  Scenario: new user must have a valid email address
    When I register user:
      | emailAddress | password  | passwordConfirm | firstName | lastName |
      | invalid      | Password1 | Password1       | Dave      | Johnston |
    Then the response status is 400
    And the response contains error:
      | field        | message                       |
      | emailAddress | The email address is invalid. |

  Scenario: new user must provide a unique email address
    Given I register user:
      | emailAddress  | password  | passwordConfirm | firstName | lastName |
      | dave@test.com | Password1 | Password1       | Dave      | Johnston |
    When I register user:
      | emailAddress  | password  | passwordConfirm | firstName | lastName |
      | dave@test.com | Password1 | Password1       | Dave      | Johnston |
    Then the response status is 400
    And the response contains error:
      | field        | message                              |
      | emailAddress | The email address is already in use. |

  Scenario: password must conform to requirements (uppercase, lowercase, digit and at least 8 characters)
    When I register user:
      | emailAddress  | password | passwordConfirm | firstName | lastName |
      | test@test.com | invalid  | invalid         | Dave      | Johnston |
    Then the response status is 400
    And the response contains error:
      | field    | message                    |
      | password | The password is not valid. |

  Scenario: password and passwordConfirm must match
    When I register user:
      | emailAddress  | password  | passwordConfirm | firstName | lastName |
      | test@test.com | Password1 | Password2       | Dave      | Johnston |
    Then the response status is 400
    And the response contains error:
      | field           | message                     |
      | passwordConfirm | The passwords do not match. |

  Scenario: user must provide a first name
    When I register user:
      | emailAddress  | password  | passwordConfirm | firstName | lastName |
      | test@test.com | Password1 | Password1       |           | Johnston |
    Then the response status is 400
    And the response contains error:
      | field     | message                      |
      | firstName | First name must be provided. |

  Scenario: user must provide a last name
    When I register user:
      | emailAddress  | password  | passwordConfirm | firstName | lastName |
      | test@test.com | Password1 | Password1       | Dave      |          |
    Then the response status is 400
    And the response contains error:
      | field    | message                     |
      | lastName | Last name must be provided. |

  Scenario: multiple validation problems should be in the response
    When I register user:
      | emailAddress | password | passwordConfirm | firstName | lastName |
      | invalid      | password | password2       |           |          |
    Then the response status is 400
    And the response contains error:
      | field           | message                       |
      | emailAddress    | The email address is invalid. |
      | password        | The password is not valid.    |
      | passwordConfirm | The passwords do not match.   |
      | firstName       | First name must be provided.  |
      | lastName        | Last name must be provided.   |
