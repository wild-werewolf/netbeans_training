<%-- 
    Document   : index
    Created on : 03.08.2013, 22:18:14
    Author     : WereWolf
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Опрос разработчика</title>
    </head>
    <body>
        <h1>Добро пожаловать на страницу опроса разработчка</h1>
        <p>Please, indicate which programming languages you are familar with.</p>
        <form name="form1" action="ControllerServlet" method="post">
            <table border="0">
                <tbody>
                    <tr>
                        <td>Full Name:</td>
                        <td><input type="text" name="fullName" value="" /></td>
                    </tr>
                    <tr>
                        <td>Java</td>
                        <td><input type="checkbox" name="progLang" value="java" /></td>
                    </tr>
                    <tr>
                        <td>Groovy</td>
                        <td><input type="checkbox" name="progLang" value="groovy" /></td>
                    </tr>
                    <tr>
                        <td>Scala</td>
                        <td><input type="checkbox" name="progLang" value="scala" /></td>
                    </tr>
                    <tr>
                        <td>C#</td>
                        <td><input type="checkbox" name="progLang" value="c_sharp" /></td>
                    </tr>
                    <tr>
                        <td>Ruby</td>
                        <td><input type="checkbox" name="progLang" value="ruby" /></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><input type="submit" value="Submit" /></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>
