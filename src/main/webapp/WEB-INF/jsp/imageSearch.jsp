<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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

	<script type="text/javascript">
		function readURL(input) {
			if (input.files && input.files[0]) {
				var reader = new FileReader();

				reader.onload = function(e) {
					$('#blah').attr('src', e.target.result).width(320).height(
							240);
				};

				reader.readAsDataURL(input.files[0]);
			}
		}
	</script>


	<div class="container" align="center" style="margin-top: 15%">
		<a href="http://localhost:8080/"> <img
			src="https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"
			width="272" height="92" />
		</a>

		<div>
			<form method="POST" action="/imageSearchResult"
				enctype="multipart/form-data">
				<h3>
					Please specify an image:<br>
				</h3>
				<input type="file" name="imagefile"
					accept="image/jpg, image/jpeg, image/png" onchange="readURL(this);" />
				<img id="blah" src="" alt="your image" />
			
				<div>
					<input type="submit" value="Send">
				</div>
			</form>
		</div>

	</div>

	<div align="right"
		style="margin-top: 23%; margin-right: 100px; margin-bottom: 30px">
		&copy; Shawn Jin, CodeU Final Project</div>



</body>
</html>