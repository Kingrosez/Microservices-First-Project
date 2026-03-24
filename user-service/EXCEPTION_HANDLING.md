# Exception Handling Documentation

## Overview
This document provides a comprehensive guide to the exception handling mechanism in the User Service microservice.

## Custom Exception Classes

All custom exceptions extend from `BaseException`, which provides a standardized error code and message format.

### 1. **BaseException** (Abstract Base Class)
```java
public abstract class BaseException extends RuntimeException {
    private final String errorCode;
    // Contains error code and message
}
```

### 2. **DuplicateEmailException**
- **HTTP Status**: 409 (CONFLICT)
- **Error Code**: `DUPLICATE_EMAIL`
- **Usage**: When attempting to create/update a user with an email that already exists
- **Example**: 
```java
throw new DuplicateEmailException("john@example.com");
// Message: User with email 'john@example.com' already exists
```

### 3. **InvalidRoleException**
- **HTTP Status**: 400 (BAD_REQUEST)
- **Error Code**: `INVALID_ROLE`
- **Usage**: When an invalid role is provided (not USER or ADMIN)
- **Example**:
```java
throw new InvalidRoleException("SUPERUSER");
// Message: Invalid role 'SUPERUSER'. Allowed roles are: USER, ADMIN
```

### 4. **InvalidEmailException**
- **HTTP Status**: 400 (BAD_REQUEST)
- **Error Code**: `INVALID_EMAIL`
- **Usage**: When email format is invalid
- **Example**:
```java
throw new InvalidEmailException("invalid-email");
// Message: Email 'invalid-email' is invalid
```

### 5. **InvalidPasswordException**
- **HTTP Status**: 400 (BAD_REQUEST)
- **Error Code**: `INVALID_PASSWORD`
- **Usage**: When password doesn't meet requirements
- **Default Message**: "Password must be at least 8 characters long and contain alphanumeric characters"
- **Example**:
```java
throw new InvalidPasswordException();
// or with custom message
throw new InvalidPasswordException("Password too weak");
```

### 6. **UserNotFoundException**
- **HTTP Status**: 404 (NOT_FOUND)
- **Error Code**: `USER_NOT_FOUND`
- **Usage**: When a user doesn't exist
- **Example**:
```java
throw new UserNotFoundException(123L);
// Message: User with id 123 not found
```

### 7. **UserInactiveException**
- **HTTP Status**: 403 (FORBIDDEN)
- **Error Code**: `USER_INACTIVE`
- **Usage**: When accessing an inactive user
- **Example**:
```java
throw new UserInactiveException(456L);
// Message: User with id 456 inactive
```

### 8. **ResourceNotFoundException**
- **HTTP Status**: 404 (NOT_FOUND)
- **Error Code**: `RESOURCE_NOT_FOUND`
- **Usage**: Generic resource not found exception
- **Example**:
```java
throw new ResourceNotFoundException("User", "id", 789);
// Message: User not found with id: 789
// OR
throw new ResourceNotFoundException("User not found");
```

### 9. **BadRequestException**
- **HTTP Status**: 400 (BAD_REQUEST)
- **Error Code**: `BAD_REQUEST`
- **Usage**: For invalid request data
- **Example**:
```java
throw new BadRequestException("Invalid input data");
```

### 10. **UnauthorizedException**
- **HTTP Status**: 401 (UNAUTHORIZED)
- **Error Code**: `UNAUTHORIZED`
- **Usage**: When user is not authenticated
- **Example**:
```java
throw new UnauthorizedException();
// Default: "User is not authorized to perform this action"
// or with custom message
throw new UnauthorizedException("Invalid credentials");
```

### 11. **ForbiddenException**
- **HTTP Status**: 403 (FORBIDDEN)
- **Error Code**: `FORBIDDEN`
- **Usage**: When user lacks required permissions
- **Example**:
```java
throw new ForbiddenException();
// Default: "Access to this resource is forbidden"
// or with custom message
throw new ForbiddenException("Admin access required");
```

### 12. **ConflictException**
- **HTTP Status**: 409 (CONFLICT)
- **Error Code**: `CONFLICT`
- **Usage**: When resource state conflicts with request
- **Example**:
```java
throw new ConflictException("Resource already exists");
```

### 13. **InternalServerException**
- **HTTP Status**: 500 (INTERNAL_SERVER_ERROR)
- **Error Code**: `INTERNAL_SERVER_ERROR`
- **Usage**: For server-side errors
- **Example**:
```java
throw new InternalServerException("Database connection failed");
// or with cause
throw new InternalServerException("Database error", cause);
```

## Global Exception Handler

The `GlobalExceptionHandler` is a centralized error handling mechanism using Spring's `@RestControllerAdvice`.

### Exception Handler Methods

#### Validation Exceptions
1. **handleValidationExceptions** - `MethodArgumentNotValidException`
   - Returns field-level validation errors
   - Status: 400 (BAD_REQUEST)
   - Error Code: `VALIDATION_ERROR`

2. **handleHttpMessageNotReadableException** - `HttpMessageNotReadableException`
   - Returns when request JSON/XML is malformed
   - Status: 400 (BAD_REQUEST)
   - Error Code: `INVALID_REQUEST_FORMAT`

