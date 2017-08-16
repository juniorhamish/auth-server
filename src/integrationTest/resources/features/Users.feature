Feature: get users

  Scenario: only admin users can get all users
    Given I have user:
      | emailAddress  | password  | firstName | lastName | role |
      | dave@test.com | Password1 | Dave      | Johnston | USER |
    And I log in as "dave@test.com" with password "Password1"
    When I request all users
    Then the response status is 403

  Scenario: admin user sees all users
    Given I have users:
      | emailAddress   | password  | firstName | lastName | role  |
      | dave@test.com  | Password1 | Dave      | Johnston | USER  |
      | admin@test.com | Password1 | Admin     | Johnston | ADMIN |
    And I log in as "admin@test.com" with password "Password1"
    When I request all users
    Then the response contains users:
      | emailAddress   | firstName | lastName | role  |
      | dave@test.com  | Dave      | Johnston | USER  |
      | admin@test.com | Admin     | Johnston | ADMIN |

