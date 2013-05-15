Working with JTables and MySQL in Java
======================================

This application is a basic MySQL CRUD application using JTables and other graphical components in the Swing toolkit in Java. The user will be able to run the application and navigate the items in the database using the JTable. This will include sorting, adding items, updating individual information in each row, and removing items.

The Application
---------------

The application contains a JTable that is used to display the data from the database. This is auto-sortable, and the interface contains text fields for insertion, as well as an error reporting area at the top. Below is a screenshot of the application.

![alt text][logo]

The Database
------------

The application connects to a MySQL database that holds customer information. The information that it holds is as follows:
- ID
- First Name
- Last Name
- Phone Number
- Email Address
- City
- State
- Date Registered

You can create and populate the database with sample data by importing the customer.sql file included in the repository, or you can create this database on your own with the following MySQL:
```SQL
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `last_name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `phone_number` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `email_address` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `city` varchar(80) COLLATE utf8_unicode_ci NOT NULL,
  `state` varchar(2) COLLATE utf8_unicode_ci NOT NULL,
  `date_registered` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
```
To import the sql file, run the following from your command line.
```
mysql -u YOURUSERNAME -p YOURDATABASENAME < customer.sql

More functionality will be added as I progress.

[logo]: https://github.com/caseyscarborough/jtables-crud/raw/master/img/1.png "The application's basic layout."
