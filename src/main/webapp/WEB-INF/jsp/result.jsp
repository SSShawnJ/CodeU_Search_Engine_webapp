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

	<div class="container" align="center" style="margin-top: 5%">
		<div>
			<img
				src="https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"
				width="272" height="92" />
		</div>

		

		<div class="col-sm-4" style="margin-left: 24%; margin-right:5%;margin-top:5%">
			<div class="list-group">
				<table>
					<tr>
						<td align="left">${word}</td>
					</tr>
				</table>
			</div>
		</div>
	</div>

	<div align="right" style="margin-right: 100px; margin-bottom:30px">&copy; Shawn Jin,
		CodeU Final Project</div>


</body>
</html>