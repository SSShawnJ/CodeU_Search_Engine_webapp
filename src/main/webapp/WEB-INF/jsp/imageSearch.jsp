<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css">
<link href="<c:url value="/css/imageSearch.css" />" rel="stylesheet">
<link href="<c:url value="/css/base.css"/>" rel="stylesheet">

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


	<div class="webdesigntuts-workshop" align="center">
		<div style="margin-top: 50px">
		<a href="/"> <img id="logo"
			src="/images/bread.png"/>
		</a>
		</div>
		<div>
			<form method="POST" action="/imageSearchResult"
				enctype="multipart/form-data">
				<h2>
					Please specify an image:<br>
				</h2>
				
				<table>
					<tr>
						<td colspan="2">
							<input type="file" name="imagefile" accept="image/jpg, image/jpeg, image/png" onchange="readURL(this);" />
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							<img id="blah" src="" alt="your image" width="320px" height="240"/>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" style="position:absolute;">
							<input  type="submit" value="Send">
						</td>			
					</tr>
				</table>
				
			</form>
			
			
		</div>

		
	</div>

	



</body>
</html>