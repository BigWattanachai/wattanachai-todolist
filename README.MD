#TODO LINE BOT Application

This is a TODO LINE BOT for helping people manage their todo list.

#Deploy the app on Heroku

#Deploy using git
1. $ git clone https://git.heroku.com/wattanachai-todolist.git

2. $ git remote set-url origin git: <your git url>

3. $ git add .

4. $ git commit -m "initial TODO LINE BOT"

5. $ heroku create

6. $ git push heroku master

#deploying without git

1. $ heroku login

2. $ heroku create <TODO LINE BOT NAME> --no-remote

4. $ heroku config:set TODO_LINE_BOT_HOST=<TODO LINE BOT NAME>.herokuapp.com


#deploying without git

1. $ heroku plugins:install heroku-cli-deploy

2. $ ./gradlew clean build

3. $ heroku deploy:jar build/libs/wattanachai-todolist-0.0.1-SNAPSHOT.jar --app <TODO LINE BOT NAME>


#Prerequisite for running the app
 - Install Lombok Plugin  [Lombok](https://projectlombok.org/download.html) 
 
 

# Running locally 
1. Clone the project:
   $ git clone https://git.heroku.com/wattanachai-todolist.git

2. Open project at your favorite IDE
   File -> New -> Project from Exisiting Sources... then navigate to the project and select POM.xml and Click OK

4. Start Application:
   $ ./gradlew clean bootRun
  
5. Compile everything and run all tests with the overage report:
   $ ./gradlew clean test jacocoTestReport
  
6. Browse to [http://localhost:8080/info](http://localhost:8080/info) 


#Built With
[Spring Boot](http://projects.spring.io/spring-boot/) - Web Framework

[Maven](https://maven.apache.org/) - Dependency Management