Feature: login user

  Scenario: successful login attempt should return token
    Given I register user:
      | emailAddress  | password  | passwordConfirm | firstName | lastName |
      | dave@test.com | Password1 | Password1       | Dave      | Johnston |
    When I log in as "dave@test.com" with password "Password1"
    Then the response should contain authorization token for "dave@test.com"

  Scenario: login failed should return 401
    Given I register user:
      | emailAddress  | password  | passwordConfirm | firstName | lastName |
      | dave@test.com | Password1 | Password1       | Dave      | Johnston |
    When I log in as "dave@test.com" with password "WrongPassword"
    Then the response status is 401
    And the error message is "Authentication Failed: Bad credentials"
