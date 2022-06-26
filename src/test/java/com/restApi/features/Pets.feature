#Data Table having keywords GenerateNumber/GenerateString is then updated with dynamic values in step definition
#For Pets request json, sample json is used as template however for Store and User json dynamic json gets created

Feature: Verify all Pet Operations

  Scenario: Verify Put, Post and Delete Pet operation
    Given I create a new pet with url "/pet" and petId variable
      | id            | GenerateNumber |
      | name          | GenerateString |
      | category_id   | 2              |
      | category_name | Cats           |
      | photoUrls     | imageUrl       |
      | tags_id       | GenerateNumber |
      | tags_name     | GenerateNumber |
      | status        | available      |
    When I perform put operation for url "/pet/" with petId variable updating status as sold
    Then I perform get operation to fetch pet details for url "/pet/" with petId variable
    And I verify response details
    When I perform post operation for url "/pet/" with petId variable updating name as Rocky and status as sold
    Then I perform get operation to fetch pet details for url "/pet/" with petId variable
    And I verify response details
    When I perform delete operation to delete pet details for url "/pet/" with petId variable
    Then I verify response has "Pet deleted" with status code 200

  Scenario: Verify Invalid Pet ID Scenario
    When I perform get operation to fetch pet details for url "/pet/" with petId value a
    Then I verify response has "Input error" with status code 400

  Scenario: Verify Pet Not Found Scenario
    When I perform get operation to fetch pet details for url "/pet/" with petId value 1500
    Then I verify response has "Pet not found" with status code 404

  Scenario: Verify Get Pet details by tags
    When I perform get operation to fetch pet details for url "/pet/findByTags" with tags value string
    Then I verify response with status code 200

  Scenario: Verify Get Pet details by status
    When I perform get operation to fetch pet details for url "/pet/findByStatus" with status value available
    Then I verify response with status code 200

 