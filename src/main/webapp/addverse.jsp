<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.sqlite.*" %>
<%
 String poem = request.getParameter("poem");
 String verse = request.getParameter("verse");
 String number = request.getParameter("number");
%>
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
                    <th>Verse ID</th>
                    <th>Text</th>
                    <th>Verse #</th>
                </tr>
            </thead>
            <tbody>
            <%
            Statement verseInsert = conn.createStatement();
            
            Boolean result = verseInsert.execute("INSERT INTO verses (text, poem, number) VALUES ('" + verse + "'," + poem + "," + number + ");");

            verseInsert.close();

            Statement verseQuery = conn.createStatement();
 
            ResultSet verses = verseQuery.executeQuery("select * from verses WHERE poem = " + poem + " ORDER BY number;");

            while (verses.next()) {
                out.println("<tr>");
                out.println("<td>" + verses.getString("id") + "</td>");
                out.println("<td>" + verses.getString("text") + "</td>");
                out.println("<td>" + verses.getInt("number") + "</td>");
                out.println("</tr>");
            }

            verses.close();
            conn.close();
            %>
            </tbody>
        </table>
    </body>
</html>