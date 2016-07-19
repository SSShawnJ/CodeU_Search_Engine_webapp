<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>CodeU Search Engine</title>
</head>
<body>
	<h1>Powerful PP</h1>
<form:form method="POST" action="/search">
   <table>
    <tr>
    	<td><form:input path="word" /></td>
         <td colspan="2">
            <input type="submit" value="Search"/>
        </td>
        
    </tr>
    
</table>  
</form:form>
</body>
</html>