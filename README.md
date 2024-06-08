# Banking Portal Rest API Using Spring Boot & Spring Security
### Fork and Star ⭐ Github Repo For New Feature Update


<div style="display: flex;">
    <!-- GitHub Repository Badges -->
    <a href="https://github.com/abhi9720/BankingPortal-UI" target="_blank">
      <img alt="GitHub Repo" src="https://img.shields.io/badge/GitHub-UI%20Repo-blue.svg?style=flat-square">
    </a>
    <a href="https://github.com/abhi9720/BankingPortal-API" target="_blank">
      <img alt="GitHub Repo" src="https://img.shields.io/badge/GitHub-API%20Repo-blue.svg?style=flat-square">
    </a>
   <a href="https://github.com/abhi9720/BankingPortal-APIe" target="_blank">
      <img alt="View" src="https://img.shields.io/badge/View-blue.svg?style=flat-square">
    </a>
  </div>

  ## API Documentation
- https://github.com/abhi9720/BankingPortal-API/wiki

## Banking Portal UI
- https://github.com/abhi9720/BankingPortal-UI
  
<img width="948" alt="image" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/237694d9-6e8d-48e8-a7a2-982b9f8ca671">



***

The Banking Portal API provides a set of endpoints for managing user accounts, fund transfers, and transactions. This project aims to facilitate secure and efficient banking operations for users.

## Features

- User Registration: Users can register by providing their details, such as name, email, address, and phone number.
- PIN Management: Users can create and update their PINs for added security.
- Cash Deposit and Withdrawal: Users can deposit and withdraw cash from their accounts.
- Fund Transfer: Users can transfer funds to other accounts within the system.
- Transaction History: Users can view their transaction history.

## Technologies Used

<img src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/31896d20-16d9-4fe1-a534-0490841de4b9" alt="image" height="100">
<img src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/c09bc4ac-c0ca-4f7c-9c6e-8eb9818eb35b" alt="image" height="100">
<img src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/78c75fff-e8a8-49c6-9897-34b08b2c9308" alt="image" height="100">
<img src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/3647613e-1d6e-4bc4-98b6-2da5648659f9" alt="image" height="100">
<img src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/8a5c0b00-776b-444e-bc24-36fc6bfe4c41" alt="image" height="50">
<img src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/b56a7167-6a3a-49a0-8b8a-8a4e3e71a383" alt="image" height="70">
<img src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/b5c86e65-cbe8-400a-afeb-895846601da7" alt="image"  height="100">

<!--
- Java Spring Boot Framework
- Spring Security for authentication
- JWT (JSON Web Token) for secure API authentication
- MySQL for data storage
- Hibernate for object-relational mapping
- Maven for project management
- Postman for API testing
-->

## TODO
- UI Fix for Dashboard Charts
- Pagination in table
- Save JWT Token in db and remove on logout
- Email trigger on account login
- Send Bank Statement on Email
  
## Installation and Setup

1. Clone the repository: `git clone https://github.com/yourusername/banking-portal-api.git`
2. Navigate to the project folder: `cd banking-portal-api`
3. Configure MySQL: Set up a MySQL database and update the database credentials in `application.properties`.
4. Build and run the project: `mvn spring-boot:run`

## Screenshots
![project](https://github.com/abhi9720/BankingPortal-API/assets/68281476/45bca1e0-0af2-4d63-a8d0-efd7b67df6bf)


<!---
<img width="960" alt="Screenshot 2023-07-23 200531" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/1c3a614b-a87d-4603-9eb8-0a21da6e1ee2">

---

<img width="959" alt="Screenshot 2023-07-23 212415" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/277e50f5-43b6-403d-b336-3431655dfe8a">

---

<img width="960" alt="Screenshot 2023-07-23 212110" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/13e15e7b-a8c5-4ba9-99e3-31bed25d8b3c">

---

<img width="960" alt="Screenshot 2023-07-23 212533" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/e8e15160-5bcb-4574-88df-9e300ae5bc59">

---

<img width="960" alt="Screenshot 2023-07-23 200346" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/29b8d193-4298-48ab-9e0d-110e66b186de">

---

<img width="960" alt="Screenshot 2023-07-23 212118" src="https://github.com/abhi9720/BankingPortal-API/assets/68281476/2654311c-7af9-4425-adea-36ab709d9c48">
--->
## Error Handling

The API implements global exception handling for common error scenarios, such as account not found, unauthorized access, and insufficient balance.

## How to Contribute

We welcome and encourage developers to contribute to the project and help us make it even better. If you are interested in contributing, follow these steps:

👉🏻**Fork the Repository**: Click on the "Fork" button on the top right corner of the GitHub repository page. This will create a copy of the repository in your GitHub account.

👉🏻**Clone the Forked Repository**: Open your terminal or command prompt and use the following command to clone the repository to your local machine:
   ```
   git clone https://github.com/your-username/BankingPortal-API.git
   ```
   Replace `your-username` with your GitHub username.

👉🏻**Create a New Branch**: Move into the project directory using `cd BankingPortal-API` and create a new branch for your changes:
   ```
   git checkout -b feature/your-new-feature
   ```
   Replace `your-new-feature` with a descriptive name for your contribution.

👉🏻**Make Changes**: Now, make the desired changes to the codebase using your favorite code editor.

👉🏻**Commit Changes**: After making the changes, save your work and commit the changes with a meaningful commit message:
   ```
   git add .
   git commit -m "Add your commit message here"
   ```

👉🏻**Push Changes**: Push your changes to your forked repository:
   ```
   git push origin feature/your-new-feature
   ```

👉🏻**Create a Pull Request**: Go to your forked repository on GitHub, and you'll see a "Compare & Pull Request" button. Click on it to create a new pull request.

👉🏻**Wait for Review**: Your pull request will be reviewed by the project maintainers. Make any necessary changes based on their feedback.

**👏🏻👏🏻 Congratulations! 🎉🎊** Your contribution has been accepted and merged into the main repository. You are now a contributor to the project.
