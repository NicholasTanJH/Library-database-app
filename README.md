# SQLite Java CRUD Application

This is a simple Java console application that demonstrates **CRUD operations (Create, Read, Update, Delete)** using **SQLite** as the database. It connects to a local SQLite database, allowing users to interact with the data through a command-line interface.

- `MyApp.java`: The main application class containing the user interface and menu logic.
- `MyDatabase.java`: Handles database connection and CRUD operations using JDBC.

---

## 🛠 Features
-	Author Search: Find authors by name or ID.
-	Store Listings: Identify stores that sell or do not sell books by a specific author.
-	Reading Insights: Determine which authors have read all their books or none.
-	Publishing Stats: Discover authors with the most publishers or books in the most cities.
-	Country Trends: Find the most-read books per country.
- Uses prepared statements to prevent SQL injection.

---

## 🛠️ Requirements

- Java 8 or newer
- `sqlite-jdbc` driver (already included in the project or downloaded manually)

---

## 🚀 Getting Started

🏃‍♂️ Running the Application

```bash
git clone https://github.com/NicholasTanJH/Library-database-app
cd Library-database-app
```

Use the provided Makefile:

✅ Compile and Run

```bash
make run
```
Alternatively, compile manually:
```bash
javac -cp .:sqlite-jdbc-3.39.3.0.jar src/MyApp.java src/MyDatabase.java
java -cp src:sqlite-jdbc-3.39.3.0.jar MyApp
```

---

## 💻 Usage

Upon running the application, you’ll be presented with a prompt:
```bash
Welcome! Type h for help.
db >
```

Type h to view available commands:
- s <name>: Search for a person by name.
- l <id>: Look up a user by ID.
- sell <author_id>: List stores selling books by the author.
- notsell <author_id>: List stores not selling books by the author.
- notread: List books not read by their authors.
- all: List authors who have read all their books.
- mp: Show authors with the most publishers.
- mc: Show authors with books in the most cities.
- mr: Show the most-read book per country.
- q: Exit the application.

---

## 📄 License

This project is licensed under the MIT License. See the LICENSE file for details.

---

## 🙌 Acknowledgments
•	Developed as part of the COMP3380 course at the University of Manitoba.