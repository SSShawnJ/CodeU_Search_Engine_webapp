<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>


</head>
<body>
	<div class="container" align="center" style="margin-top: 15%">
		<a href="/"> <img
			src="https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"
			width="272" height="92" />
		</a>
		<div style="margin-top: 3%">
			<form method="POST" action="/search">
				<table>
					<tr>
						<td width="600" height="50" style="padding-right: 30"><input
							name="word" class="form-control" autocomplete="on" /></td>
						<td colspan="2"><input
							class="btn btn-lg btn-primary btn-block" type="submit"
							value="Search" /></td>
						<td colspan="2"><input
							class="btn btn-lg btn-primary btn-block" type="button"
							onclick="location.href='/searchImage'" value="Search Image">
						</td>
					</tr>
				</table>
			</form>

		</div>

		<div align="left" style="padding: 20px; margin: 20px">
			<table>
				<tr>
					<td class="list-group-item-heading" align="left">Search
						result:</td>
					<td class="list-group-item-text" align="left">${annotation}</td>
				</tr>
			</table>
		</div>

		<div style="margin-left: 4%; margin-right: 5%; margin-top: 5%">
			<div class="list-group">
				<table>
					<tr>
						<td align="left">${word}</td>
					</tr>
				</table>
			</div>
		</div>
	</div>

	<div align="right"
		style="margin-top: 23%; margin-right: 100px; margin-bottom: 30px">
		&copy; Shawn Jin, CodeU Final Project</div>


</body>
</html>