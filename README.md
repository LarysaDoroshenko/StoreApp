# StoreApp
Store App is a REST API application that simulates the order creation process.
Normally any order would include 1+ products, but for simplicity an order can include only one product.

To avoid dirty reads of product quantity by multiple concurrent requests TransactionSynchronizationManager is used.

The app functionality is covered with two integration tests:
* **OrderSingleThreadedTest** - checks a happy path of order creation flow where two cases are covered:
    * there is enough products in the store (order status is SUCCESS and product quantity is decreased)
    * there is not enough products in the store (order status is FAILURE and product quantity is unchanged)
* **OrderMultiThreadedTest** - simulates multiple concurrent order creation requests