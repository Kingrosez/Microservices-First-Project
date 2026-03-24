# Exception Handling Architecture

## Exception Class Hierarchy

```
Throwable
  └── Exception
      └── RuntimeException
          └── BaseException (abstract)
              ├── DuplicateEmailException
              ├── InvalidRoleException
              ├── InvalidEmailException
              ├── InvalidPasswordException
              ├── UserNotFoundException
              ├── UserInactiveException
              ├── ResourceNotFoundException
              ├── BadRequestException
              ├── UnauthorizedException
              ├── ForbiddenException
              ├── ConflictException
              └── InternalServerException
```

## GlobalExceptionHandler Flow

```
                        HTTP Request
                             |
                             v
                      Spring Controller
                             |
                             v
                    Exception Thrown
                             |
              _______________|_______________
             |                               |
             v                               v
    Custom Exception          Spring/General Exception
    (BaseException child)      
             |                               |
             v                               v
     GlobalExceptionHandler
    (@RestControllerAdvice)
             |
      _______|_______________________________________
      |                                               |
      v                                               v
   Custom Handler              Framework/General Handler
   @ExceptionHandler                @ExceptionHandler
             |                               |
             v                               v
       ErrorResponse                   ErrorResponse
      {message, code,              {message, code,
       status, timestamp}          status, timestamp}
             |                               |
             v                               v
          JSON Response                  JSON Response
             |_______________________________|
                        |
                        v
                    HTTP Response
                   (with status code)
```

## Exception Handling Decision Tree

```
                    Exception Caught
                         |
         ____________________|____________________
        |                                         |
        v                                         v
    Is it a Custom        Is it a Spring/
    Exception?            General Exception?
    (BaseException)       
        |                                         |
     YES|NO                                    YES|NO
        v                                         v
   Custom Handler          Spring/General Handler
        |                                         v
        +---> Map to HTTP Status              Map to HTTP Status
        |     - 400 (Bad Request)            - 400 (Bad Request)
        |     - 401 (Unauthorized)          - 401 (Unauthorized)
        |     - 403 (Forbidden)             - 403 (Forbidden)
        |     - 404 (Not Found)             - 404 (Not Found)
        |     - 409 (Conflict)              - 405 (Method Not Allowed)
        |     - 500 (Server Error)          - 409 (Conflict)
        |                                    - 500 (Server Error)
        v
   Create ErrorResponse
   {message, errorCode,
    status, timestamp}
        |
        v
   Return ResponseEntity
   with JSON + HTTP Status
```

## Exception Handling Coverage

```
┌─────────────────────────────────────────────────────────┐
│            EXCEPTION HANDLING COVERAGE                   │
├─────────────────────────────────────────────────────────┤
│ Category              │ Count │ Status Code │ Examples   │
├───────────────────────┼───────┼─────────────┼────────────┤
│ Validation            │ 2     │ 400         │ @Valid     │
│ Custom Business       │ 10    │ Various     │ Duplicate  │
│ Framework             │ 3     │ Various     │ Data Integ │
│ General               │ 3     │ Various     │ NPE        │
├───────────────────────┼───────┼─────────────┼────────────┤
│ TOTAL HANDLERS        │ 20+   │             │            │
└─────────────────────────────────────────────────────────┘
```

## Request-Response Flow Example

```
CLIENT REQUEST
├── POST /api/users/v1
├── Content-Type: application/json
└── Body: {
    "name": "",
    "email": "john@example.com",
    "password": "pass123",
    "role": "INVALID_ROLE"
  }
        |
        v
    VALIDATION
    ├── name: EMPTY (MethodArgumentNotValidException)
    └── role: INVALID (InvalidRoleException)
        |
        v
    GlobalExceptionHandler
    ├── handleValidationExceptions()
    └── handleInvalidRoleException()
        |
        v
    RESPONSE (400 Bad Request)
    {
      "message": "name: Name is required, role: Invalid role 'INVALID_ROLE'...",
      "errorCode": "VALIDATION_ERROR",
      "status": 400,
      "timestamp": "2024-03-24T17:23:52.123456"
    }
```

