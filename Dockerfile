FROM openjdk:8
ADD target/todolist.jar todolist.jar
ENTRYPOINT ["java","-jar","/todolist.jar"]