<%@ page session="false" %><%@
page import="ordappengine.*" %><%

// Anti-cache headers. See:
// http://www.xyzws.com/JSPfaq/how-to-disable-browser-caching-for-a-specific-jsp/11
// http://stackoverflow.com/questions/49547/making-sure-a-web-page-is-not-cached-across-all-browsers
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Expires", "Sat, 26 Jul 1997 05:00:00 GMT");

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="css/ord.css" media="screen" rel="stylesheet" type="text/css" />
<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Roboto:300' rel='stylesheet' type='text/css'>
<title>Computer Science</title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="js/jquery-1.7.2.min.js"><\/script>')</script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$('.carousel').carousel({
			interval:200000
		});
	});
	function check(input) {
    if (input.value != document.getElementById('password').value) {
        input.setCustomValidity('The two passwords must match.');
    } else {
        // input is valid -- reset the error message
        input.setCustomValidity('');
   }
}
	function showRegistration(){
		document.getElementById("overlay").style.display = "block";
		document.getElementById("register-dialog").style.display = "block";
	}
	function closeRegistration(){
		document.getElementById("overlay").style.display = "none";
		document.getElementById("register-dialog").style.display = "none";
	}
	function showNotification(){
		document.getElementById("overlay").style.display = "block";
		document.getElementById("notification-dialog").style.display = "block";
	}
	function closeNotification(){
		document.getElementById("overlay").style.display = "none";
		document.getElementById("notification-dialog").style.display = "none";
	}
</script>
</head>

<body<% if (request.getParameter("msg") != null) { out.print(" onload=\"showNotification()\""); } %>>

<div id="menu-wrapper">
	<div id="menu-center">
		<a href="index.jsp"><img alt="" src="images/whitelogo.png" /></a>
		<div class="register" onclick="showRegistration()">Register and Submit Poster</div>
		<form id="signin-form" method="post" action="NetworkServlet">
			<input name="emailAddress" type="email" placeholder="Email address"/>
			<input name="password" type="password" placeholder="Password"/>
			<input class="button" type="submit" value="Sign in"/>
			<input type="hidden" name="actionIndex" value="<% out.print(NetworkServlet.SIGNIN); %>"/>
		</form>
	</div>
</div>
<div id="content-wrapper">
	<div id="content-window">
		<div id="side-pane">
			<div class="welcome-title">Welcome to the Oklahoma Research Day Poster Submission System</div>
			<div class="welcome-message">Please login with the provided username and password fields</div>
		</div>
		<div id="content-pane">
			<div id="myCarousel" class="carousel slide">
			<!-- Carousel items -->
			<div class="carousel-inner">
				<div class="active item">
					<img src="images/ord1.jpg"/>
				</div>
				<div class="item">
					<img src="images/ord2.jpg"/>
				</div> 
				<div class="item">
					<img src="images/ord3.jpg"/>
				</div> 
				<div class="item">
					<img src="images/ord4.jpg"/>
				</div>                  
			</div>
			<!-- Carousel nav -->
			<a class="carousel-control left" data-slide="prev" href="#myCarousel">&lsaquo;</a>
			<a class="carousel-control right" data-slide="next" href="#myCarousel">&rsaquo;</a>
		</div>
		</div>
	</div>
</div>
<div id="footer-wrapper">
	<div id="menu-center">
		<img alt="" src="images/bwbroncho.png" /> 
		<div class="footer-content underlined">
			<a class="footer-link" href="http://uconnect.uco.edu">Uconnect</a>
			<a class="footer-link" href="https://www.dreamspark.com/">MSDN-AA [DreamSpark]</a>
			<a class="footer-link" href="http://learn.uco.edu">D2L</a>
			<a class="footer-link" href="http://cs2.uco.edu/~acm/">ACM Club</a>
		</div>
		<div class="footer-content">
			&copy; 2013 Department of Computer Science<br>
			100 North University Drive, Edmond, OK 73034 &middot; (405) 974-5717 <br>
			Designed by Stephen Staker
		</div>
	</div>
</div>
<div id="overlay"></div>
<div class="dialog" id="register-dialog">
	<form id="register-form" method="post" action="NetworkServlet?actionIndex=<% out.print(NetworkServlet.UPLOADPOSTER); %>" enctype="multipart/form-data">
			<img src="images/exit.png" onClick="closeRegistration()"/>
			<p>Email Address</p>
			<p><input type="email" placeholder="Email address" name="email"/></p>
			<p>Password</p>
			<p><input type="password" placeholder="Password" name="password"/></p>
			<p>Confirm Password</p>
			<p><input type="password" placeholder="Confirm password" name="confirm"/></p>
			<p>Poster File</p>
			<p><input type="file" name="uploadPoster" id="file"/></p>
			<p><input class="button" type="submit" value="Register"/>		</p>
	</form>
</div>
<div id="notification-dialog">
	<div id="notification-area"><p><%
	
if (request.getParameter("msg") != null) {
	if (request.getParameter("msg").equals("bad_credentials")) {
		out.print("The email/password combination is incorrect! Please try again.");
	} else if (request.getParameter("msg").equals("no_session")) {
		out.print("You are not logged in!");
	} else if (request.getParameter("msg").equals("register_success")) {
		out.print("Registration was successful! Please login using your credentials.");
	} else if (request.getParameter("msg").equals("register_invalid_email")) {
		out.print("The email you entered is not a valid address! Please try again.");
	} else if (request.getParameter("msg").equals("register_unavailable_email")) {
		out.print("This email is already in use!");
	} else if (request.getParameter("msg").equals("register_invalid_password")) {
		out.print("The password you entered is not valid! Please try again. The password must be between 6 and 12 characters long.");
	} else if (request.getParameter("msg").equals("register_password_not_confirmed")) {
		out.print("You did not confirm your password! Please try again. The password must be between 6 and 12 characters long.");
	} else if (request.getParameter("msg").equals("register_password_not_match")) {
		out.print("The passwords you entered do not match! Please try again.");
	} else if (request.getParameter("msg").equals("register_other_error")) {
		out.print("An unspecified error has occurred! Please try again.");
	} else if (request.getParameter("msg").equals("register_authentication_error")) {
		out.print("An unspecified authentication error has occurred! Please login and try uploading the file again.");
	} else if (request.getParameter("msg").equals("register_invalid_file")) {
		out.print("The file you submitted is not valid. Make sure it is one of the following types: .ppt, .pptx, .jpg, .png. Also, the maximum size is 1 MB.");
	}
}
			
%></p><p><input class="button" type="button" value="Ok" onclick="closeNotification()" /></p></div>
</div>
</body>

</html>