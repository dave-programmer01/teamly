# Teamly API Documentation

Teamly is a backend application designed for team management, allowing users to create, join, and manage teams. This documentation provides a comprehensive guide to using the API, including authentication, user profile management, and team operations.

## Table of Contents
- [Technologies Used](#technologies-used)
- [Authentication](#authentication)
- [User Endpoints](#user-endpoints)
- [Team Endpoints](#team-endpoints)
- [Models](#models)

---

## Technologies Used
- **Java 17** & **Spring Boot 3**
- **Spring Security** with **JWT** for stateless authentication
- **Spring Data JPA** with **Hibernate** for database management
- **ModelMapper** for entity-to-DTO conversion
- **Cloudinary** for image upload and storage
- **Lombok** for reducing boilerplate code

---

## Authentication
Teamly uses JSON Web Tokens (JWT) for authentication. Most endpoints require an `Authorization` header in the format `Bearer <token>`.

### Register a User
- **Method:** `POST`
- **URL:** `/api/auth/register`
- **Payload:**
    ```json
    {
      "username": "spalobolo",
      "email": "user@example.com",
      "password": "yourpassword"
    }
    ```
- **Response:** Returns a JWT token.

### Authenticate a User
- **Method:** `POST`
- **URL:** `/api/auth/authenticate`
- **Payload:**
    ```json
    {
      "username": "spalobolo",
      "password": "yourpassword"
    }
    ```
- **Response:** Returns a JWT token.

---

## User Endpoints

### Update Profile Picture
- **Method:** `PUT`
- **URL:** `/api/users/profile-picture`
- **Authentication Required:** Yes
- **Form-Data:**
    - `file`: The image file to upload.
- **Response:** Returns the URL of the uploaded image on Cloudinary.

---

## Team Endpoints

### Get All Teams
- **Method:** `GET`
- **URL:** `/api/teams`
- **Authentication Required:** No (Configured in `SecurityConfig.java`)
- **Response:** A list of all teams.

### Create a Team
- **Method:** `POST`
- **URL:** `/api/teams`
- **Authentication Required:** Yes
- **Payload:**
    ```json
    {
      "teamName": "Alpha Squad",
      "teamDescription": "High-performance dev team"
    }
    ```
- **Constraint:** The authenticated user will automatically become the **Team Owner**.
- **Response:** Details of the created team.

### Update a Team
- **Method:** `PUT`
- **URL:** `/api/teams/{id}`
- **Authentication Required:** Yes
- **Constraint:** Only the **Team Owner** can update the team.
- **Payload:** Same as `POST`.
- **Response:** Details of the updated team.

### Delete a Team
- **Method:** `DELETE`
- **URL:** `/api/teams/{id}`
- **Authentication Required:** Yes
- **Constraint:** Only the **Team Owner** can delete the team.
- **Response:** `204 No Content`.

---

## Models

### User
- Each user can belong to **at most one team**.
- A user can be the **leader of exactly one team**.

### Team
- **Team Owner:** The user who created the team.
- **Members:** A list of users associated with the team.

---

## Error Handling
The API returns standard HTTP status codes:
- `200 OK` / `201 Created`: Success
- `400 Bad Request`: Validation failure
- `401 Unauthorized`: Missing or invalid JWT token
- `403 Forbidden`: User is not the owner of the team
- `404 Not Found`: Team or User not found
-