## Exception Selection Guide

```
Problem Occurs?
    |
    +---> User tries to use duplicate email?
    |     → throw new DuplicateEmailException(email)
    |
    +---> Invalid role provided?
    |     → throw new InvalidRoleException(role)
    |
    +---> Invalid email format?
    |     → throw new InvalidEmailException(email)
    |
    +---> Password doesn't meet requirements?
    |     → throw new InvalidPasswordException()
    |
    +---> User not found by ID?
    |     → throw new UserNotFoundException(id)
    |
    +---> User account is inactive?
    |     → throw new UserInactiveException(id)
    |
    +---> Generic resource not found?
    |     → throw new ResourceNotFoundException(...)
    |
    +---> Bad request data?
    |     → throw new BadRequestException(msg)
    |
    +---> User not authenticated?
    |     → throw new UnauthorizedException()
    |
    +---> User lacks permissions?
    |     → throw new ForbiddenException()
    |
    +---> Resource state conflicts?
    |     → throw new ConflictException(msg)
    |
    +---> Server-side error?
    |     → throw new InternalServerException(msg)
```

## HTTP Status Code Distribution

```
400 - Bad Request
├── ValidationError          (MethodArgumentNotValidException)
├── InvalidRole              (InvalidRoleException)
├── InvalidEmail             (InvalidEmailException)
├── InvalidPassword          (InvalidPasswordException)
├── BadRequest               (BadRequestException)
├── IllegalArgument          (IllegalArgumentException)
└── NullPointer              (NullPointerException)

401 - Unauthorized
└── Unauthorized             (UnauthorizedException)

403 - Forbidden
├── Forbidden                (ForbiddenException)
└── UserInactive             (UserInactiveException)

404 - Not Found
├── UserNotFound             (UserNotFoundException)
├── ResourceNotFound         (ResourceNotFoundException)
└── NoHandlerFound           (NoHandlerFoundException)

405 - Method Not Allowed
└── MethodNotSupported       (HttpRequestMethodNotSupportedException)

409 - Conflict
├── DuplicateEmail           (DuplicateEmailException)
├── Conflict                 (ConflictException)
└── DataIntegrityViolation   (DataIntegrityViolationException)

500 - Internal Server Error
├── InternalServer           (InternalServerException)
└── GeneralException         (Catch-all Exception)
```

## Logging Strategy

```
Exception Occurs
    |
    v
GlobalExceptionHandler
    |
    +---> Log at ERROR level
    |     └─ log.error("...", ex)
    |
    +---> Include context
    |     ├─ Exception message
    |     ├─ Error code
    |     ├─ Stack trace (for general exceptions)
    |     └─ Request context (implicit)
    |
    v
Monitor & Debug
    ├─ Exception logs in application logs
    ├─ Centralized logging (ELK, etc.)
    ├─ Error tracking (Sentry, etc.)
    └─ Metrics & alerts
```

## Files Created Summary

```
Exception Classes (11 new files):
├── DuplicateEmailException.java
├── InvalidRoleException.java
├── InvalidEmailException.java
├── InvalidPasswordException.java
├── ResourceNotFoundException.java
├── BadRequestException.java
├── UnauthorizedException.java
├── ForbiddenException.java
├── ConflictException.java
├── InternalServerException.java
└── GlobalExceptionHandler.java (enhanced - 20+ handlers)

Documentation (2 files):
├── EXCEPTION_HANDLING.md (detailed guide)
└── EXCEPTION_IMPLEMENTATION_SUMMARY.md (quick reference)

Total Files: 13 (11 exception classes + 1 enhanced handler + 1 this diagram)
Total Exception Handlers: 20+
Total Custom Exceptions: 11 (+ 2 pre-existing)
```
