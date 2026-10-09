# Java-Projects
# SQLite Flashcard App

A Java desktop application built with a graphical user interface (GUI) and an integrated database to store, manage, and test yourself on study flashcards.

## Why I Built This
Preparing for competitive exams means dealing with a massive number of formulas, concepts, and definitions. Traditional paper flashcards are easy to lose, and basic terminal programs wipe all your saved data the moment you close the application. I built this desktop app to create a permanent, digital study system that saves my study sets to a database and lets me run quick, randomized review sessions before exams.

## Key Features
- Visual Interface: Replaces the standard command terminal with a proper Java Swing desktop window containing organized input fields and action buttons.
- Permanent Data Storage: Uses an integrated SQLite database (flashcards.db) via JDBC prepared statements to ensure study cards are never lost when the app shuts down.
- Data Management: Full capability to add new flashcards, display the entire collection in a scrollable view, update existing details, or delete specific entries by ID.
- Randomized Review Mode: Pulls a random card from the database to test your knowledge, checking your typed input against the correct answer.

## Requirements
The project is built using native Java libraries and requires the SQLite JDBC driver (.jar file) added to your project dependencies or classpath to handle the database connectivity.
