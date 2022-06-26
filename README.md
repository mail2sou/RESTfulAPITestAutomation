# RESTfulAPITestAutomation framework for SwaggerUI
### Author: Sougata Bandyopadhyay

### Pre-Requisites

1. Download and install Java JDK 11
2. Download and install Apache Maven and set up environment variables for Maven

### Solution Approach
1. I have chosen a BDD cucumber framework so that the scenarios can be understood by any stakeholders. This is also a very common but efficient industrial approach for developing RestAPI test automation framework as the stories in the form of Gherkin can directly come from PO or from the derived test scenarios from large epics or stories

### List of test cases proposed for automation
1. Verify Put, Post and Delete Pet operation
2. Verify Invalid Pet ID Scenario
3. Verify Pet Not Found Scenario
4. Verify Get Pet details by tags
5. Verify Get Pet details by status
6. Verify Get User Login/Logout operations
7. Verify Create, Put and Delete User operation for a single user
8. Verify CreateList User operation for multiple user
9. Verify Create and Delete Store operation
10. Verify Invalid ID supplied Scenario
11. Verify Order Not Found Scenario
12. Verify get pet Inventories by status Scenario

### Automating proposed scenarios on different levels
1. On Dev or test level, we should automate as much test cases as possible and hence we can run these test cases on dev and test levels where the QA team generally targets more code coverage. API testing is very fast and the amount of time it takes to execute these test cases or similar API testing framework is less and hence covering as many scenarios as possible through test automation is desired.
2. Based on the application quality, we may skip the negative scenarios in higher levels of testing like Staging or Pre Production. On higher levels we should execute test case 1,4,6,7,8,9,12. If we execute these test cases, from quality perspective we will be very confident on the product and can pass on the same confidence to the respective stakeholders.

### Tech-Stack used in the framework
1. Framework: Cucumber BDD RestAssured framework with TestNG and ExtentReports
2. Build tool: Maven
3. IDE used: IntelliJ IDEA Community Edition (version 2021.2.2)
4. Programming Language Used: Java

### How to execute the tests
1. Clone the project from GitHub to your local repository
2. Run this project as Maven Build using commands <I><B>mvn clean test</I></B>
3. Make sure you are behind firewall while resolving dependencies of Maven

### Getting the test report
1. Once the execution is complete, the test reports can be found in "Reports\Spark.html"

