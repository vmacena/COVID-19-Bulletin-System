# COVID-19 Bulletin System

Overview
This Java application aims to streamline the consolidation of COVID-19 bulletins from various cities, states, and multiple days into a unified database. The system allows the storage, editing, and removal of daily bulletins, each containing auto-generated ID, city name, state, date, number of new infections, number of new deaths, and the percentage of occupied ICU beds. The application also provides filtering functionalities based on date range and location (state or city).

Features
Bulletin Management:

Inclusion, editing, and removal of daily COVID-19 bulletins.
Automatic generation of unique IDs for each bulletin.
Filtering:

Filter bulletins based on date range (start and/or end) and location (state or city).
City filter supports partial name matching, regardless of case sensitivity.
Statistics Display:

Show total number of infections and deaths for the selected set of bulletins.
Display the average occupancy of ICU beds.
Database Integration:

Utilizes an SQLite database named database.db for persistent storage.
The DatabaseBuilder class in the persistence package creates and populates the initial database with test data.
The ConnectionFactory class in the persistence package manages connections and statement creation.
Getting Started
Clone the repository to your local machine.
Run the DatabaseBuilder class to initialize and populate the SQLite database.
Open the BulletinApp class in the view package to start the application.
Implementation Details
The application is structured into packages:

model: Contains the Bulletin class representing the COVID-19 bulletin.
persistence: Includes the DatabaseBuilder class for database setup and the ConnectionFactory class for managing database connections.
view: Houses the BulletinApp class, the main entry point for the application.
Usage
Follow the on-screen instructions in the BulletinApp UI to interact with the system. Perform bulletin management, apply filters, and view statistics seamlessly.

Note
For re-creating the database, execute the main method in the DatabaseBuilder class.

Note: This project is a work in progress, and additional features and improvements are planned for future updates. Contributions and feedback are welcome.

Best Practices:

Follow Java naming conventions.
Use meaningful variable and method names.
Maintain code modularity and readability.
Disclaimer:
This project is part of an exercise and may not represent a fully polished or production-ready application. Use at your own discretion.
