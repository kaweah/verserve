<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.sqlite.*" %>
<% String poem = request.getParameter("poem"); %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Poem Detail</title>
    </head>
    <body>
            <%
                Class.forName("org.sqlite.JDBC");
                Connection conn =
                     DriverManager.getConnection("jdbc:sqlite:/Users/dan/dev/databases/verserve.db");
                
                Statement poemQuery = conn.createStatement();
                
                ResultSet poems = poemQuery.executeQuery("select * from poems WHERE id = " + poem + ";");
                
                while (poems.next()) {
                    out.println("<p>Poem ID: " + poems.getInt("id") + "</p>");
                    out.println("<p>Title:   " + poems.getString("title") + "</p>");
                    out.println("<p>Author:  " + poems.getString("author") + "</p>");
                }               

                poems.close();
              %>
          <table>
            <thead>
                <tr>
                    <th>Section</th>
                    <th>Verse ID</th>
                    <th>Text</th>
                    <th>Verse #</th>
                </tr>
            </thead>
            <tbody>
            <%
		        Statement sectionQuery = conn.createStatement();
		           
		        ResultSet sections = sectionQuery.executeQuery("select * from sections WHERE poem_id = " + poem + " ORDER BY idx;");
		
		        while (sections.next()) {
			        Statement verseQuery = conn.createStatement();
			        String section_id = sections.getString("id");
	 				String query = "select * from verses WHERE section_id = " + section_id + " ORDER BY idx;";
	 		 		// System.out.println(query);
	                ResultSet verses = verseQuery.executeQuery(query);
	                while (verses.next()) {
	                    out.println("<tr>");
	                    out.println("<td>" + section_id + "</td>");
	                    out.println("<td>" + verses.getString("id") + "</td>");
	                    out.println("<td>" + verses.getString("text") + "</td>");
	                    out.println("<td>" + verses.getInt("idx") + "</td>");
	                    out.println("</tr>");
	                }
	 
	                verses.close();
		        }
		        
		        sections.close();
                conn.close();
            %>
            </tbody>
        </table>
    </body>
</html>