#### Custom Exception Handlers
- **handleDuplicateEmailException** - Status: 409 (CONFLICT)
- **handleUserNotFoundException** - Status: 404 (NOT_FOUND)
- **handleUserInactiveException** - Status: 403 (FORBIDDEN)
- **handleInvalidRoleException** - Status: 400 (BAD_REQUEST)
- **handleInvalidEmailException** - Status: 400 (BAD_REQUEST)
- **handleInvalidPasswordException** - Status: 400 (BAD_REQUEST)
- **handleResourceNotFoundException** - Status: 404 (NOT_FOUND)
- **handleBadRequestException** - Status: 400 (BAD_REQUEST)
- **handleUnauthorizedException** - Status: 401 (UNAUTHORIZED)
- **handleForbiddenException** - Status: 403 (FORBIDDEN)
- **handleConflictException** - Status: 409 (CONFLICT)
- **handleInternalServerException** - Status: 500 (INTERNAL_SERVER_ERROR)
- **handleBaseException** - Status: 400 (BAD_REQUEST)

#### Spring Framework Exceptions
1. **handleDataIntegrityViolationException** - `DataIntegrityViolationException`
   - Handles database constraint violations
   - Status: 409 (CONFLICT)
   - Error Code: `DATA_INTEGRITY_VIOLATION`

2. **handleHttpRequestMethodNotSupportedException** - `HttpRequestMethodNotSupportedException`
   - Handles unsupported HTTP methods
   - Status: 405 (METHOD_NOT_ALLOWED)
   - Error Code: `METHOD_NOT_ALLOWED`

3. **handleNoHandlerFoundException** - `NoHandlerFoundException`
   - Handles non-existent endpoints
   - Status: 404 (NOT_FOUND)
   - Error Code: `NOT_FOUND`

#### General Exception Handlers
1. **handleIllegalArgumentException** - `IllegalArgumentException`
   - Status: 400 (BAD_REQUEST)
   - Error Code: `ILLEGAL_ARGUMENT`

2. **handleNullPointerException** - `NullPointerException`
   - Status: 400 (BAD_REQUEST)
   - Error Code: `NULL_POINTER`

3. **handleGeneralException** - `Exception` (Catch-all)
   - Status: 500 (INTERNAL_SERVER_ERROR)
   - Error Code: `INTERNAL_SERVER_ERROR`

## Error Response Format

All exceptions return a standardized error response:

```json
{
  "message": "Detailed error message",
  "errorCode": "ERROR_CODE",
  "status": 400,
  "timestamp": "2024-03-24T17:21:01.123456"
}
```

### Example Responses

**Validation Error (400)**
```json
{
  "message": "name: Name is required, email: Invalid email format",
  "errorCode": "VALIDATION_ERROR",
  "status": 400,
  "timestamp": "2024-03-24T17:21:01.123456"
}
```

**Duplicate Email Error (409)**
```json
{
  "message": "User with email 'john@example.com' already exists",
  "errorCode": "DUPLICATE_EMAIL",
  "status": 409,
  "timestamp": "2024-03-24T17:21:01.123456"
}
```

**User Not Found (404)**
```json
{
  "message": "User with id 123 not found",
  "errorCode": "USER_NOT_FOUND",
  "status": 404,
  "timestamp": "2024-03-24T17:21:01.123456"
}
```

**Unauthorized (401)**
```json
{
  "message": "Invalid credentials",
  "errorCode": "UNAUTHORIZED",
  "status": 401,
  "timestamp": "2024-03-24T17:21:01.123456"
}
```

## Usage Examples

### In Service Layer
```java
@Service
public class UserService {
    public UserResponse createUser(CreateUserRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        
        // Validate role
        try {
            Role.valueOf(request.role());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException(request.role());
        }
        
        // ... rest of logic
    }
}
```

### In Controller Layer
```java
@RestController
@RequestMapping("/api/users/v1")
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        // @Valid annotation triggers validation
        // GlobalExceptionHandler catches MethodArgumentNotValidException
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

## Logging

All exceptions are logged at the ERROR level with appropriate context:
- Exception type and message
- Stack trace (for general exceptions)
- Request details (implicit in Spring logging)

## Best Practices

1. **Throw appropriate exceptions**: Use the most specific exception class
2. **Provide descriptive messages**: Include relevant details (user ID, email, etc.)
3. **HTTP Status Codes**: Follow REST conventions
4. **Consistent error codes**: Use predefined error codes
5. **Log all errors**: Enables debugging and monitoring
6. **Don't expose sensitive information**: Keep error messages user-friendly

## HTTP Status Code Mapping

| Status | Code | Exceptions |
|--------|------|-----------|
| 400 | BAD_REQUEST | ValidationError, InvalidRole, InvalidEmail, InvalidPassword, etc. |
| 401 | UNAUTHORIZED | UnauthorizedException |
| 403 | FORBIDDEN | ForbiddenException, UserInactiveException |
| 404 | NOT_FOUND | UserNotFoundException, ResourceNotFoundException |
| 405 | METHOD_NOT_ALLOWED | HttpRequestMethodNotSupported |
| 409 | CONFLICT | DuplicateEmailException, ConflictException, DataIntegrityViolation |
| 500 | INTERNAL_SERVER_ERROR | InternalServerException, General exceptions |

## Testing Exception Handling

Example test for exception handling:
```java
@Test
void testDuplicateEmailException() {
    DuplicateEmailException exception = 
        new DuplicateEmailException("test@example.com");
    
    assertEquals("User with email 'test@example.com' already exists", 
                 exception.getMessage());
    assertEquals("DUPLICATE_EMAIL", exception.getErrorCode());
}
```

## Future Enhancements

- Add Rate Limiting exception
- Add Timeout exception
- Add Service Unavailable exception
- Add custom exception interceptors
- Add distributed tracing support
