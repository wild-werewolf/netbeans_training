 <%-- 
    Document   : output
    Created on : 04.08.2013, 0:10:05
    Author     : WereWolf
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Thank you!</title>
    </head>
    <body>
        <h2>Thanks for taking our survey</h2>
        <p>
            <%= request.getParameter("fullName") %>
            you indicate you are familiar with the following
            programming languages: </p>
        <ul>
            <% 
                String[] selectedlanguages=request.getParameterValues("progLang");
                if(selectedlanguages!=null) {
                    for(int i=0; i<selectedlanguages.length; i++ ) {
            %>
            <li>
                <%= selectedlanguages[i]%>
            </li>
            <%      }
                }
            %>
        </ul>
    </body>
</html>
