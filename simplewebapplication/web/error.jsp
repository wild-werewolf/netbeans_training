<%-- 
    Document   : error
    Created on : 28.09.2013, 12:53:39
    Author     : WereWolf
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ошибка входа в систему</title>
    </head>
    <body>
        Возникла ошибка при входе в систему. Пожалуйста, попробуйте еще раз.
        <br/>
        <form method="POST" action="j_security_check">
            <table cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td align="right">Имя&nbsp;</td>
                    <td>
                        <input type="text" name="j_username"/>
                    </td>
                </tr>
                <tr>
                    <td align="right">Пароль&nbsp;</td>
                    <td>
                        <input type="password" name="j_password"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <input type="submit" name="Войти"/>
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>
