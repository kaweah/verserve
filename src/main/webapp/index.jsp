<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.sqlite.*" %>
 
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Poems</title>
    </head>
    <body>
        <table>
            <thead>
                <tr>
                    <th>Poem ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Year</th>
                </tr>
            </thead>
            <tbody>
            <%
                Class.forName("org.sqlite.JDBC");
                Connection conn =
                     DriverManager.getConnection("jdbc:sqlite:/Users/dan/dev/databases/verserve.db");
                Statement stat = conn.createStatement();
 
                ResultSet rs = stat.executeQuery("select * from poems;");
 
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("id") + "</td>");
                    out.println("<td>" + rs.getString("title") + "</td>");
                    out.println("<td>" + rs.getString("author") + "</td>");
                    out.println("<td>" + rs.getInt("pub_year") + "</td>");
                    out.println("</tr>");
                }
 
                rs.close();
                conn.close();
            %>
            </tbody>
        </table>
    </body>
</html>