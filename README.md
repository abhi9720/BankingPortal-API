## Banking Portal API

<img width="960" alt="Screenshot 2023-07-23 212110" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/13e15e7b-a8c5-4ba9-99e3-31bed25d8b3c">

***

The Banking Portal API provides a set of endpoints for managing user accounts, fund transfers, and transactions. This project aims to facilitate secure and efficient banking operations for users.

## Features

- User Registration: Users can register by providing their details, such as name, email, address, and phone number.
- PIN Management: Users can create and update their PINs for added security.
- Cash Deposit and Withdrawal: Users can deposit and withdraw cash from their accounts.
- Fund Transfer: Users can transfer funds to other accounts within the system.
- Transaction History: Users can view their transaction history.

## Technologies Used

- Java Spring Boot Framework
- Spring Security for authentication
- JWT (JSON Web Token) for secure API authentication
- PostgreSQL for data storage
- Hibernate for object-relational mapping
- Maven for project management
- Postman for API testing

## Installation and Setup

1. Clone the repository: `git clone https://github.com/yourusername/banking-portal-api.git`
2. Navigate to the project folder: `cd banking-portal-api`
3. Configure PostgreSQL: Set up a PostgreSQL database and update the database credentials in `application.properties`.
4. Build and run the project: `mvn spring-boot:run`

## API Endpoints

- `/api/account/pin/check`: Check if a PIN is created for the user's account.
- `/api/account/pin/create`: Create a new PIN for the user's account.
- `/api/account/pin/update`: Update the existing PIN for the user's account.
- `/api/account/withdraw`: Withdraw cash from the user's account.
- `/api/account/deposit`: Deposit cash into the user's account.
- `/api/account/fund-transfer`: Transfer funds to another user's account.
- `/api/account/transactions`: Get the list of transactions for the user's account.
- `/api/dashboard/user`: Get details of the authenticated user.
- `/api/dashboard/account`: Get details of the user's account.

## Authentication

The API endpoints require bearer token authentication. To access protected endpoints, include a valid access token in the "Authorization" header with the "Bearer" scheme.

## Screenshots
<img width="960" alt="Screenshot 2023-07-23 200531" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/1c3a614b-a87d-4603-9eb8-0a21da6e1ee2">

---

<img width="959" alt="Screenshot 2023-07-23 212415" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/277e50f5-43b6-403d-b336-3431655dfe8a">

---

<img width="960" alt="Screenshot 2023-07-23 212533" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/e8e15160-5bcb-4574-88df-9e300ae5bc59">

---

<img width="960" alt="Screenshot 2023-07-23 200346" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/29b8d193-4298-48ab-9e0d-110e66b186de">

---

<img width="960" alt="Screenshot 2023-07-23 212118" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/2654311c-7af9-4425-adea-36ab709d9c48">

## Error Handling

The API implements global exception handling for common error scenarios, such as account not found, unauthorized access, and insufficient balance.

## Contribution

Contributions to the project are welcome! If you find any issues or have suggestions for improvements, feel free to open an issue or submit a pull request.
