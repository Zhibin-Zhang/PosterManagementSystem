<%@ page session="false" %><%@ 
page import="ordappengine.*,java.util.ArrayList" %><%

// Anti-cache headers. see http://www.xyzws.com/JSPfaq/how-to-disable-browser-caching-for-a-specific-jsp/11
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

// Check if session exists and is valid
HttpSession session = request.getSession(false);
NetworkEndpoint endpoint = new NetworkEndpoint();

if (session != null) {
	if (session.getAttribute("token") != null) {
		endpoint.setBackendSessionToken((String)session.getAttribute("token"));
		BackendSession backendSession = endpoint.authenticateSession();
		
		if (backendSession != null) {
			if (!backendSession.isAdmin) {
		
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="css/ord.css" media="screen" rel="stylesheet" type="text/css" />
<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Roboto:300' rel='stylesheet' type='text/css'>
<title>Computer Science</title>
<script type="text/javascript">
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

<body>

<div id="menu-wrapper">
	<div id="menu-center">
		<a href="index.html"><img alt="" src="images/whitelogo.png" /></a>
	</div>
</div>
<div id="content-wrapper">
	<div id="content-window">
		<div id="side-pane">
			<div class="welcome-message">
				These are instructions to use our system. You do not know how to use the system so these are instructions to use the system.
			</div>		
		</div>
		<div id="content-pane">
			<p align="right"><b>Welcome <%= backendSession.emailAddress %> (<a href="NetworkServlet?actionIndex=<%= NetworkServlet.LOGOUT %>">Logout</a>)</b></p>
			<p align="right"><input type="button" class="button" value="Upload Poster" onclick="showRegistration()"></p>
			<ul>
<%

// Here is where the list of submissions should be output
ArrayList<Submission> submissions = backendSession.submissions;

%>
				<li>
					<div class="list-username">submission.ptx</div>
					<div class="list-filename">Submitted</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
				</li>
				<li>
					<div class="list-username">outposter.ptx</div>
					<div class="list-filename">Processing</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
				</li>
				<li>
					<div class="list-username">poster1.ptx</div>
					<div class="list-filename">Processing</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
				</li>
				<li>
					<div class="list-username">ord.pdf</div>
					<div class="list-filename">Submitted</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
				</li>
			</ul>
		</div>
	</div>
</div>
<div id="footer-wrapper">
	<div id="menu-center">
		<img alt="" src="images/bwbroncho.png" /> 
		<div class="footer-content underlined">
			<a class="footer-link" href="#">Uconnect</a>
			<a class="footer-link" href="#">MSDN-AA [DreamSpark]</a>
			<a class="footer-link" href="#">D2L</a>
			<a class="footer-link" href="#">ACM Club</a>
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
			Poster File<input type="file" name="uploadPoster" id="file"/>
			<input class="button" type="submit" value="Upload Poster"/>		
	</form>
</div>
</body>

</html><%
			} else {
				// User is not a normal user
				response.sendRedirect("/admin.jsp");
			}
		} else {
			// Session does not exist
			response.sendRedirect("/index.jsp?msg=no_session");
		}
	} else {
		// Token is not stored in HTTP session. Logout
		response.sendRedirect("/NetworkServlet?actionIndex=" + NetworkServlet.LOGOUT);
	}
} else {
	// Session does not exist
	response.sendRedirect("/index.jsp?msg=no_session");
}

%>