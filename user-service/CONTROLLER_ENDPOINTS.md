# User Service Controller Endpoints

This document lists all the RESTful API endpoints available in the User Service.

## Base URL
```
/api/users/v1
```

## Endpoints Overview

### 1. CREATE Operations
- **POST** `/` - Create a new user
  - Request: `CreateUserRequest` (JSON body)
  - Response: `UserResponse` with HTTP 201 Created

### 2. READ Operations

#### Get By ID/Email
- **GET** `/{id}` - Get user by ID
  - Path Variable: `id` (Long)
  - Response: `UserResponse`

- **GET** `/email/{email}` - Get user by email
  - Path Variable: `email` (String)
  - Response: `UserResponse`

#### Get All Users
- **GET** - Get all users with pagination and sorting
  - Query Parameters:
    - `page` (default: 0) - Page number (0-indexed)
    - `size` (default: 10) - Page size
    - `sortBy` (default: "id") - Field to sort by
    - `sortOrder` (default: "ASC") - Sort order (ASC or DESC)
  - Response: `Page<UserResponse>`

#### Get by Status
- **GET** `/active/all` - Get all active users
  - Response: `List<UserResponse>`

- **GET** `/inactive/all` - Get all inactive users
  - Response: `List<UserResponse>`

#### Get by Role
- **GET** `/role/{role}` - Get users by role
  - Path Variable: `role` (String: USER or ADMIN)
  - Response: `List<UserResponse>`

#### Check Existence
- **GET** `/exists/email/{email}` - Check if user exists by email
  - Path Variable: `email` (String)
  - Response: `Boolean`

#### Count Operations
- **GET** `/count` - Get total user count
  - Response: `Long`

- **GET** `/count/role/{role}` - Get user count by role
  - Path Variable: `role` (String)
  - Response: `Long`

- **GET** `/count/active` - Get active user count
  - Response: `Long`

- **GET** `/count/inactive` - Get inactive user count
  - Response: `Long`

### 3. UPDATE Operations

#### Update User Fields
- **PUT** `/{id}/name` - Update user name
  - Path Variable: `id` (Long)
  - Query Parameter: `name` (String)
  - Response: `UserResponse`

- **PUT** `/{id}/email` - Update user email
  - Path Variable: `id` (Long)
  - Query Parameter: `email` (String)
  - Response: `UserResponse`

- **PUT** `/{id}/role` - Update user role
  - Path Variable: `id` (Long)
  - Query Parameter: `role` (String: USER or ADMIN)
  - Response: `UserResponse`

- **PUT** `/{id}/password` - Update user password
  - Path Variable: `id` (Long)
  - Query Parameter: `newPassword` (String)
  - Response: `String` (Success message)

#### Activate/Deactivate
- **PUT** `/{id}/activate` - Activate user
  - Path Variable: `id` (Long)
  - Response: `UserResponse`

- **PUT** `/{id}/deactivate` - Deactivate user
  - Path Variable: `id` (Long)
  - Response: `UserResponse`

### 4. DELETE Operations

#### Delete Single User
- **DELETE** `/{id}` - Delete user by ID
  - Path Variable: `id` (Long)
  - Response: `String` (Success message)

- **DELETE** `/email/{email}` - Delete user by email
  - Path Variable: `email` (String)
  - Response: `String` (Success message)

#### Delete Multiple
- **DELETE** `/inactive/all` - Delete all inactive users
  - Response: `Long` (Count of deleted users)

### 5. SEARCH & FILTER Operations

- **GET** `/search/name` - Search users by name (partial match)
  - Query Parameter: `name` (String)
  - Response: `List<UserResponse>`

### 6. BULK Operations

- **PUT** `/bulk/activate` - Activate multiple users
  - Request Body: `List<Long>` (User IDs)
  - Response: `Long` (Count of activated users)

- **PUT** `/bulk/deactivate` - Deactivate multiple users
  - Request Body: `List<Long>` (User IDs)
  - Response: `Long` (Count of deactivated users)

- **DELETE** `/bulk` - Delete multiple users
  - Request Body: `List<Long>` (User IDs)
  - Response: `Long` (Count of deleted users)

### 7. HEALTH CHECK

- **GET** `/health` - Health check endpoint
  - Response: `String` (Health status message)

## Summary

| Method | Count |
|--------|-------|
| GET | 17 |
| POST | 1 |
| PUT | 10 |
| DELETE | 4 |
| **Total** | **32** |

## Request/Response DTOs

### CreateUserRequest
```json
{
  "name": "String",
  "email": "String",
  "password": "String",
  "role": "String (USER or ADMIN)"
}
```

### UserResponse
```json
{
  "id": "Long",
  "name": "String",
  "email": "String",
  "role": "String",
  "active": "Boolean",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

## Response Status Codes

- **200 OK** - Successful GET, PUT, or DELETE operation
- **201 Created** - Successful user creation (POST)
- **400 Bad Request** - Invalid input
- **404 Not Found** - User not found
- **409 Conflict** - Duplicate email or other conflict
- **500 Internal Server Error** - Server error

## Exception Handling

Common exceptions:
- `UserNotFoundException` - User not found (404)
- `DuplicateEmailException` - Email already exists (409)
- `InvalidRoleException` - Invalid role provided (400)
- `InvalidPasswordException` - Invalid password (400)
- `ResourceNotFoundException` - Resource not found (404)

## Service Features

✅ Full CRUD Operations
✅ Pagination and Sorting
✅ Search and Filter
✅ Bulk Operations
✅ Active/Inactive User Management
✅ Role-based Operations
✅ Email Existence Check
✅ User Count Statistics
✅ Password Encryption
✅ Comprehensive Logging
✅ Transaction Management

