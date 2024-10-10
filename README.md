# Banking Portal API fork

This is a fork of the repository [abhi9720/BankingPortal-API](https://github.com/abhi9720/BankingPortal-API?tab=MIT-1-ov-file#readme)

## Add-ons

### docker-compose

Ensure Docker Compose is installed on your machine by running the command below. If not, download and install
from [Docker's official site](https://www.docker.com/get-started).

   ```bash
   docker compose
   ```
If docker compose is installed, you'll see a list of docker compose commands. If not, you'll receive a "command not found" error.

### MySQL

Before running the project or the tests, we need a MySQL server with a database named `bankingapp`.
To start it, run the docker-compose file from the `database` folder

    ```bash
    docker compose up
    ```

### Jacoco

Jacoco is a code coverage tool. We will use it to generate a report of the code coverage of the tests.
To run the tests and generate a report run the following command:

```bash
mvn clean test jacoco:report
```

The report will be generated in the `target/site/jacoco` folder.