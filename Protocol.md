# Media Ratings Platform (MRP)

**Student:** Ajsi Sala  
**Course:** Software Engineering

## 1. Project Description

The Media Ratings Platform (MRP) is a Java application that allows users to manage media entries and create ratings. The application provides a REST API that communicates with a PostgreSQL database.

## 2. Technologies Used

- Java 17
- Maven
- PostgreSQL
- Docker
- JDBC
- Jackson for JSON processing
- jBCrypt for password hashing
- Postman for API testing

## 3. Project Structure

The project is divided into different classes with separate responsibilities.

### Main.java
The main class starts the application by calling the HTTP server.

### MediaHttpServer.java
Handles incoming HTTP requests and sends HTTP responses. It manages the API routes, request data and authentication.

### DatabaseConnection.java
Establishes the connection between the Java application and the PostgreSQL database.

### Repository classes
- `MediaRepository.java` handles media database operations.
- `UserRepository.java` handles user registration and login.
- `RatingRepository.java` handles rating database operations.

### Model classes
- `User.java` represents a user.
- `MediaEntry.java` is the abstract base class for media entries.
- `Movie.java` represents a movie.
- `Series.java` represents a series.
- `Rating.java` represents a rating.
- `Rateable.java` defines the rating information method.

## 4. Technical Decisions

### Java and HTTP Server
Java was selected as the programming language. The application uses Java's built-in HTTP server instead of a web framework.

### Database
PostgreSQL is used to store users, media entries and ratings. The database runs inside a Docker container.

### Database Access
JDBC is used to communicate with PostgreSQL. Prepared statements are used for database queries to prevent SQL injection.

### Password Security
Passwords are hashed using BCrypt before they are stored in the database.

### Authentication
After a successful login, the server creates an authorization token. The token is used for protected API requests.

### JSON
Jackson is used to process JSON request bodies and responses.

## 5. Object-Oriented Programming

The project uses several object-oriented programming concepts:

- **Encapsulation:** Fields are private and accessed through methods.
- **Inheritance:** Movie and Series extend MediaEntry.
- **Abstraction:** MediaEntry is an abstract class.
- **Interfaces:** Rateable defines the rateInfo() method.
- **Polymorphism:** Movie and Series implement their own methods.

## 6. API Testing

Postman was used to test the REST API.

The collection includes requests for:
- Retrieving media entries
- Creating media entries
- Updating media entries
- Deleting media entries
- User registration and login
- Retrieving and creating ratings
- Deleting ratings

The exported Postman collection is included with the project documentation.

## 7. GitHub Repository

Project repository:

https://github.com/ajsi-s/project_swe.git
