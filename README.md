##### Description:
This program is implemented as spring boot application. It exposed 2 endpoints i.e. for registering goal and executing 
round up goals for registered account. You can only register same goal (ignore case) for same account though multiple goals
(with different names ) can be registered on the system. When you execute the rounding up for account, rounding up will 
be done on all the account goals.

Please note that if goal got removed from the Starling bank, the round up for the will fail, though it's registered with app.

##### Assumptions
* Only transactions having `direction : OUT, status : SETTLED are considered for round-up.
* If round up has api is executed multiple time with same details, then the already executed feeds will be ignored.
* As the currency for account, saving goal target is tightly coupled, extracting currency from account for saving goal.

##### Instructions:
* This project can be run with Java SE 18 or upwards, and Gradle 7.4 or upwards.
* Run `./gradlew bootJar` to build the project.
  This will create an executable `SavingsGoal-1.0-SNAPSHOT.jar` file in the `build/libs` directory.
* To run the application issue the following command:  
  `java -jar build/libs/SavingsGoal-1.0-SNAPSHOT.jar`
* Application will start on port 7001
* You can access api's using curl as below
  * To register saving goal for account :
    `curl --location --request POST 'localhost:7001/starling/savings-app/account/<ACCOUNT_UID>/saving-goal' \
    --header 'Authorization: Bearer <THE ACCESS TOKEN FOR ACCOUNT> \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "name" : "<Your saving goal name>",
    "targetAmount": <Amount in minor currency denominator>
    }'`
  * To run roundup execution
    `curl --location --request PUT 'localhost:7001/starling/savings-app/account/bbebc062-5216-465c-bf34-81b3191135a4/execute-round-up' \
    --header 'Authorization: Bearer <THE ACCESS TOKEN FOR ACCOUNT> - \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "fromDate" : "<Date in yyyy-MM-dd format>",
    "previousDays": <Up to of previous days>
    }'`
  * Documentation link: http://localhost:7001/starling/savings-app/swagger-ui/index.html?url=/v3/api-docs
    (Available after you start the server)
##### Improvements:
* Error/Exception handling is pretty basic and can be made more robust.
* The weekly round up run can be handled using Spring Scheduler after storing refresh token for user on key vault
  (Using Spring cloud vault for abstraction from underline implementation).
* Test coverage can be improved and should add BDD style automation test cases using Cucumber
* Swagger documentation improvement
