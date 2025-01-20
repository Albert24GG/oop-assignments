# Project Assignment POO - J. POO Morgan - Phase Two

### Student: Guiman Albert, 325CA

## Project Description

This project implements a modular banking system API, allowing users to execute predefined operations. The design
emphasizes modularity, adhering to the Single Responsibility Principle and utilizing the Service Layer pattern.

The system consists of the following services:

- BankAccountService - manages bank accounts
- UserService - handles user management
- CardService - manages card operations
- AuditLogService - stores and manages logs of system operations
- CurrencyExchangeService - stores exchange rates and performs currency conversions
- MerchantService - manages merchants and operations related to them
- SplitPaymentService - handles split payments between users
- BankEventService - the event dispatcher for the system

State changes in the system occur exclusively through these services, ensuring consistency and centralizing data
operations.

### `Bank` class

The `Bank` class serves as the primary interface for users. It initializes the services, and provides methods for
interacting with the system. It registers the event handlers for the available events, namely `SplitPaymentEvent` and
`TransactionEvent`. It employs the Command pattern, enabling intuitive operation execution and future extensibility.

### Operation Results

Operation results are encapsulated in `BankOperationResult<T>` objects, containing the operation's status and result
(payload of type `T`) if successful.

### View Objects

Data retrieved from the system is provided as read-only "view" objects, adhering to the Data Transfer Object (DTO)
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

This package defines wrapper types (e.g., `CardNumber`, `Currency`, `IBAN`) to ensure data validity and consistency,
providing a strong type system for a clear and concise API.

### bank.account

This package contains all user and bank account related classes, such as `UserAccount`, `BankAccount`, `BankAccountType`
etc. and the services responsible for managing them: `UserService` and `BankAccountService`. `BankAccount` is an
abstract class that represents the common behavior of all bank accounts, such as the balance, the currency, the owner of
the account etc. and the concrete implementations are present in the `bank.account.impl` package. These are constructed
using a factory method in the base abstract class, which allows the system to easily add new types of bank accounts in
the future.

The `BusinessAccount` class is a special type of bank account that is used for businesses. Its main differentiator is
the fact that it can have multiple users associated, whose roles are controlled via a permissions' system.
The permissions system is role-based (RBAC), where each `BusinessAccountRole` (e.g., OWNER, MANAGER, EMPLOYEE) has
predefined permissions and limits. For each `BusinessAccount` instance, these roles are associated with
`AccountRoleRestrictions` that define what actions users in these roles can perform and their financial limits, so
the permissions system allows customization per account.

Each business operation is represented by a `BusinessOperation` class, which defines its required permissions and
implements a method for validating the permissions of the users involved in the operation.

### bank.card

Similar to the `bank.account` package, this package contains card-related classes, such as `Card`, `CardType` etc.
The cards are managed by the `CardService` class. `Card` is the base abstract class common to all concrete card
implementations located in the `bank.card.impl` package. They are also constructed using a factory method in the base
abstract class. Currently, the implementations do not have any additional behavior, but they can be easily extended in
the future.

### bank.log

This package manages logging via AuditLogService. Abstract AuditLog classes and concrete implementations in
`bank.log.impl` minimize null fields for efficiency. Log views use the DTO pattern to ensure secure and efficient data
transfer.

### bank.currency

This package contains the `CurrencyExchangeService` class, which is the service used for storing exchange rates and
converting between currencies. Because we do not have exchange rates between all currencies, the implementation uses a
graph structure to store the rates and calculate the conversion. Ideally, to avoid any floating point errors and to
mimic the real world, we would need a direct mapping between any two currencies.

### bank.report

This package contains report related classes that are returned by the system when the user requests reports, such as
`TransactionsReport`, `SpendingReport`, and `BusinessReport`. These classes are an example of simple data classes that
are used to represent the information in a readable format.

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

### bank.merchant

This package contains all the merchant related classes, such as `Merchant`, `MerchantType` etc. The merchants are
managed by the `MerchantService` class, which is responsible for creating and managing the merchants. Here also resides
the `CashbackService` which is the system that manages the discounts that are given when an account makes a transaction
to a merchant. The two cashback methods, **spendingThreshold** and **nrOfTransactions**, are implementations of the
`CashbackStrategy` interface, which allows the system to easily add new cashback methods in the future. Each merchant
thus has a cashback strategy associated with it, which is used to calculate the cashback amount for each transaction.

### bank.servicePlan

This package contains the `ServicePlan` interface, as well as all the available service plans in the system. The
implementation of the service plans is located in the `bank.servicePlan.impl` package. Each plan is a lazily initialized
singleton that contains all information about the plan, such as the fees, and also implements the state pattern to
handle the upgrades.

### bank.eventSystem

This package contains the `BankEventService` class, which is the event dispatcher for the system. It is responsible for
registering and dispatching events. The events can be of any type, and are defined in the `bank.eventSystem.events`
package. Currently, the system has two events, `SplitPaymentEvent` and `TransactionEvent`, which are dispatched when a
split payment is finalized or a transaction is made, respectively. This allows for easy extensibility in the future,
with more complex events being added to the system. The event system is also a very clean way of decoupling the system,
as well as facilitating asynchronous operations.

### bank.splitPayment

This package contains the split payment related classes, such as `SplitPayment`, and `SplitPaymentService`. The
`SplitPayment` class represents a new request for a split payment, and the `SplitPaymentService` is responsible for
managing the state of the split payments while users accept or decline the request. When a payment is finalized, meaning
that it was either accepted by all users or declined by one, the `SplitPaymentService` dispatches a `SplitPaymentEvent`
that contains the result of the payment.

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

## Design Patterns

The system makes use of several design patterns to ensure a clean and maintainable codebase:

- **Service Layer Pattern**: The system is divided into services that handle specific operations, such as
  `BankAccountService`, `UserService`, `CardService`, etc. This pattern ensures that the system adheres to the Single
  Responsibility Principle and that the services are easily testable and maintainable.
- **Factory Method Pattern**: The `CommandFactory` class uses the factory method pattern to create command objects based
  on the input data. This allows the system to easily add new commands in the future without modifying the factory
  class.
- **Builder Pattern**: This is used for many classes throughout the project, such as `AuditLog`, `BankOperation` etc.
  The
  builder pattern allows the system to create complex objects with many optional parameters in a clean and readable way.
- **State Pattern**: The `ServicePlan` interface uses the state pattern to handle the upgrades of the service plans.
  This
  allows the system to easily add new plans and handle the transitions between them.
- **Observer Pattern**: The `BankEventService` class uses the observer pattern to dispatch events to the system. This
  allows the system to decouple the event dispatching from the event handling, and also facilitates asynchronous
  operations.
- **Command Pattern**: The `Bank` class uses the command pattern to execute operations. This allows the system to
  encapsulate the operations and their parameters in a single object, and also facilitates the extensibility of the
  system.
- **Data Transfer Object (DTO) Pattern**: The system uses view objects to transfer data between the services and the
  user. This pattern ensures that the system is decoupled from the external representation of the data, and also
  prevents external modifications to the system data.
- **Registry Pattern**: The `BankOperationRegistry` class acts as a repository for all the available operations in the
  system. This pattern allows the system to validate the operations before executing them
- **Strategy Pattern**: The `CashbackService` class uses the strategy pattern to calculate the cashback amount for each
  transaction. This allows the system to easily add new cashback methods in the future.
- **Singleton Pattern**: The `BankOperationRegistry` and `ServicePlan` classes are implemented as singletons to ensure
  that there is only one instance of each class in the system. They are also lazily initialized to reduce the memory
  usage of the system.