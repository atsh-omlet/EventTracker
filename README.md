# Event Tracker Android App

## Overview
This application is designed to allow users to create and manage events. It was originally developed as a university project and has been iteratively enhanced, resulting in improved 
architecture, database design, and additional features.

## Features
- User authentication (login details hashed using BCrypt)
- Create, edit, and delete events
- Search events by title
- Filter events by date
- Event notifications
- Account management (logout, clear data, delete account)

## Screenshots
![Screenshot 2025-12-25 at 13 22 12](https://github.com/user-attachments/assets/8e04c7ff-8133-4464-8519-f73aaf01dfd4)

## Tech Stack
- Language: Java, Kotlin
- Architecture: MVVM
- Database: MongoDB Realm
- Platform: Android
- Build Tool: Gradle

## Architecture Overview
The application originally loosely followed the MVC pattern, resulting in mixed UI and business logic. It was later refactored to MVVM, where business logic is decoupled from UI elements,
enforcing separation of concerns and improving maintainability.

## Database
Originally implemented with SQLite, the data layer has been migrated to MongoDB Realm to adopt an object-oriented data model.

## Author
Atsushi
