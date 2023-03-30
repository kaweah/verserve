package com.kaweah.verserve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author sqlitetutorial.net
 */
public class TableCreator {

    public static void dropTables() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/dan/dev/databases/verserve.db";
        
        String tableName = "poems";
        
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE " + tableName + ";");
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        tableName = "sections";
        
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
        	stmt.execute("DROP TABLE " + tableName + ";");
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        tableName = "verses";

        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
        	stmt.execute("DROP TABLE " + tableName + ";");
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Create table for poems.
     *
     */
    
    public static void createPoemsTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/dan/dev/databases/verserve.db";
        
        String sql = "CREATE TABLE IF NOT EXISTS poems (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	title text,\n"
                + "	author text,\n"
                + "	pub_year integer\n"
                + ");";
        
        System.out.println(sql);
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

	/**
     * Create table for poem sections.
     *
     */
    
    public static void createSectionsTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/dan/dev/databases/verserve.db";
        
        String sql = "CREATE TABLE IF NOT EXISTS sections (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	poem_id integer NOT NULL,\n"
                + "	idx integer NOT NULL\n"
                + ");";
        
        System.out.println(sql);

        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

	/**
     * Create table for verses.
     *
     */
    
    public static void createVersesTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/dan/dev/databases/verserve.db";
        
        String sql = "CREATE TABLE IF NOT EXISTS verses (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	text text NOT NULL,\n"
                + "	section_id integer NOT NULL,\n"
                + "	idx integer NOT NULL\n"
                + ");";
        
        System.out.println(sql);

        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
    	dropTables();
        System.out.println("Tables dropped.");
        createPoemsTable();
        createSectionsTable();
        createVersesTable();
        System.out.println("Tables created.");
    }

}