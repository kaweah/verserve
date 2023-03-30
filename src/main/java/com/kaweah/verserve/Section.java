package com.kaweah.verserve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Section {

	private long id;
	private long poem_id;
	
	public Section() {
		
	}
	
	public Section (long poem_id) {
		this.poem_id = poem_id;
	}

	public Section (long id, long poem_id) {
		this.id = id;
		this.poem_id = poem_id;
	}

	public long getId() {
		return id;
	}
	
    /**
     * Insert a new verse into the verses table
     *
     * @param text
     * @param index
     */
    
    protected void insertVerse(String text, int index) {
        String sql = "INSERT INTO verses(text,section_id,idx) VALUES(?,?,?)";

        System.out.println("Insert: \"" + text + "\" (" + id + ":" + index + ").");
        
        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.setLong(2, id);
            pstmt.setInt(3, index);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Insert a new verse into the verses table
     *
     * @param text
     */
    
    protected void insertVerses(String text) {

        String [] verses = text.split("\\n");
        int index = 0;
        for (String verse : verses) {
        	insertVerse(verse, index++);
        }
    }

    /**
     * Insert a new section into the sections table
     *
     * @param poem_id
     * @param text
     */
    
    protected static Section insertSection(long poem_id, String text) {

    	Section section = new Section(poem_id);
    	
        // Query sections table for number of instances of poem id, then apply this to section_index.
    	
    	int section_index = 0;
    	long section_id = 0;
        
        String querySql = "SELECT COUNT(*) FROM sections WHERE poem_id = " + poem_id + ";";
        System.out.println(querySql);
        
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(querySql);
            rs.next();
            section_index = rs.getInt(1);
            System.out.println("" + section_index + " sections found for poem #" + poem_id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        // Insert new section.
        
        String insertSql = "INSERT INTO sections(poem_id,idx) VALUES(?,?)";
        System.out.println(insertSql);
        
        try (Connection conn = DBConnector.connect();
            PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, poem_id);
            pstmt.setInt(2, section_index);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            } else if (affectedRows > 1) {
                throw new SQLException("Multiple rows affected.");            	
            }

            System.out.println("Get generated key ...");
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                	section_id = generatedKeys.getLong(1);
                	System.out.println("Section id = " + section_id);

                    // Acquire section id from newly created record and then insert verses.
                    section.id = section_id;
                    section.insertVerses(text);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            } catch (SQLException e) {
            	System.err.println("Failed to get generated key: " + e.getMessage());
            	throw e;
            }
        } catch (SQLException e) {
            System.err.println("Failed to insert record: " + e.getMessage());
        }
        
        return section;
    }

    /**
     * Remove all verses in this section.
     * 
     * @return
     */
    
    protected boolean removeVerses() {
    	// returnValue should remain true if no verses are encountered.
    	boolean returnResult = true;
    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
                Statement stmt = conn.createStatement()) {
        	String deleteCmd = "DELETE FROM verses WHERE section_id = " + id + ";";
        	System.out.println(deleteCmd);
        	if (!stmt.execute(deleteCmd)) returnResult = false;
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	return returnResult;
    }

    /**
     * Remove a single verse by verse id.
     * 
     * @param verse_id
     * @return
     */
    
    protected boolean removeVerse(long verse_id) {
    	boolean returnValue = false;
    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
                Statement stmt = conn.createStatement()) {
        	String deleteStr = "DELETE FROM verses WHERE id = " + verse_id + ";";
        	returnValue = stmt.execute(deleteStr);
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	return returnValue;
    }
    
    /**
     * Get all verses in this section.
     * 
     * @return
     */
    
    protected List <Long> getVerseIds() {
    	
    	List <Long> verseList = new ArrayList <Long> ();

    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
                Statement stmt = conn.createStatement()) {
        	String queryStr = "SELECT id FROM verses WHERE section_id = " + id + ";";
        	ResultSet verses = stmt.executeQuery(queryStr);
        	while (verses.next()) {
		        Long verse_id = verses.getLong("id");
		        verseList.add(verse_id);
        	}
        	verses.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return verseList;
    }

    /**
     * Get all verses in this section.
     * 
     * @return
     */
    
    public List <String> getVersesText() {
    	
    	List <String> verseList = new ArrayList <String> ();

    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
                Statement stmt = conn.createStatement()) {
        	String queryStr = "SELECT * FROM verses WHERE section_id = " + id + ";";
        	ResultSet verses = stmt.executeQuery(queryStr);
        	while (verses.next()) {
		        String verse = verses.getString("text");
		        verseList.add(verse);
        	}
        	verses.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return verseList;
    }
    
    static void dumpPoemSections(long poem_id) {
        String querySql = "SELECT * FROM sections WHERE poem_id=" + poem_id + ";";
        System.out.println(querySql);
        
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            ResultSet sections = stmt.executeQuery(querySql);
        	while (sections.next()) {
	            Long id = sections.getLong("id");
	            System.out.println("Section: " + id + "; Poem: " + poem_id);
        	}
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	
    }

    static void dumpAllSections() {
        String querySql = "SELECT * FROM sections;";
        System.out.println(querySql);
        
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            ResultSet sections = stmt.executeQuery(querySql);
        	while (sections.next()) {
	            Long id = sections.getLong("id");
	            Long poem_id = sections.getLong("poem_id");
	            System.out.println("Section: " + id + "; Poem: " + poem_id);
        	}
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	
    }

    public static void main(String[] args) {
    	Section.dumpAllSections();
    	Verse.dumpAllVerses();
    }
}
