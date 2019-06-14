# paysys-app

This is the simple example of payment system. Restful Web Services - Jersey 2.26 (with Grizzly 2 HTTP server container). Repositories for data are ConcurrentHashMap in a memory.<br/>
Response's media type is application/json.<br/>
Used jdk 1.8 (1.8.0.112).

## Build from source files
1. gradlew build<br/>
Builds jar and runs tests. Other jar files are placed to the folder.
If necessary, specify org.gradle.java.home to jdk.<br/>
Example: `gradlew build -Dorg.gradle.java.home=\Java\jdk1.8.0_112`

2. gradlew shadowJar<br/>
Builds one jar with all dependencies.
If necessary, specify org.gradle.java.home to jdk.<br/> 
Example: `gradlew shadowJar -Dorg.gradle.java.home=\fromk\Java\jdk1.8.0_112`

## Run
`java -Dfile.encoding=UTF-8 -jar paysys-app-1.0-SNAPSHOT-all.jar`<br/>
Run witn your logback settings<br/>
`java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=./logback.xml -jar paysys-app-1.0-SNAPSHOT-all.jar`

## Settings
There are setting in application.propperties file. Parameters:
- `base.uri`. Uri for web service
- `operation.timeout`. Timeout of an asynchronous operations (in seconds): money's adding and transfer money from one account to another. 

## API methods:
- `<base.uri>/accounts` [POST]. 
Parameters: `String email` - account's email (required).<br/>
Creates account by email.<br/>
Returns created account.<br/>
Example of call for curl: `curl.exe -d "email=address@gmail.com" "http://localhost:2222/accounts"`

- `<base.uri>/accounts/<id>` [GET]. 
Parameters: `Long id` - account's id (required).<br/>
Gets account by id.<br/>
Returns found account.<br/>
Example of call for curl: `curl.exe "http://localhost:2222/accounts/1"`

- `<base.uri>/accounts/<id>/operations/transfer` [POST]. 
Parameters: `Long id` - account's id (required), `BigDecimal sum` - operation's sum (required, must be a positive), `Long receiverId` - receiver account id (required).<br/>
Creates operation for transfer money from one account to another and asynchronously handles it.<br/>
Returns created operation.<br/>
Example of call for curl: `curl.exe -d "sum=10.2&receiverId=2" "http://localhost:2222/accounts/1/operations/transfer"`

- `<base.uri>/accounts/<id>/operations/addmoney` [POST]. 
Parameters: `Long id` - account's id (required), `BigDecimal sum` - operation's sum (required, must be a positive).<br/>
Creates operation for money's adding to account and asynchronously handles it.<br/>
Returns created operation.<br/>
Example of call for curl: `curl.exe -d "sum=10.2" "http://localhost:2222/accounts/1/operations/addmoney"`
