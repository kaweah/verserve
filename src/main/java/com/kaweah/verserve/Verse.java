package com.kaweah.verserve;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Verse {

    /**
     * Insert a new verse into the verses table
     *
     * @param text
     * @param section_id
     * @param index
     */
    
    protected void insertVerse(String text, long section_id, int index) {
        String sql = "INSERT INTO verses(text,section_id,idx) VALUES(?,?,?)";

        System.out.println("Insert: \"" + text + "\" (" + section_id + ":" + index + ").");
        
        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.setLong(2, section_id);
            pstmt.setInt(3, index);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Insert a new verse into the verses table
     *
     * @param text
     * @param section_id
     */
    
    protected void insertVerses(String text, long section_id) {

        String [] verses = text.split("\\n");
        int index = 0;
        for (String verse : verses) {
        	insertVerse(verse, section_id, index++);
        }
    }

    static void dumpAllVerses() {
        String querySql = "SELECT section_id, text FROM verses;";
        System.out.println(querySql);
        
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            ResultSet verses = stmt.executeQuery(querySql);
        	while (verses.next()) {
	            Long section_id = verses.getLong("section_id");
	            String text = verses.getString("text");
	            System.out.println("Section: " + section_id + ": " + text);
        	}
        	verses.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	
    }

    static boolean purgeAllVerses() {
        String querySql = "DELETE FROM verses;";
        System.out.println(querySql);
        boolean result = true;
        
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            result = stmt.execute(querySql);
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	return result;
    }

    public static void main(String[] args) {
       	Verse.dumpAllVerses();
       	Verse.purgeAllVerses();
    	Verse.dumpAllVerses();
    }

}
