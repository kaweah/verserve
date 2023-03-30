package com.kaweah.verserve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Book {
	
	public static String samplePoem = "The extraordinary patience of things! \n"
			+ "This beautiful place defaced with a crop of suburban houses—\n"
			+ "How beautiful when we first beheld it,\n"
			+ "Unbroken field of poppy and lupin walled with clean cliffs;\n"
			+ "No intrusion but two or three horses pasturing,\n"
			+ "Or a few milch cows rubbing their flanks on the outcrop rockheads—\n"
			+ "Now the spoiler has come: does it care?\n"
			+ "Not faintly. It has all time. It knows the people are a tide\n"
			+ "That swells and in time will ebb, and all\n"
			+ "Their works dissolve. Meanwhile the image of the pristine beauty\n"
			+ "Lives in the very grain of the granite,\n"
			+ "Safe as the endless ocean that climbs our cliff.—As for us:\n"
			+ "We must uncenter our minds from ourselves;\n"
			+ "We must unhumanize our views a little, and become confident\n"
			+ "As the rock and ocean that we were made from.";

	// Member data
	
	private List <Poem> poems;
	
	public Book() {
		poems = new ArrayList <Poem> ();
		loadPoems();
	}

	/**
	 * Load poems from database (without content).
	 */
	
	void loadPoems() {
        String querySql = "SELECT * FROM poems;";
        System.out.println(querySql);
        
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            ResultSet poemSet = stmt.executeQuery(querySql);
            while (poemSet.next()) {
	            long id = poemSet.getLong("id");
	            String title = poemSet.getString("title");
	            String author = poemSet.getString("author");
	            int pub_year = poemSet.getInt("pub_year");
	            Poem poem = new Poem(id, title, author, pub_year, null);
	            poems.add(poem);
	            System.out.println("ID: " + id + "; Title: " + title + "; Author: " + author );
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
	}
	
	/**
	 * Insert a new poem into the database.
	 * 
	 * @param title
	 * @param author
	 * @param pub_year
	 * @param text
	 * @return
	 */
	
    public long insertPoem(String title, String author, int pub_year, String text) {
    	
    	Poem poem = Poem.insertPoem(title, author, pub_year, text);
    	poems.add(poem);
		   	
    	return poem.getId();
	}

    public Poem getPoemById(long poem_id) {
    	for (Poem poem : poems) {
    		if (poem_id == poem.getId()) return poem;
    	}
    	return null;
    }
    
    /**
     * Remove poem by id (also removing the poem's sections and verses).
     * 
     * @param poem_id
     * @return
     */
    
    public boolean removePoem(long poem_id) {
    	// if poem has no sections, returnValue should remain true after section loop.
    	boolean returnValue = true;
    	
    	Poem poem = getPoemById(poem_id);
    	
    	if (poem != null) {   	
	    	// 1. Remove poem sections
	    	List <Long> sections = poem.loadSectionIds();
	    	for (Long section_id : sections) {
	    		// Any single failure to remove a section or verse should not mean summary failure,
	    		// and should not cause poem deletion to abort.
	    		System.out.println("Removing section " + section_id);
	    		poem.removeSection(section_id);
	    	}
	    	
	    	// 2. Remove record from poems table.
	    	try (Connection conn = DriverManager.getConnection(Configuration.dbUrl);
	                Statement stmt = conn.createStatement()) {
	        	String deleteCmd = "DELETE FROM poems WHERE id = " + poem_id + ";";
	        	System.out.println(deleteCmd);
	        	returnValue = stmt.execute(deleteCmd);
	            conn.close();
	        } catch (SQLException e) {
	            System.err.println(e.getMessage());
	        }
    	} else returnValue = false;
    	
    	return returnValue;
    }

    /**
     * Example use(s)
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {

    	Book book = new Book();

/*
    	// Parameters
    	String title = "Carmel Point";
    	String author = "Robinson Jeffers";
    	int pub_year = 1954;
    	
    	long poemId = book.insertPoem(title, author, pub_year, samplePoem);
    	
    	Poem newPoem = book.getPoemById(poemId);
    	
    	System.out.println("Verses:");
    	for (Long sectionId : newPoem.getSections()) {
    		Section section = newPoem.getSectionById(sectionId);
    		for (String verse : section.getVerses()) {
    			System.out.println(verse);
    		}
    	}
*/

    	long poem_id = 1;
    	Section.dumpPoemSections(poem_id);
    	book.removePoem(poem_id);
    	Section.dumpPoemSections(poem_id);
    	Poem.dumpAllPoems();
    }


}
