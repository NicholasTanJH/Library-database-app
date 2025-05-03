import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Scanner;

public class CS3380A3Q3 {
    static Connection connection;

    public static void main(String[] args) throws Exception {

        // startup sequence
        MyDatabase db = new MyDatabase();
        runConsole(db);

        System.out.println("Exiting...");
    }

    public static void runConsole(MyDatabase db) {

        Scanner console = new Scanner(System.in);
        System.out.print("Welcome! Type h for help. ");
        System.out.print("db > ");
        String line = console.nextLine();
        String[] parts;
        String arg = "";

        while (line != null && !line.equals("q")) {
            parts = line.split("\\s+");
            if (line.indexOf(" ") > 0)
                arg = line.substring(line.indexOf(" ")).trim();

            if (parts[0].equals("h"))
                printHelp();
            else if (parts[0].equals("mp")) {
                db.getMostPublishers();
            } else if (parts[0].equals("s")) {
                if (parts.length >= 2)
                    db.nameSearch(arg);
                else
                    System.out.println("Require an argument for this command");
            } else if (parts[0].equals("l")) {
                try {
                    if (parts.length >= 2)
                        db.lookupByID(arg);
                    else
                        System.out.println("Require an argument for this command");
                } catch (Exception e) {
                    System.out.println("id must be an integer");
                }
            } else if (parts[0].equals("sell")) {
                try {
                    if (parts.length >= 2)
                        db.lookupWhoSells(arg);
                    else
                        System.out.println("Require an argument for this command");
                } catch (Exception e) {
                    System.out.println("id must be an integer");
                }
            } else if (parts[0].equals("notsell")) {
                try {
                    if (parts.length >= 2)
                        db.whoDoesNotSell(arg);
                    else
                        System.out.println("Require an argument for this command");
                } catch (Exception e) {
                    System.out.println("id must be an integer");
                }
            } else if (parts[0].equals("mc")) {
                db.mostCities();
            } else if (parts[0].equals("notread")) {
                db.ownBooks();
            } else if (parts[0].equals("all")) {
                db.readAll();
            } else if (parts[0].equals("mr")) {
                db.mostReadPerCountry();
            } else
                System.out.println("Read the help with h, or find help somewhere else.");

            System.out.print("db > ");
            line = console.nextLine();
        }

        console.close();
    }

    private static void printHelp() {
        System.out.println("Library database");
        System.out.println("Commands:");
        System.out.println("h - Get help");
        System.out.println("s <name> - Search for a name");
        System.out.println("l <id> - Search for a user by id");
        System.out.println("sell <author id> - Search for a stores that sell books by this id");
        System.out.println("notread - Books not read by its own author");
        System.out.println("all - Authors that have read all their own books");
        System.out.println("notsell <author id>  - list of stores that do not sell this author");
        System.out.println("mp - Authors with the most publishers");
        System.out.println("mc - Authors with books in the most cities");
        System.out.println("mr - Most read book by country");
        System.out.println("");

        System.out.println("q - Exit the program");

        System.out.println("---- end help ----- ");
    }

}

class MyDatabase {
    private Connection connection;

    public MyDatabase() {
        try {
            String url = "jdbc:sqlite:library.db";
            // create a connection to the database
            connection = DriverManager.getConnection(url);

        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //1
    public void nameSearch(String name) {
        try {
            System.out.println("Getting nameSearch for " + name);
            String sql = "Select first, last, id from people where first like ? or last like ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + name + "%");
            statement.setString(2, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id") + " - " + resultSet.getString("first") + " " + resultSet.getString("last"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //2
    public void lookupByID(String id) {
        try {
            System.out.println("Getting lookupByID for " + id);
            String sql = "Select first, last, aid from people where id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString("aid") == null)
                    System.out.println("This person has no author id");
                else
                    System.out.println(resultSet.getInt("aid") + " - " + resultSet.getString("first") + " " + resultSet.getString("last"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //3
    public void lookupWhoSells(String id) {
        try {
            System.out.println("Getting lookupWhoSells for " + id);
            String sql = "Select store.name, count(store.id) " +
                    "from people " +
                    "join books on people.aid = books.aid " +
                    "join publishers on books.pid = publishers.pid " +
                    "join sells on publishers.pid = sells.pid " +
                    "join store on sells.sid = store.id " +
                    "where people.aid = ? " +
                    "group by people.aid, store.id";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString("name") == null)
                    System.out.println("None");
                else
                    System.out.println(resultSet.getString("name") + " - " + resultSet.getInt("count(store.id)"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //4
    public void ownBooks() {
        try {
            System.out.println("Getting ownBooks");
            String sql = "Select first, last, title from people " +
                    "natural join books " +
                    "where id not in (" +
                    "select id from read " +
                    "where read.bid = books.bid" +
                    ")";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("first") + " " + resultSet.getString("last") + " - " + resultSet.getString("title"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //5
    public void readAll() {
        try {
            System.out.println("Getting readAll");
            String sql = "Select first, last from people " +
                    "where aid is not null and not exists( " +
                    "select bid " +
                    "from books " +
                    "where books.aid = people.aid " +
                    "except " +
                    "select bid from read where read.id = people.id " +
                    ")";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("first") + " " + resultSet.getString("last"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //6
    public void whoDoesNotSell(String id) {
        try {
            System.out.println("Getting whoDoesNotSell " + id);
            String sql = "Select store.name from store " +
                    "except " +
                    "select store.name from people " +
                    "join books on people.aid = books.aid " +
                    "join publishers on books.pid = publishers.pid " +
                    "join sells on publishers.pid = sells.pid " +
                    "join store on sells.sid = store.id " +
                    "where people.aid = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //7
    public void getMostPublishers() {
        try {
            System.out.println("Getting getMostPublishers");
            String sql = "Select first, last, count(distinct publishers.pid) from people " +
                    "join books on people.aid = books.aid " +
                    "join publishers on books.pid = publishers.pid " +
                    "group by people.aid " +
                    "order by count(distinct publishers.pid) desc " +
                    "limit 5";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("first") + " " + resultSet.getString("last") + " - " + resultSet.getInt("count(distinct publishers.pid)"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //8
    public void mostCities() {
        try {
            System.out.println("Getting mostCities");
            String sql = "select first, last, count(distinct city.cid) from people\n" +
                    "join books on people.aid = books.aid\n" +
                    "join publishers on books.pid = publishers.pid\n" +
                    "join sells on publishers.pid = sells.pid\n" +
                    "join store on sells.sid = store.id\n" +
                    "join city on store.cid = city.cid\n" +
                    "group by people.aid\n" +
                    "order by count(distinct city.cid) desc\n" +
                    "limit 5";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("first") + " " + resultSet.getString("last") + " - " + resultSet.getInt("count(distinct city.cid)"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //9
    public void mostReadPerCountry() {
        try {
            System.out.println("Getting mostReadPerCountry");
            String sql = "Select distinct country, title from city " +
                    "natural join people " +
                    "natural join read " +
                    "join books on read.bid = books.bid " +
                    "where books.title in ( " +
                    "select innerbooks.title from city innercity " +
                    "natural join people " +
                    "natural join read " +
                    "join books innerbooks on read.bid = innerbooks.bid " +
                    "where innercity.country = city.country " +
                    "group by innercity.country, innerbooks.title " +
                    "order by count(people.id) desc " +
                    "limit 1 " +
                    ")";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("country") + " - " + resultSet.getString("title"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

}
