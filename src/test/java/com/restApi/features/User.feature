#Data Table having keywords GenerateNumber/GenerateString is then updated with dynamic values in step definition
#For Pets request json, sample json is used as template however for Store and User json dynamic json gets created

Feature: Verify all User operations

  Scenario Outline: Verify Get User Login/Logout operations
    Given I Perform get operation for <urls> url
      | username | password |
      | test     | 123      |
    Examples:
      | urls   |
      | login  |
      | logout |

  Scenario: Verify Create, Put and Delete User operation
    Given I create a new single user with url "/user" and userName variable
      | id         | GenerateNumber |
      | username   | GenerateString |
      | firstName  | GenerateString |
      | lastName   | GenerateString |
      | email      | approved       |
      | password   | true           |
      | phone      | GenerateNumber |
      | userStatus | 1              |
    When I perform put operation for url "/user/" with userName variable updating phone as 1234578
    Then I perform get operation to fetch user details for url "/user/" with userName variable
    And I verify response details
    When I perform delete operation to delete user details for url "/user/" with userName variable
    Then I perform get operation to fetch user details for url "/user/" with userName variable
    Then I verify response has "User not found" with status code 404

  Scenario: Verify CreateList User operation
    Given I create a new multiple user with url "/user/createWithList" and userName variable
      | id         | GenerateNumber | GenerateNumber |
      | username   | GenerateString | GenerateString |
      | firstName  | GenerateString | GenerateString |
      | lastName   | GenerateString | GenerateString |
      | email      | approved       | approved       |
      | password   | true           | true           |
      | phone      | GenerateNumber | GenerateNumber |
      | userStatus | 1              | 1              |
    Then I perform get operation to fetch user details for url "/user/" with userName variable
    And I verify response details list

