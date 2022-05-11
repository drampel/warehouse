## Warehouse project in Java using PostgreSQL

Methods:
- Connecting to a database and creating a table if it doesn't exist.
- Creating a menu with a choice of actions:
  - 1 - addProduct
  - 2 - updateProduct
  - 3 - deleteProduct
  - 4 - printProfit
  - 5 - exit
- Adding a product with parameters such as: name, buy price, sale price, count. Sequential id values are automatically generated.
- Update product parameter values by id or by name. It is also possible to exit to the menu if you change your mind about updating any value of a product parameter.
- Deleting a product by id or by name. When a product is deleted, it remains in the database with the "deleted" status, and will not be taken into account when using the methods of adding, updating, deleting, printing profit.
It is also possible to exit to the menu if you change your mind about deleting the product.
- Calculation of profit from all products and their printing.

There are also various data entry checks.
