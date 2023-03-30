<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.sqlite.*" %>
<%@ page import="com.kaweah.verserve.*" %>

<jsp:useBean id="book" scope="application"
    class="com.kaweah.verserve.Book">
   </jsp:useBean>
   
<%
 String title = request.getParameter("title");
 String author = request.getParameter("author");
 String yearStr = request.getParameter("year");
 int pub_year = Integer.valueOf(yearStr);
 String text = request.getParameter("text");
 long poem_id = book.insertPoem(title, author, pub_year, text);
 Poem poem = book.getPoemById(poem_id);
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>New Poem Detail</title>
    </head>
    <body>
    	<p>Poem ID: <%=poem_id%></p>
    	<p>Title:   <%=title%></p>
    	<p>Author:  <%=author%></p>
    	<p>Year:    <%=yearStr%></p>
      <%
        for (Section section : poem.getSections()) {
        	for (String verse : section.getVersesText()) {
        		out.println("<p>" + verse + "</p>");
        	}
        }
      %>
    </body>
</html>