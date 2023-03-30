package com.kaweah.verserve;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Poem {
	
	private long id;
	private String title;
	private String author;
	private int pub_year;
	private List <Section> sections;

	public Poem() {
		
	}
	
    public Poem(long id, String title, String author, int pub_year, List <Section> sections) {
    	this.id = id;
    	this.title = title;
		this.author = author;
		this.pub_year = pub_year;
		this.sections = sections;
	}
    
    public long getId() {
    	return id;
    }

    public List <Section> getSections() {
    	if (sections == null) sections = new ArrayList <Section> ();
    	return sections;
    }

    public Section getSectionById(long section_id) {
    	
    	// Now that we're being asked for section info, load this poem's sections.
    	
    	if (getSections().isEmpty()) {
    		loadSections();
    	}
    	
    	for (Section section : getSections()) {
    		if (section_id == section.getId()) return section;
    	}
    	return null;
    }

    /**
     * Insert a new row into the poems table
     *
     * @param title
     * @param author
     * @param year
     */
    
    protected Long insertPoemHeader(String title, String author, int year) {
        String sql = "INSERT INTO poems(title,author,pub_year) VALUES(?,?,?)";
        long poem_id = 0;
        
        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setInt(3, year);
            pstmt.executeUpdate();

            System.out.println("Get generated key ...");
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                	poem_id = generatedKeys.getLong(1);
                	System.out.println("Poem id = " + poem_id);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            } catch (SQLException e) {
            	System.err.println("Failed to get generated key: " + e.getMessage());
            	throw e;
            }

            
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return poem_id;
    }

    /**
     * Insert poem into database.
     * 
     * @param title
     * @param author
     * @param pub_year
     * @param text
     * @return
     */
    
    public static Poem insertPoem(String title, String author, int pub_year, String text) {
		Poem poem = new Poem();
		poem.id = poem.insertPoemHeader(title, author, pub_year);
    	
    	if (text != null) {
    		Section section = Section.insertSection(poem.id, text);
    		poem.getSections().add(section);
    		// VerseInserter inserter = new VerseInserter();
    		// inserter.insertSection(id, text);
    	}
    	
    	return poem;
	}
    
    /**
     * Remove section with specified section_id (and also remove all section verses).
     * 
     * @param section_id
     * @return
     */
    
    public boolean removeSection(long section_id) {
    	// returnValue should remain true if no verses are encountered.
    	Section section = getSectionById(section_id);
    	boolean returnValue = section.removeVerses();

    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
                Statement stmt = conn.createStatement()) {
        	String deleteCmd = "DELETE FROM sections WHERE id = " + section_id + ";";
        	System.out.println(deleteCmd);
        	if (!stmt.execute(deleteCmd)) returnValue = false;
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    	return returnValue;
    }

    /**
     * Loads sections (w/o verses).
     * @return
     */
    
    public void loadSections() {

    	getSections().clear();
    	
    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
                Statement stmt = conn.createStatement()) {
        	String queryStr = "SELECT * FROM sections WHERE poem_id = " + id + ";";
        	System.out.println(queryStr);
        	ResultSet sectionSet = stmt.executeQuery(queryStr);
        	while (sectionSet.next()) {
		        Long section_id = sectionSet.getLong("id");
		        Long poem_id = sectionSet.getLong("poem_id");
        		Section section = new Section(section_id, poem_id);	
        		System.out.println("Loading section id=" + section_id);
		        getSections().add(section);
        	}
        	sectionSet.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public List <Long> loadSectionIds() {
    	
    	List <Long> sectionIds = new ArrayList <Long> ();

    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
                Statement stmt = conn.createStatement()) {
        	String queryStr = "SELECT id FROM sections WHERE poem_id = " + id + ";";
        	System.out.println(queryStr);
        	ResultSet sections = stmt.executeQuery(queryStr);
        	while (sections.next()) {
		        Long section_id = sections.getLong("id");
		        sectionIds.add(section_id);
        	}
        	sections.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return sectionIds;
    }
    
    static void dumpAllPoems() {
        String querySql = "SELECT * FROM poems;";
        System.out.println(querySql);
        
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            ResultSet poems = stmt.executeQuery(querySql);
            while (poems.next())
            {
	            long id = poems.getLong("id");
	            String title = poems.getString("title");
	            String author = poems.getString("author");
	            int pub_year = poems.getInt("pub_year");
	            System.out.println("ID: " + id + "; Title: " + title + "; Author: " + author);
        	}
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	
    }

    public static void main(String[] args) {
    	Poem.dumpAllPoems();
    }
}
