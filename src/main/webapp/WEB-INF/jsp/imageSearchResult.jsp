<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<link href="<c:url value="/css/base.css"/>" rel="stylesheet">
<link href="<c:url value="/css/result.css" />" rel="stylesheet">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>


</head>
<body>
	<div class="webdesigntuts-workshop" align="center">
		<a href="/"> <img id="logo"
			src="/images/bread.png" />
		</a>
		<div>
			<form method="POST" action="/search">
				<table>
					<tr>
						<td class="form-input"><input
							name="word"  autocomplete="on" /></td>
						<td colspan="2"><input type="submit"
							value="Search" /></td>
						<td colspan="2"><input type="button"
							onclick="location.href='/searchImage'" value="Search Image">
						</td>
					</tr>
				</table>
			</form>

		</div>

		<div align="center" style="padding: 20px;">
			<table>
				<tr>
					<td align="center"><h2 style="color:white">${annotation}</h2></td>
				</tr>
			</table>
		</div>

		<div style="margin:30px 0px 0px 50px;">
			<div>
				<table>
					<tr>
						<td colspan="2">
						<div class="container" align="center"> ${word}</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div class="copy" align="right">
		&copy; Team 42, CodeU Final Project</div>
	</div>
</body>
</html>