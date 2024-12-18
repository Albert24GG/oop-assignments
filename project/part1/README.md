# Project Assignment POO - J. POO Morgan - Phase One

### Student: Guiman Albert, 325CA

## Project Description

This project implements a modular banking system API, allowing users to execute predefined operations. The design
emphasizes modularity, adhering to the Single Responsibility Principle and utilizing the Service Layer pattern.

The system consists of the following services:

- BankAccountService - manages bank accounts
- UserService - handles user management
- CardService - manages card operations
- TransactionLogService - stores and manages transaction logs

State changes in the system occur exclusively through these services, ensuring consistency and centralizing data
operations.

### `Bank` class

The `Bank` class serves as the primary interface for users. It initializes the services and provides methods for
interacting with the system. It employs the Command pattern, enabling intuitive operation execution and future
extensibility.

### Operation Results

Operation results are encapsulated in `BankOperationResult<T>` objects, containing the operation's status and result
(payload of type `T`) if successful.

### View Objects

Data retrieved from the system is provided as read-only "View" objects, adhering to the Data Transfer Object (DTO)
pattern. This approach:

- Prevents external modifications to system data.
- Decouples internal data structures from their external representations.
- Controls data exposure to clients.

### Decoupled Commands

To separate the system from the test environment (`Main` class), an additional command layer processes operations and
formats results for testing. This layer resides in the command package.

## Project Structure

The banking system resides in the `bank` package, with the `Bank` class being the main class that the user interacts
with.

### bank.type

This package contains all the domain wrapper types used in the system, such as `CardNumber`, `Currency`, `Email`,
`IBAN`. This allows the system to have a strong type system, which ensures that the data is always valid and consistent,
without the need for any additional validation or parsing. Also, this allows the system to have a clear and concise API,
as the user can easily understand what data is expected.

### bank.account

This package contains all user and bank account related classes, such as `UserAccount`, `BankAccount`, `BankAccountType`
etc. and the services responsible for managing them: `UserService` and `BankAccountService`. `BankAccount` is an
abstract class that represents the common behavior of all bank accounts, such as the balance, the currency, the owner of
the account etc. and the concrete implementations are present in the `bank.account.impl` package. These are constructed
using a factory method in the base abstract class, which allows the system to easily add new types of bank accounts in
the future.

### bank.card

Similar to the `bank.account` package, this package contains all card related classes, such as `Card`, `CardType` etc.
The cards are managed by the `CardService` class, which is responsible for creating and managing the cards. `Card`
is the base abstract class common to all concrete card implementations located in the `bank.card.impl` package. They are
also constructed using a factory method in the base abstract class. Currently, the implementations do not have any
additional behavior, but they can be easily extended in the future.

### bank.transaction

This package contains all transaction log related classes, such as `TransactionLog`, `TransactionLogType` etc. The
transaction logs are managed by the `TransactionLogService` class, which is responsible for creating and managing the
transaction logs. The `TransactionLog` class is the base abstract class extended by the concrete implementations in the
`bank.transaction.impl` package. Each concrete implementation has its own fields in order to reduce the amount of null
fields in the logs and thus reduce the memory footprint of the system.

### bank.currency

This package contains the `CurrencyExchangeService` class, which is the service used for storing exchange rates and
converting between currencies. Because we do not have exchange rates between all currencies, the implementation uses a
graph structure to store the rates and calculate the conversion. Ideally, to avoid any floating point errors and to
mimic the real world, we would need a direct mapping between any two currencies.

### bank.report

This package contains report related classes that are returned by the system when the user requests reports, such as
`TransactionsReport` and `SpendingReport`. These classes are an example of simple data classes that are used to
represent the information in a readable format.

### bank.operation

This package contains all the operation related classes, such as `BankOperation`, `BankOperationResult`,
`BankOperationType`,`BankOperationException` etc. The `BankOperation` class is the base abstract class that all concrete
operations extend. It contains a final method `execute` that first validates the operation by querying
`BankOperationRegistry`, and then proceeds to execute the operation by calling `internalExecute`. This method is
implemented by the concrete operations and contains the actual logic of the operation. The `BankOperationResult` class
is a wrapper class that contains the status of the operation and the result of the operation if it was successful. The
`BankOperationException` is a custom exception that is thrown when an operation fails, and it is caught by the
dispatcher method from the `Bank` class.

`BankOperationRegistry` is a singleton class that is responsible for registering all the available operations in the
system. This class was created for security reasons, as it allows the system to validate the operations before executing
them. This way, the system can ensure that the user cannot execute any unauthorized operations.

All the available operations are located in the `bank.operation.impl` package. They make use of the
`BankOperationContext` which encapsulates the services required to execute the operation. This makes the services act
like puzzle pieces that can be easily combined to create new operations.

## Workflow

The user starts by creating a new `Bank` object, which initializes the services and registers the operations. Then the
user must construct the operation object with the required data and then call the `processOperation` method on the
`Bank`, or simply call `processBy` method with the bank object parameter. The operation is then validated by the
`BankOperationRegistry` and executed. The result of the operation is then returned to the user.

## Testing

For our testing environment, we follow the same workflow as the user, but with the extra step of creating a command
object using the `CommandFactory` class. Each command object has a `execute` method that processes the operation and
returns the result in the specified format. The commands are then executed by the `Main` class with the bank object as a
parameter. The result is a `CommandOutput` object that contains the output of the command which can be converted to JSON
using the `toJson` method.

Mention: Given the inconsistency of the output required by the tests, in the `CommandFactory` class I did not find a way
to create only a few Command-derived classes that can be used by all commands, specifically "deleteAccount" and report
commands needed specific output handling.